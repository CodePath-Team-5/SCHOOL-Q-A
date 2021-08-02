package com.example.schoolqa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.greenfrvr.hashtagview.HashtagView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity implements CommentAdapter.OnCommentItemListener{

    public static String tag = "PostActivity";
    public  String CHANNEL_NAME;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    TextView tv_title;
    TextView tv_question_content;
    TextView tv_author_name;
    TextView tv_post_timestamp;
    TextView tv_imageAttached;
    EditText et_user_comment;
    ImageView iv_comment_image;
    ImageView iv_question_image;
    TextView btnn_vote;
    ImageButton bttn_cancel_comment_image;
    HashtagView tv_tags;
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;
    int vote;
    Boolean is_upvote;
    Boolean comment_image_exist;
    Post post;
    List<Comment> commentList;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tv_title = findViewById(R.id.tv_post_title);
        tv_question_content = findViewById(R.id.tv_post_questionContent);
        tv_author_name = findViewById(R.id.tv_post_author_name);
        tv_post_timestamp = findViewById(R.id.tv_post_timestamp);
        tv_imageAttached = findViewById(R.id.imageAttached);
        tv_tags = findViewById(R.id.tv_post_tags);
        iv_question_image = findViewById(R.id.iv_post_image);
        iv_comment_image = findViewById(R.id.iv_post_comment_image);
        et_user_comment = findViewById(R.id.et_post_userComment);
        btnn_vote = findViewById(R.id.bttn_post_upvote);
        bttn_cancel_comment_image = findViewById(R.id.bttn_post_cancle_comment_image);
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        is_upvote = false;
        comment_image_exist = false;

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        CHANNEL_NAME = "POST_"+post.getObjectId();

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList, this);
        recyclerView_post_comments.setAdapter(commentAdapter);
        recyclerView_post_comments.setLayoutManager(new LinearLayoutManager(this));

        setupPost(post);

    }

    private void setupPost(Post post) {

        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);

        tv_title.setText(post.getQuestion());
        tv_question_content.setText(post.getContent());
        tv_author_name.setText(post.getUser().getUsername());
        String createdAt = TimeFormatter.getTimeDifference(post.getCreatedAt().toString());
        tv_post_timestamp.setText(" - "+ createdAt);


        if (post.getHashtags().isEmpty()==false) {
            List<String> tags = new ArrayList<>();
            tags.addAll(post.getHashtags());

            tv_tags.setData(tags);
        }

        int vote = post.getVote();
        btnn_vote.setText(String.valueOf(vote));

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(iv_question_image);
        }
        else
        {
            Log.d(tag,"No image attached to the post");
        }


        queryComments();
    }

    private void queryComments() {


        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        query.whereEqualTo("postId", post.getObjectId());
        query.addDescendingOrder(Post.KEY_CREATED);

        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> itemList, ParseException e) {
                for (Comment comment:itemList) {
                    Log.i(tag, "Comment: " + comment.getContent());
                }
                commentAdapter.clear();
                commentAdapter.addAll(itemList);
            }
        });

    }


    public void handle_send_button(View view) {

        String input = et_user_comment.getText().toString();
        Comment comment = new Comment();
        comment.setContent(input);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPostId(post.getObjectId());
        if (comment_image_exist ==true) {
            comment.setImage(new ParseFile(photoFile));
            comment_image_exist = false;
        }
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null)
                {
                    Toast.makeText(getApplicationContext(),"Sent!", Toast.LENGTH_SHORT).show();
                    ParsePush push = new ParsePush();
                    push.setChannel(CHANNEL_NAME);
                    String message = ParseUser.getCurrentUser().getUsername() + " has just commented to your post: "+post.getQuestion();
                    push.setMessage(message);
                    push.sendInBackground();
                    queryComments();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Fail to send comment. Please try again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);
        et_user_comment.setText("");

    }
    public void handle_vote_button(View view) {
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

    public File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void launchCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                iv_comment_image.setImageBitmap(takenImage);
                iv_comment_image.setVisibility(View.VISIBLE);
                tv_imageAttached.setVisibility(View.VISIBLE);
                bttn_cancel_comment_image.setVisibility(View.VISIBLE);
                comment_image_exist = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                comment_image_exist = false;
            }
        }
    }

    public void handle_add_image(View view) {
        launchCamera();


    }
    public void handle_cancel_comment_image(View view) {
        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);
        comment_image_exist = false;
    }
    public void handle_back_button(View view) {
        Intent returnIntent= new Intent();
        setResult(RESULT_OK,returnIntent);
        finish(); //go back to previous screen - Search screen/Profile screen
    }


    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("is_guest", true);
        intent.putExtra("comment", Parcels.wrap(commentList.get(position)));
        startActivity(intent);

    }

    public void handle_refresh_cmts(View view) {
        queryComments();
    }
}