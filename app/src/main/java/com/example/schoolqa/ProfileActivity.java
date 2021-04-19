package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements PostAdaptor.OnQuestionItemListener {
    public static String tag = "ProfileActivity";
    TextView tv_username;
    TextView tv_major;
    TextView tv_year;
    TextView tv_intro;
    TextView tv_post_count;
    ImageView iv_user_image;
    ImageButton bttn_back;
    RecyclerView recyclerView_User_postResults;
    ParseUser user = ParseUser.getCurrentUser();
    Context context;
    PostAdaptor adapter;
    List<Post> userposts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //link layout components
        tv_username = findViewById(R.id.tv_profile_username);
        tv_major = findViewById(R.id.tv_profile_major);
        tv_year = findViewById(R.id.tv_profile_year);
        tv_intro = findViewById(R.id.tv_profile_description);
        tv_post_count = findViewById(R.id.tv_profile_post_count);
        iv_user_image = findViewById(R.id.iv_profile_image);
        recyclerView_User_postResults = findViewById(R.id.rv_profile_UserPosts);

        userposts = new ArrayList<>();
        adapter= new PostAdaptor(this, userposts, this);

        recyclerView_User_postResults.setAdapter(adapter);
        recyclerView_User_postResults.setLayoutManager(new LinearLayoutManager(this));

        bttn_back = findViewById(R.id.bttn_profile_back_button);
        bttn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_back_button(v);
            }
        });

        bind();
    }

    private void bind() {
        tv_username.setText(user.getUsername().toString());
        tv_major.setText(user.get("major_profession").toString());
        tv_year.setText(user.get("year_experience").toString());
        tv_intro.setText(user.get("description").toString());

        ParseFile image = user.getParseFile("user_image");

        if (image != null) {
            // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            Glide.with(this).load(image.getUrl()).into(iv_user_image);
        } else {
            Glide.with(this).load(context.getResources().getDrawable(R.drawable.ic_user)).into(iv_user_image);
        }

        queryPost();
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
    public void handle_edit_button(View view) {
        //Edit button clicked
        Log.d(tag,"Edit button clicked");

        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen
    }


    @Override
    public void onItemClick(int position) {
        Post post = userposts.get(position);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivity(intent);
    }
}