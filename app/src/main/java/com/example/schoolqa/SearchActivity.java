package com.example.schoolqa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.greenfrvr.hashtagview.HashtagView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity implements PostAdaptor.OnQuestionItemListener {
    public static String tag = "SearchActivity";
    public static final int COMPOSE_CODE = 29;
    EditText et_user_input;
    TextView tv_search;
    TextView tv_popular_tag_txt;
    ImageButton bttn_user_profile;
    ImageButton bttn_logout;
    ImageButton bttn_compose;
    RecyclerView recyclerView_postResults;
    PostAdaptor adaptor;
    List<Post> allpost;

    ImageButton bttn_bttn_search_button;
    String search_key;

    SwipeRefreshLayout refreshLayout;

    HashtagView tv_popular_tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //link layout components
        et_user_input = findViewById(R.id.et_search_input_text);
        bttn_logout = findViewById(R.id.bttn_logout_button);
        bttn_user_profile = findViewById(R.id.bttn_profile_button);
        bttn_compose = findViewById(R.id.bttn_compose_button);
        recyclerView_postResults = findViewById(R.id.rv_search_results);
        bttn_bttn_search_button = findViewById(R.id.bttn_search_button);
        tv_search = findViewById(R.id.tv_searchtresult);
        refreshLayout = findViewById(R.id.swipeContainer);
        tv_popular_tags = findViewById(R.id.tv_Tags);
        tv_popular_tag_txt = findViewById(R.id.tv_popularTag);


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
        adaptor = new PostAdaptor(this, allpost,  this);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(tag, "refreshing");
                queryPost();
                refreshLayout.setRefreshing(false);
            }
        });

        recyclerView_postResults.setAdapter(adaptor);
        recyclerView_postResults.setLayoutManager(new LinearLayoutManager(this));

        bttn_bttn_search_button = findViewById(R.id.bttn_search_button);


        queryHashtags();
        queryPost();


    }

    private void queryHashtags() {
        ParseQuery<Hashtag> query = ParseQuery.getQuery(Hashtag.class);
        query.addDescendingOrder("count");
        query.setLimit(5);
        query.findInBackground(new FindCallback<Hashtag>() {
            @Override
            public void done(List<Hashtag> objects, ParseException e) {
                if(e==null)
                {
                    //success
                    List<String> tags = new ArrayList<>();
                    tags.add("LatestPosts");
                    for(int i=0; i<objects.size();i++)
                    {
                        tags.add(objects.get(i).getKeyWord());
                    }
                    tv_popular_tags.setData(tags);
                    tv_popular_tags.addOnTagClickListener(new HashtagView.TagsClickListener() {
                        @Override
                        public void onItemClicked(Object item) {
                            String s = (String) item;
                            // query posts with related hashtag
                            queryPostByHashtagKey(s);

                        }
                    });
                }
                else
                {
                    Log.e(tag, "Issue with getting hashtags", e);
                    return;
                }
            }
        });
    }


    private void queryPost() {
        //query 15 latest post
        tv_search.setText("Recently Added Posts:");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(15);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!= null){
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                adaptor.clear();
                adaptor.addAll(posts);
                //allPost.addAll(posts);
                //adaptor.notifyDataSetChanged();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void queryPostBySearchKey(String search_key)
    {
        tv_search.setText("Search Results:");

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_VOTE);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e!= null){
                    Log.e(tag, "Issue with getting post", e);
                    return;
                }
                adaptor.clear();
                adaptor.addSearch(posts,search_key);
                //allpost.addAll(posts);
                //adaptor.notifyDataSetChanged();
                //swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void queryPostByHashtagKey(String hashtag)
    {
        if (hashtag == "LatestPosts")
        {
            //LatestPosts hashtag is clicked
            queryPost();
        }
        else {
            //query by hashtag
            tv_search.setText("Search Results:");
            // Search post by hashtag
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.include(Post.KEY_USER);
            query.addDescendingOrder(Post.KEY_VOTE);
            query.whereContains(Post.KEY_HASHTAG_LIST, hashtag);
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> posts, ParseException e) {
                    if (e != null) {
                        Log.e(tag, "Issue with getting post", e);
                        return;
                    }
                    adaptor.clear();
                    adaptor.addHashtagSearch(posts, hashtag);
                    //allpost.addAll(posts);
                    //adaptor.notifyDataSetChanged();
                    //swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    public void handle_search_button(View view) {
        //get input key
        String key = et_user_input.getText().toString();
        Log.d(tag,"Search button clicked - key input: "+key);
        queryPostBySearchKey(key);

    }
    private void handle_profile_button() {
        Log.d(tag,"Profile button clicked");
        //go to Profile activity
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("is_guest", false);
        startActivity(intent);
    }
    private void handle_compose_button() {
        Log.d(tag,"Compose button clicked");
        //go to Profile activity
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivityForResult(intent, COMPOSE_CODE);
    }
    private void handle_logout_button() {
        Log.d(tag,"Logout button clicked");
        //logout account
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==COMPOSE_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Post Created!", Toast.LENGTH_SHORT).show();
                queryPost();
            }
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Post post = allpost.get(position);
        Log.d(tag,"Item clicked: item "+position+ " - title: "+post.getQuestion() );
        Intent intent = new Intent(this, PostActivity.class);
        intent.putExtra("post", Parcels.wrap(post));
        startActivity(intent);

    }

}