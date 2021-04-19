package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    public static String tag = "PostActivity";
    TextView tv_title;
    TextView tv_question_content;
    TextView tv_author_name; //name of user who created the post
    EditText et_user_comment;
    ImageView iv_question_image;
    TextView btnn_vote;
    ProgressBar progressBar;
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;
    int vote;
    Boolean is_upvote;
    Post post;
    List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //link layout components
        tv_title = findViewById(R.id.tv_post_title);
        tv_question_content = findViewById(R.id.tv_post_questionContent);
        tv_author_name = findViewById(R.id.tv_post_author_name);
        iv_question_image = findViewById(R.id.iv_post_image);
        et_user_comment = findViewById(R.id.et_post_userComment);
        btnn_vote = findViewById(R.id.bttn_post_upvote);
        progressBar  = findViewById(R.id.post_progressBar);
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        is_upvote = false;

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        recyclerView_post_comments.setAdapter(commentAdapter);
        recyclerView_post_comments.setLayoutManager(new LinearLayoutManager(this));


        progressBar.setVisibility(View.VISIBLE);
        setupPost(post);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setupPost(Post post) {
        tv_title.setText(post.getQuestion());
        tv_question_content.setText(post.getContent());
        tv_author_name.setText(post.getUser().getUsername());

        vote = post.getVote();
        btnn_vote.setText(String.valueOf(vote));

        ParseFile image = post.getImage();
        if (image != null) {
            // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            Glide.with(this).load(image.getUrl()).into(iv_question_image);
        }
        else
        {
            Log.d(tag,"No image attached to the post");
        }


        queryComments();
    }

    private void queryComments() {
        progressBar.setVisibility(View.VISIBLE);
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Define our query conditions
        query.whereEqualTo("postId", post.getObjectId());
        query.addDescendingOrder(Post.KEY_CREATED);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> itemList, ParseException e) {
                if (e != null) {
                    Log.d("item", "Error: " + e.getMessage());

                }
                // Access the array of results here
                for (Comment comment:itemList) {
                    Log.i(tag, "Comment: " + comment.getContent());
                }
                commentAdapter.clear();
                commentAdapter.addAll(itemList);
            }
        });
        progressBar.setVisibility(View.GONE);
    }


    public void handle_send_button(View view) {
        //Send button clicked

        //get user comment & add comment to Post's list of comments
        String input = et_user_comment.getText().toString();
        Comment comment = new Comment();
        comment.setContent(input);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPostId(post.getObjectId());
        comment.saveInBackground();// Immediately save the data asynchronously
        Log.d(tag,"Send button clicked - comment: "+input);

        Log.d(tag,"Update comment list ");
        queryComments();
        et_user_comment.setText("");
        Toast.makeText(this,"Comment sent!", Toast.LENGTH_SHORT).show();


    }
    public void handle_vote_button(View view) {
        //Vote button clicked
        Log.d(tag,"Vote button clicked");

        if (is_upvote==false)
        {
            is_upvote = true;
            vote++;
            post.setVote(vote);
            btnn_vote.setTextColor(Color.parseColor("#F44336"));
            Toast.makeText(this,"Upvote!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            is_upvote = false;
            vote--;
            post.setVote(vote);
            btnn_vote.setTextColor(Color.parseColor("#000000"));
            Toast.makeText(this,"Downvote!", Toast.LENGTH_SHORT).show();
        }
        btnn_vote.setText(String.valueOf(vote));
        post.saveInBackground();// Immediately save the data asynchronously

    }
    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen/Profile screen
    }



}