package com.example.schoolqa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ProfileActivity extends AppCompatActivity implements FavoritePostAdapter.OnFavoriteQuestionItemListener, PostAdaptor.OnQuestionItemListener, UserCommentAdapter.OnUserCommentItemListener {
    public static String tag = "ProfileActivity";
    public static final int EDIT_CODE = 5;
    public static final int POST_CODE = 24;
    TextView tv_username;
    TextView tv_major;
    TextView tv_year;
    TextView tv_intro;
    TextView tv_favorite_post_count;
    TextView tv_post_count;
    TextView tv_comment_count;
    ImageView iv_user_image;
    ImageButton bttn_back;
    ImageButton bttn_editProfile;
    RecyclerView recyclerView_User_favoritePosts;
    RecyclerView recyclerView_User_postResults;
    RecyclerView recyclerView_User_comments;
    ParseUser user;
    Context context;
    UserCommentAdapter commentAdapter;
    FavoritePostAdapter favoritePost_adapter;
    PostAdaptor adapter;
    List<Comment> user_comments;
    List<Post> userposts;
    List<Post> favorite_posts;
    Post deletedPost = null;
    Comment deletedComment = null;
    Comment user_cmt;
    Boolean isPostDelete; //ONLY true if user delete their post

    int postCount;
    int commentCount;
    int favPostCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //link layout components
        tv_username = findViewById(R.id.tv_profile_username);
        tv_major = findViewById(R.id.tv_profile_major);
        tv_year = findViewById(R.id.tv_profile_year);
        tv_intro = findViewById(R.id.tv_profile_description);
        tv_favorite_post_count = findViewById(R.id.tv_profile_favorite_post_count);
        tv_comment_count = findViewById(R.id.tv_profile_comment_count);
        tv_post_count = findViewById(R.id.tv_profile_post_count);
        iv_user_image = findViewById(R.id.iv_profile_image);
        recyclerView_User_favoritePosts = findViewById(R.id.rv_profile_UserFavoritePosts);
        recyclerView_User_postResults = findViewById(R.id.rv_profile_UserPosts);
        recyclerView_User_comments = findViewById(R.id.rv_user_comments);
        bttn_editProfile = findViewById(R.id.imageButton_profile_edit);

        //initialize var
        isPostDelete = false;
        postCount = 0;
        favPostCount=0;
        commentCount=0;

        //get intent
        Bundle extras = getIntent().getExtras();
        boolean guest =  extras.getBoolean("is_guest");


        //rv for favorite posts
        favorite_posts = new ArrayList<>();
        favoritePost_adapter = new FavoritePostAdapter(this, favorite_posts,this);
        recyclerView_User_favoritePosts.setAdapter(favoritePost_adapter);
        recyclerView_User_favoritePosts.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));


        //rv for user_posts
        userposts = new ArrayList<>();
        adapter= new PostAdaptor(this, userposts, this);
        recyclerView_User_postResults.setAdapter(adapter);
        recyclerView_User_postResults.setLayoutManager(new LinearLayoutManager(this));

        //rv for user_comments
        user_comments = new ArrayList<>();
        commentAdapter = new UserCommentAdapter(this,user_comments,this);
        recyclerView_User_comments.setAdapter(commentAdapter);
        recyclerView_User_comments.setLayoutManager(new LinearLayoutManager(this));

        bttn_back = findViewById(R.id.bttn_profile_back_button);
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_back_button(v);
            }
        });



        if (guest == true)
        {
            // Guest profile => invisible Edit button
            bttn_editProfile.setVisibility(View.INVISIBLE);
            user_cmt = (Comment) Parcels.unwrap(getIntent().getParcelableExtra("comment"));
            user  = user_cmt.getUser();
        }
        else {
            // User profile
            // visible Edit button
            user = ParseUser.getCurrentUser();
            bttn_editProfile.setVisibility(View.VISIBLE);

        }
        bind(); //set up layout view

    }


    private void bind() {
        //set up layout view

        tv_username.setText(user.getUsername());
        tv_major.setText("Major/Profession: "+user.get("major_profession").toString());
        tv_year.setText("Year of Graduation/ Experience: "+user.get("year_experience").toString());
        tv_intro.setText(user.get("description").toString());

        ParseFile image = user.getParseFile("user_image");

        if (image != null) {
            // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            Glide.with(this).load(image.getUrl()).into(iv_user_image);
        } else {
            Glide.with(this).load(context.getResources().getDrawable(R.drawable.ic_user)).into(iv_user_image);
        }

        queryFavoritePosts();
        queryPost();
        queryComments();
    }

    private void queryFavoritePosts() {
        ParseQuery<FavoritePost> query2 = ParseQuery.getQuery(FavoritePost.class);
        query2.include(FavoritePost.KEY_USER);
        query2.whereEqualTo(Post.KEY_USER, user);
        query2.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e==null)
                {
                    favPostCount = count;
                    tv_favorite_post_count.setText(""+count);}
            }
        });
        ParseQuery<FavoritePost> query = ParseQuery.getQuery(FavoritePost.class);
        query.include(FavoritePost.KEY_USER);
        query.whereEqualTo(FavoritePost.KEY_USER, user);
        query.setLimit(5);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<FavoritePost>() {
            @Override
            public void done(List<FavoritePost> objects, ParseException e) {
                if (e==null)
                {
                    favoritePost_adapter.clear();
                    favoritePost_adapter.addAll(objects);
                }
                else
                {
                    Log.i(tag,"Fail to query favorite post");
                }
            }
        });
    }
    private void queryPost() {
        ParseQuery<Post> query2 = ParseQuery.getQuery(Post.class);
        query2.include(Post.KEY_USER);
        query2.whereEqualTo(Post.KEY_USER, user);
        query2.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e==null)
                {
                    postCount = count;
                    tv_post_count.setText(""+count);}
            }
        });
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(5);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!= null){
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                else
                {
                    adapter.clear();
                    adapter.addAll(posts);
                    //allPost.addAll(posts);
                    //adaptor.notifyDataSetChanged();
                    //swipeRefreshLayout.setRefreshing(false);
                }


            }
        });

    }

    private void queryComments(){

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_USER, user);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e==null)
                {
                    commentCount = count;
                    tv_comment_count.setText(""+count);}
            }
        });


        ParseQuery<Comment> query2 = ParseQuery.getQuery(Comment.class);
        query2.include(Comment.KEY_USER);
        query2.whereEqualTo(Comment.KEY_USER, user);
        query2.addDescendingOrder(Comment.KEY_CREATED);
        query2.setLimit(5);
        query2.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if  (e!=null)
                {
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                else
                {
                    user_comments.addAll(comments);
                    commentAdapter.clear();
                    commentAdapter.addAll(comments);

                }

            }
        });
    }
    public void handle_edit_button(View view) {
        //Edit button clicked
        Log.d(tag,"Edit button clicked");

        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivityForResult(intent, EDIT_CODE);
    }
    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");

        //return to search screen
        if (isPostDelete == true)
        {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);
        }
        finish(); //go back to previous screen - Search screen
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EDIT_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                Toast.makeText(this,"Update profile sucessfully",Toast.LENGTH_SHORT).show();
                bind();
            }
            else if (resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this,"Fail to update your profile. Please try again later!",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == POST_CODE)
        {
            if (resultCode==RESULT_OK)
            {
                queryComments();
            }
        }
    }


    @Override
    public void onItemClick(View v, int position) {
        Post post = userposts.get(position);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );
        goToPostActivity(post);

    }
    @Override
    public void onFavoritePostlick(View v, int position) {
        Post post = favorite_posts.get(position);
        goToPostActivity(post);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );

    }

    private void goToPostActivity(Post post) {
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivityForResult(intent,POST_CODE);
    }


    public void handle_view_more_favorite_posts(View view) {
        Intent intent = new Intent(this, ShowUserFavoritePostListActivity.class);
        intent.putExtra("favPostCount", favPostCount);
        startActivity(intent);
    }
    public void handle_view_more_posts(View view) {
        Intent intent = new Intent(this, ShowUserPostListActivity.class);
        intent.putExtra("postCount", postCount);
        startActivity(intent);
    }
    public void handle_view_more_comments(View view) {

        Intent intent = new Intent(this, ShowUserCommentListActivity.class);
        intent.putExtra("commentCount", commentCount);
        startActivity(intent);
    }


    @Override
    public void onCommentClick(View v, int position) {
        //do nothing
    }
}