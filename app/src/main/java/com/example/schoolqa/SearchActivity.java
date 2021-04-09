package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    public static String tag = "SearchActivity";
    EditText et_user_input;
    ImageButton bttn_user_profile;
    ImageButton bttn_logout;
    ImageButton bttn_compose;
    RecyclerView recyclerView_postResults;
    PostAdaptor adaptor;
    List<Post> allpost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //link layout components
        et_user_input= findViewById(R.id.et_search_input_text);
        bttn_logout = findViewById(R.id.bttn_logout_button);
        bttn_user_profile = findViewById(R.id.bttn_profile_button);
        bttn_compose = findViewById(R.id.bttn_compose_button);
        recyclerView_postResults= findViewById(R.id.rv_search_results);

        //Logout button clicked
        bttn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_logout_button();
            }
        });
        //Profile button clicked
        bttn_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_profile_button();
            }
        });
        //Compose button clicked
        bttn_compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_compose_button();
            }
        });

        allpost = new ArrayList<>();
        adaptor = new PostAdaptor(this, allpost);

        recyclerView_postResults.setAdapter(adaptor);
        recyclerView_postResults.setLayoutManager(new LinearLayoutManager(this));

        queryPost();

    }


    private void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                adaptor.clear();
                adaptor.addAll(posts);
                //allPost.addAll(posts);
                //adaptor.notifyDataSetChanged();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void handle_search_button(View view) {
        //Search button clicked
        Log.d(tag,"Search button clicked");

    }
    private void handle_profile_button() {
        Log.d(tag,"Profile button clicked");
        //go to Profile activity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private void handle_compose_button() {
        Log.d(tag,"Compose button clicked");
        //go to Profile activity
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivity(intent);
    }
    private void handle_logout_button() {
        Log.d(tag,"Logout button clicked");
        //logout account

        //exit app
        finish();
    }

}