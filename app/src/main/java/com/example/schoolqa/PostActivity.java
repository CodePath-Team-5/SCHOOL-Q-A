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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    public static String tag = "PostActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    TextView tv_title;
    TextView tv_question_content;
    TextView tv_author_name; //name of user who created the post
    TextView tv_post_timestamp;
    TextView tv_imageAttached;
    EditText et_user_comment;
    ImageView iv_comment_image;
    ImageView iv_question_image;
    TextView btnn_vote;
    ImageButton bttn_cancel_comment_image;
    ProgressBar progressBar;
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;
    int vote;
    Boolean is_upvote;
    Boolean comment_image_exist;
    Post post;
    List<Comment> commentList;
    Button btn_refresh;
    //SwipeRefreshLayout refreshLayout;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        //link layout components
        tv_title = findViewById(R.id.tv_post_title);
        tv_question_content = findViewById(R.id.tv_post_questionContent);
        tv_author_name = findViewById(R.id.tv_post_author_name);
        tv_post_timestamp = findViewById(R.id.tv_post_timestamp);
        tv_imageAttached = findViewById(R.id.imageAttached);
        iv_question_image = findViewById(R.id.iv_post_image);
        iv_comment_image = findViewById(R.id.iv_post_comment_image);
        et_user_comment = findViewById(R.id.et_post_userComment);
        btnn_vote = findViewById(R.id.bttn_post_upvote);
        bttn_cancel_comment_image = findViewById(R.id.bttn_post_cancle_comment_image);
        progressBar  = findViewById(R.id.post_progressBar);
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        is_upvote = false;
        comment_image_exist = false;

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        recyclerView_post_comments.setAdapter(commentAdapter);
        recyclerView_post_comments.setLayoutManager(new LinearLayoutManager(this));
        btn_refresh = findViewById(R.id.bttn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "refresh button clicked");
                setupPost(post);
            }
        });
        //set visibility of progress bar
        progressBar.setVisibility(View.VISIBLE);
        setupPost(post);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setupPost(Post post) {
        //set visibility of comment image & text & button
        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);
        //set text & image view
        tv_title.setText(post.getQuestion());
        tv_question_content.setText(post.getContent());
        tv_author_name.setText(post.getUser().getUsername());
        String createdAt = TimeFormatter.getTimeDifference(post.getCreatedAt().toString());
        tv_post_timestamp.setText(" - "+ createdAt);
        Log.d(tag,"Time create post: "+ createdAt);

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
        if (comment_image_exist ==true) {
            comment.setImage(new ParseFile(photoFile));
            comment_image_exist = false; //reset
        }
        comment.saveInBackground();// Immediately save the data asynchronously
        Log.d(tag,"Send button clicked - comment: "+input);

        Log.d(tag,"Update comment list ");
        //when sent...reset text box & image view
        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);
        et_user_comment.setText("");

        queryComments();
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


    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(tag, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                iv_comment_image.setImageBitmap(takenImage);
                iv_comment_image.setVisibility(View.VISIBLE);
                tv_imageAttached.setVisibility(View.VISIBLE);
                bttn_cancel_comment_image.setVisibility(View.VISIBLE);
                comment_image_exist = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen/Profile screen
    }



}