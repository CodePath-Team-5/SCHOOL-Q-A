package com.example.schoolqa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import com.greenfrvr.hashtagview.HashtagView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.text.Html.*;

public class PostActivity extends AppCompatActivity implements CommentAdapter.OnCommentItemListener{

    private static final int PICK_IMAGE = 1;
    public static String tag = "PostActivity";
//    public  String CHANNEL_NAME;

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
    TextView bttn_favorite;
    ImageButton bttn_cancel_comment_image;
    HashtagView tv_tags;
    RecyclerView recyclerView_post_comments;
    CommentAdapter commentAdapter;
    int vote;
    Boolean is_upvote; // vote button flag
    Boolean is_favoritePost; //favourite button flag
    Boolean comment_image_exist;

    Post post;
    List<Comment> commentList;
    //SwipeRefreshLayout refreshLayout;
    FavoritePost favoritePost;

    private File photoFile;

    public String photoFileName = "photo.jpg";
    Uri imageURI;

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
        tv_tags = findViewById(R.id.tv_post_tags);
        iv_question_image = findViewById(R.id.iv_post_image);
        iv_comment_image = findViewById(R.id.iv_post_comment_image);
        et_user_comment = findViewById(R.id.et_post_userComment);
        btnn_vote = findViewById(R.id.bttn_post_upvote);
        bttn_favorite = findViewById(R.id.bttn_set_favorite);
        bttn_cancel_comment_image = findViewById(R.id.bttn_post_cancle_comment_image);
        //refreshLayout = findViewById(R.id.swipeContainer_inPost);
        recyclerView_post_comments = findViewById(R.id.rv_post_comments);

        is_upvote = false;
        is_favoritePost = false;
        comment_image_exist = false;


        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra("post"));


//        CHANNEL_NAME = "POST_"+post.getObjectId();

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
        //set post content
        tv_title.setText(post.getQuestion());
        tv_question_content.setText(post.getContent());
        tv_author_name.setText(post.getUser().getUsername());
        String createdAt = TimeFormatter.getTimeDifference(post.getCreatedAt().toString());
        tv_post_timestamp.setText(" - "+ createdAt);

        //setup hashtags

        if (post.getHashtags().isEmpty()==false) {
            Log.d(tag,"Tags: "+post.getHashtags().size());
            List<String> tags = new ArrayList<>();
            tags.addAll(post.getHashtags());

            tv_tags.setData(tags);
        }
        // setup vote
        int vote = post.getVote();
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
        // check & change color of Favorite Button if current post is user's favorite post
        isUser_FavoritePost();
        //query post's comment
        queryComments();
    }

    private void isUser_FavoritePost() {
        //check if current post is User's favorite post
        ParseQuery<FavoritePost> query =ParseQuery.getQuery(FavoritePost.class);
        query.include(FavoritePost.KEY_USER);
        query.whereEqualTo(FavoritePost.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(FavoritePost.KEY_POST,post);
        query.findInBackground(new FindCallback<FavoritePost>() {
            @Override
            public void done(List<FavoritePost> objects, ParseException e) {
                if(e==null)
                {
                    //find success
                    if(!objects.isEmpty())
                    {
                       //array not empty -> current post is user's favorite post

                        favoritePost= objects.get(0);
                        is_favoritePost = true;
                        setColor_FavoritePost_Bttn();//change color Favorite button
                    }

                }

            }
        });

    }

    private void setColor_FavoritePost_Bttn() {

        if (is_favoritePost==true)
        {
            Drawable drawable = bttn_favorite.getCompoundDrawables()[0];
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(bttn_favorite.getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
            bttn_favorite.setTextColor(Color.parseColor("#F44336")); //red
        }
        else
        {
            Drawable drawable = bttn_favorite.getCompoundDrawables()[0];
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(bttn_favorite.getContext(), R.color.White), PorterDuff.Mode.SRC_IN));
            bttn_favorite.setTextColor(Color.parseColor("#FFFFFF")); //white
        }
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
            Log.d(tag, photoFile.getAbsolutePath());
            comment.setImage(new ParseFile(photoFile));
           // photoFile.delete(); //delete file
            comment_image_exist = false; //reset
        }
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null)
                {
                    //save success
                    Toast.makeText(getApplicationContext(),"Sent!", Toast.LENGTH_SHORT).show();
                    //send push notification
                    JSONObject data = new JSONObject();
                    String message = ParseUser.getCurrentUser().getUsername() + " has just commented to your post: "+post.getQuestion();
                    try{
                        data.put("alert","New comment!");
                        data.put("title", message);
                    }
                    catch (JSONException e2)
                    {
                        throw new IllegalArgumentException("Unexpected parsing error", e2);
                    }

                    //send notification
//                    ParsePush push = new ParsePush();
//                    push.setChannel(CHANNEL_NAME);
//
//                    push.setData(data);
//                    push.sendInBackground();
                    //query comments
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


    public static String getFilePathFromContentUri(Uri contentUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        //Yi 
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;

    }

    public void handle_vote_button(View view) {
        //Vote button clicked
        Log.d(tag,"Vote button clicked");

        vote = post.getVote();

        if (is_upvote==false)
        {
            is_upvote = true;
            vote++;
            post.setVote(vote);
            //change button color
            Drawable drawable = btnn_vote.getCompoundDrawables()[0];
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(btnn_vote.getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
            btnn_vote.setTextColor(Color.parseColor("#F44336")); //red
            Toast.makeText(this,"Upvote!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            is_upvote = false;
            vote--;
            post.setVote(vote);
            //change button color
            Drawable drawable = btnn_vote.getCompoundDrawables()[0];
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(btnn_vote.getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN));
            btnn_vote.setTextColor(Color.parseColor("#FFFFFF")); //white

            Toast.makeText(this,"Downvote!", Toast.LENGTH_SHORT).show();
        }
        btnn_vote.setText(String.valueOf(vote));
        post.saveInBackground();// Immediately save the data asynchronously

    }
    public void handle_favourite_button(View view) {
        //Vote button clicked
        Log.d(tag,"Favorite button clicked");

        if (is_favoritePost==false)
        {
            //set favorite
            is_favoritePost = true;
            favoritePost = new FavoritePost();
            favoritePost.setUser(ParseUser.getCurrentUser());
            favoritePost.setPost(post);
            favoritePost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null)
                    {
                        //save sucess => change button color & pop Toast to update user
                        setColor_FavoritePost_Bttn();
                        Toast.makeText(getApplicationContext(),"Add to My Favorite", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error.Please try again later!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else
        {
            //set NOT favorite
            is_favoritePost = false;
            favoritePost.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    //delete success
                    if (e==null)
                    {
                        setColor_FavoritePost_Bttn();
                        Toast.makeText(getApplicationContext(),"Remove from My Favourite", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Error.Please try again later!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
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
                if (!link.isEmpty())
                {
                    //String hyperlink = "<a href="+link+">"+link+"</a>";

                    // user entered a link
                    Comment comment = new Comment();
                    comment.setContent(link);
                    Log.d(tag,"Send button clicked - comment: "+link);
                    comment.setUser(ParseUser.getCurrentUser());
                    comment.setPostId(post.getObjectId());
                    comment.set_isURL(true);
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null)
                            {
                                //save success
                                queryComments(); //refresh - query comments

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Fail to attach hyperlink. Please try again later!",Toast.LENGTH_LONG).show();
                            }
                            et_hyberlink.setText(""); //reset
                            popupWindow.dismiss();
                        }
                    });

//                    et_user_comment.setMovementMethod(LinkMovementMethod.getInstance());
//                    et_user_comment.append(Html.fromHtml(hyperlink));

                }
                else
                {
                    //user not entered anything
                    popupWindow.dismiss();
                }

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
/*
    public void insert_image(View view) {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);

        photoFile2 = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(PostActivity.this, "com.codepath.fileprovider", photoFile2);
        gallery.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (gallery.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
        }

    }

 */
//    public void insert_image(View view) {
//    // Create intent for picking a photo from the gallery
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//        // So as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            // Bring up gallery to select a photo
//            startActivityForResult(intent, PICK_IMAGE);
//        }
//    }


}