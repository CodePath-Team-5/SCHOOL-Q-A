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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.text.Html.*;

public class PostActivity extends AppCompatActivity implements CommentAdapter.OnCommentItemListener{
    //test new branch
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
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;
    int vote;
    Boolean is_upvote;
    Boolean comment_image_exist;
    Post post;
    List<Comment> commentList;
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
        //refreshLayout = findViewById(R.id.swipeContainer_inPost);
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        is_upvote = false;
        comment_image_exist = false;

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList, this);
        recyclerView_post_comments.setAdapter(commentAdapter);
        recyclerView_post_comments.setLayoutManager(new LinearLayoutManager(this));

//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i(tag, "refreshing");
//                queryComments();
//                refreshLayout.setRefreshing(false);
//            }
//        });
        setupPost(post);
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

    }


    public void handle_send_button(View view) {
        //Send button clicked

        //get user comment & add comment to Post's list of comments
        String input = et_user_comment.getText().toString();
        Comment comment = new Comment();
        comment.setContent(input);
        Log.d(tag,"Send button clicked - comment: "+input);
        comment.setUser(ParseUser.getCurrentUser());
        comment.setPostId(post.getObjectId());
        if (comment_image_exist ==true) {
            comment.setImage(new ParseFile(photoFile));
            comment_image_exist = false; //reset
        }
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null)
                {
                    //save success
                    Toast.makeText(getApplicationContext(),"Sent!", Toast.LENGTH_SHORT).show();
                    queryComments();
                }
                else
                {
                    //fail to save
                    Toast.makeText(getApplicationContext(),"Fail to send comment. Please try again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });// Immediately save the data asynchronously


        Log.d(tag,"Update comment list ");
        //when sent...reset text box & image view
        iv_comment_image.setVisibility(View.INVISIBLE);
        tv_imageAttached.setVisibility(View.INVISIBLE);
        bttn_cancel_comment_image.setVisibility(View.INVISIBLE);
        et_user_comment.setText("");




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
        //Back button clicked
        Log.d(tag,"Back button clicked");
        Intent returnIntent= new Intent();
        setResult(RESULT_OK,returnIntent);
        finish(); //go back to previous screen - Search screen/Profile screen
    }


    @Override
    public void onItemClick(View v, int position) {
        Log.d(tag,"User click on other user image... guest_name: "+ commentList.get(position).getUser().getUsername());

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("is_guest", true);
        intent.putExtra("comment", Parcels.wrap(commentList.get(position)));
        startActivity(intent);

    }

    public void handle_refresh_cmts(View view) {
        queryComments();
    }


    public void start_hyperlink_window(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View hyperlinkView = inflater.inflate(R.layout.window_hyperlink, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(hyperlinkView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        Log.i(tag, "Hyperlink window opened");


        popupWindow.getContentView().findViewById(R.id.bttn_add_hyperlink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Add hyperlink button clicked");
                EditText et_hyberlink = popupWindow.getContentView().findViewById(R.id.et_hyperlink);
                String link = et_hyberlink.getText().toString();
                String hyperlink = "<a href="+link+">"+link+"</a>";
                et_user_comment.setMovementMethod(LinkMovementMethod.getInstance());
                et_user_comment.append(Html.fromHtml(hyperlink));

                popupWindow.dismiss();
            }
        });


        // dismiss the popup window when touched
        hyperlinkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void insert_image(View view) {
    }
}