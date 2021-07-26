package com.example.schoolqa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fenlisproject.hashtagedittext.HashTagEditText;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComposeActivity extends AppCompatActivity {
    public static String TAG = "ComposeActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1042;
    EditText et_question_title;
    EditText et_question_content;
    Button btn_compose_addPhoto_button;
    Button btn_compose_submit_button;
    ImageView iv_compose_image;
    Button btn_compose_cancel_button;
    File photo_file;
    String photo_file_name = "photo.jpg";
    HashTagEditText et_hashtag;
    List<String> hastags;
    Boolean containHashtag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        et_question_title = findViewById(R.id.et_compose_title);
        et_question_content = findViewById(R.id.et_compost_questionContent);
        btn_compose_addPhoto_button = findViewById(R.id.bttn_compose_addPhoto_button);
        btn_compose_submit_button = findViewById(R.id.bttn_compose_submit_button);
        iv_compose_image = findViewById(R.id.iv_compose_image);
        btn_compose_cancel_button = findViewById(R.id.bttn_compose_cancel_button);
        et_hashtag = findViewById(R.id.et_compose_hastags);

        hastags = new ArrayList<>();
        containHashtag = false;



    }

    public void handle_add_image_button(View view) {
        //Add image button clicked
        Log.d(TAG,"Add image button clicked");

        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photo_file = getPhotoFileUri(photo_file_name);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider", photo_file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photo_file.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                iv_compose_image.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void handle_submit_button(View view) {
        // Submit button clicked
        Log.d(TAG,"Submit button clicked");
        String question_title = et_question_title.getText().toString();
        String question_content = et_question_content.getText().toString();
        if (question_title.isEmpty() || question_content.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (photo_file == null || iv_compose_image.getDrawable() == null) {
//            Toast.makeText(ComposeActivity.this,"There is no image!", Toast.LENGTH_SHORT).show();
//            return;
//        }
        checkHashtags();
        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(question_title, question_content, currentUser, photo_file);
    }
    private void checkHashtags() {

        if (et_hashtag.getValues().isEmpty() )
        {
            Log.d(TAG,"No hastags added");
            containHashtag = false;
            return;
        }
        containHashtag = true;

        hastags.addAll(et_hashtag.getValues());

        //See if hashtags exists on database or not
        for (int i = 0; i< hastags.size(); i++)
        {
            final String my_tag = hastags.get(i);
            Log.d(TAG,"Tag: "+ my_tag);
            ParseQuery<Hashtag> query = ParseQuery.getQuery(Hashtag.class);
            query.whereEqualTo(Hashtag.KEY_WORD, my_tag);
            query.findInBackground(new FindCallback<Hashtag>() {
                @Override
                public void done(List<Hashtag> objects, ParseException e) {
                    if (e== null)
                    {
                        //sucess -> tag exist on database ->  update tag_count
                        if (objects.isEmpty())
                        {
                            saveHashtag(my_tag);
                        }
                        else {
                            Log.d(TAG, "-> Update tag_count: " + objects.get(0));
                            int count = objects.get(0).getCount() + 1;
                            objects.get(0).setCount(count);
                            objects.get(0).saveInBackground();
                        }
                    }
                    else
                    {
                        //fail -> tag does not exist on database -> save to database
                        saveHashtag(my_tag);
                    }
                }
            });
        }
    }

    private void saveHashtag(String s) {
        Log.d(TAG,"-> Save tag: "+ s + " to database");

        Hashtag hashtag = new Hashtag();
        hashtag.setKeyWord(s);
        hashtag.setCount(1);
        hashtag.saveInBackground();
    }


    private void savePost(String title, String content, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setQuestion(title);
        post.setContent(content);
        post.setUser(currentUser);
        if(photoFile!=null)
        {
            post.setImage(new ParseFile(photoFile));
        }
        if (containHashtag==true)
        {
            post.setHashtags(hastags);
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this,"Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Post save was successful");
                et_question_title.setText("");
                et_question_content.setText("");
                iv_compose_image.setImageResource(0);
                //return to search screen
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();

            }
        });
    }

    public void handle_cancel_button(View view) {
        // Cancel button clicked
        Log.d(TAG,"Cancel button clicked");
        finish();
    }
}