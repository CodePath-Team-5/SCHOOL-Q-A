package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;

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
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        recyclerView_post_comments.setAdapter(commentAdapter);
        recyclerView_post_comments.setLayoutManager(new LinearLayoutManager(this));



        setupPost(post);
    }

    private void setupPost(Post post) {
        tv_title.setText(post.getQuestion());
        tv_question_content.setText(post.getContent());
        tv_author_name.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if (image != null) {
            // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            Glide.with(this).load(image.getUrl()).into(iv_question_image);
        }

        queryComments();
    }

    private void queryComments() {
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // Define our query conditions
        query.whereEqualTo("postId", post.getObjectId());
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

    }
    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen/Profile screen
    }


}