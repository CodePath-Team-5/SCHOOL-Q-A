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
import java.util.List;
import java.util.prefs.Preferences;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ProfileActivity extends AppCompatActivity implements PostAdaptor.OnQuestionItemListener, UserCommentAdapter.OnUserCommentItemListener {
    public static String tag = "ProfileActivity";
    public static final int EDIT_CODE = 5;
    public static final int POST_CODE = 24;
    TextView tv_username;
    TextView tv_major;
    TextView tv_year;
    TextView tv_intro;
    TextView tv_post_count;
    TextView tv_comment_count;
    ImageView iv_user_image;
    ImageButton bttn_back;
    ImageButton bttn_editProfile;
    RecyclerView recyclerView_User_postResults;
    RecyclerView recyclerView_User_comments;
    ParseUser user;
    Context context;
    UserCommentAdapter commentAdapter;
    PostAdaptor adapter;
    List<Comment> user_comments;
    List<Post> userposts;
    Post deletedPost = null;
    Comment deletedComment = null;
    Comment user_cmt;
    Boolean isPostDelete; //ONLY true if user delete their post


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //link layout components
        tv_username = findViewById(R.id.tv_profile_username);
        tv_major = findViewById(R.id.tv_profile_major);
        tv_year = findViewById(R.id.tv_profile_year);
        tv_intro = findViewById(R.id.tv_profile_description);
        tv_comment_count = findViewById(R.id.tv_profile_comment_count);
        tv_post_count = findViewById(R.id.tv_profile_post_count);
        iv_user_image = findViewById(R.id.iv_profile_image);
        recyclerView_User_postResults = findViewById(R.id.rv_profile_UserPosts);
        recyclerView_User_comments = findViewById(R.id.rv_user_comments);
        bttn_editProfile = findViewById(R.id.imageButton_profile_edit);

        //initialize var
        isPostDelete = false;

        //get intent
        Bundle extras = getIntent().getExtras();
        boolean guest =  extras.getBoolean("is_guest");

        userposts = new ArrayList<>();
        adapter= new PostAdaptor(this, userposts, this);
        //rv for user_posts
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

            //set up swipe to delete feature for Post list
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(Post_simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView_User_postResults);

            //set up swipe to delete feature for Comment list
            ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(Comment_simpleCallback);
            itemTouchHelper2.attachToRecyclerView(recyclerView_User_comments);
        }
        bind(); //set up layout view

    }


    private void bind() {
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

        queryPost();
        queryComments();
    }

    private void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!= null){
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                for (Post post:posts){
                    Log.i(tag, "Post: "+post.getContent()+" user: "+ post.getUser().getUsername());
                }
                adapter.clear();
                adapter.addAll(posts);
                //allPost.addAll(posts);
                //adaptor.notifyDataSetChanged();
                //swipeRefreshLayout.setRefreshing(false);
                tv_post_count.setText(""+posts.size());
            }
        });

    }
    private  void queryComments2()
    {
        Log.i(tag,"In QUERY2");
        for (int i =0; i< userposts.size();i++)
        {
            ParseQuery<Comment> query = new ParseQuery("Comment");
            query.whereEqualTo(Comment.KEY_POST_ID, userposts.get(i).getObjectId());
            query.addDescendingOrder(Comment.KEY_CREATED);
            query.findInBackground(new FindCallback<Comment>() {
                @Override
                public void done(List<Comment> comments, ParseException e) {
                    if  (e!=null)
                    {
                        //fail
                        Log.e(tag, "Issue with getting comment", e);
                        return;
                    }
                    //sucess
                    for (Comment cmt:comments){
                        Log.i(tag, "Commment: "+cmt.getContent()+" -user: "+ cmt.getUser().getUsername());
                        user_comments.add(cmt);
                        commentAdapter.notifyItemInserted(user_comments.size()-1);
                    }

                }
            });
        }

    }
    private void queryComments()
    {

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_USER, user);
        query.addDescendingOrder(Comment.KEY_CREATED);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if  (e!=null)
                {
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                for (Comment cmt:comments){
                    Log.i(tag, "Commment: "+cmt.getContent()+" user: "+ cmt.getUser().getUsername());
                }
                commentAdapter.clear();
                commentAdapter.addAll(comments);
                tv_comment_count.setText(""+comments.size());
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
    ItemTouchHelper.SimpleCallback Comment_simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                //swipe left to delete
                deletedComment = new Comment();
                //save backup of deleted comment in case user want to undo their delete
                try {
                    deletedComment = (Comment) user_comments.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                user_comments.remove(position);
                commentAdapter.notifyItemRemoved(position);

                //reset post count:
                tv_comment_count.setText("" + user_comments.size());

                //making snackbar for undo delete
                Snackbar.make(recyclerView_User_comments, "Comment: " + deletedComment.getContent() + " is deleted!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //user click undo
                                Log.i(tag, "User click undo");
                                user_comments.add(position, deletedComment);
                                //update adapter post_list
                                commentAdapter.notifyItemInserted(position);
                                //reset post count:
                                tv_comment_count.setText("" + userposts.size());

                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    // Snackbar closed on its own

                                    // Delete comment officially if user does not intent to undo their action
                                    deletedComment.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                //sucessfully delete on back4app
                                                Log.i(tag, "Sucessfully delete comment on Back4App. Comment: " + deletedComment.getContent());
                                            } else {
                                                //fail to delete post
                                                Log.i(tag, "Fail to delete comment: " + e.getMessage());
                                            }
                                        }
                                    });

                                }
                            }
                        })
                        .show();

            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setActionIconTint(Color.BLACK)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 18)
                    .setSwipeLeftLabelColor(Color.BLACK)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    ItemTouchHelper.SimpleCallback Post_simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                //swipe left to delete
                deletedPost = new Post();

                //save backup of deleted Post in case user want to undo their delete
                try {
                    deletedPost = (Post) userposts.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                userposts.remove(position);
                adapter.notifyItemRemoved(position);

                //reset post count:
                tv_post_count.setText("" + userposts.size());

                //making snackbar for undo delete
                Snackbar.make(recyclerView_User_postResults, "Post: "+deletedPost.getQuestion()+" is deleted!", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //user click undo
                                Log.i(tag, "User click undo");
                                userposts.add(position, deletedPost);
                                //update adapter post_list
                                adapter.notifyItemInserted(position);
                                //reset post count:
                                tv_post_count.setText("" + userposts.size());

                            }
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                    // Snackbar closed on its own

                                    //delete comments that relate with post
                                    query_and_deleteComments(deletedPost.getObjectId());

                                    //unsubscribe post channel before delete post
                                    String postChannel = "POST_"+deletedPost.getObjectId();
                                    ParsePush.unsubscribeInBackground(postChannel);

                                    // Delete post officially if user does not intent to undo their action

                                    deletedPost.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null)
                                            {
                                                //sucessfully delete on back4app
                                                Log.i(tag, "Sucessfully delete post on Back4App. Post: " + deletedPost.getQuestion());

                                                //set isPostDelte to true -> trigger flag to refresh post list when go back to Search Activity
                                                isPostDelete = true;
                                            }
                                            else
                                            {
                                                //fail to delete post
                                                Log.i(tag, "Fail to delete post: "+e.getMessage());
                                            }
                                        }
                                    });

                                }
                            }
                        })
                        .show();

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ProfileActivity.this,R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setActionIconTint(Color.BLACK)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP,18)
                    .setSwipeLeftLabelColor(Color.BLACK)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public void query_and_deleteComments(String postId)
    {
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Define our query conditions
        query.whereEqualTo("postId", postId);
        query.addDescendingOrder(Post.KEY_CREATED);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> itemList, ParseException e) {
                if (e != null) {
                    Log.d("item", "Error: " + e.getMessage());

                }
                // Access the array of results here
                for (Comment comment:itemList) {
                    Log.i(tag, "Delete Comment: " + comment.getContent());
                    //removeComment_onList(comment);
                    comment.deleteInBackground();
                    queryComments();
                }
            }
        });
    }


    @Override
    public void onItemClick(View v, int position) {
        Post post = userposts.get(position);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivityForResult(intent,POST_CODE);

    }

    @Override
    public void onCommentClick(View v, int position) {
        Log.d(tag,"User click on other user image... guest_name: "+ user_comments.get(position).getUser().getUsername());
        if (user_comments.get(position).getUser().hasSameId(ParseUser.getCurrentUser()))
        {
            //same user - do nothing
        }
        else {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("is_guest", true);
            intent.putExtra("comment", Parcels.wrap(user_comments.get(position)));
            startActivity(intent);
        }
    }

    public void handle_refresh_comments(View view) {
        queryComments();
    }
}