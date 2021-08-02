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
import com.parse.ParsePush;
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

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photo_file = getPhotoFileUri(photo_file_name);

        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider", photo_file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photo_file.getAbsolutePath());
                iv_compose_image.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void handle_submit_button(View view) {

        String question_title = et_question_title.getText().toString();
        String question_content = et_question_content.getText().toString();
        if (question_title.isEmpty() || question_content.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        checkHashtags();
        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(question_title, question_content, currentUser, photo_file);
    }
    private void checkHashtags() {

        if (et_hashtag.getValues().isEmpty() )
        {
            containHashtag = false;
            return;
        }
        containHashtag = true;

        hastags.addAll(et_hashtag.getValues());

        for (int i = 0; i< hastags.size(); i++)
        {
            final String my_tag = hastags.get(i);
            ParseQuery<Hashtag> query = ParseQuery.getQuery(Hashtag.class);
            query.whereEqualTo(Hashtag.KEY_WORD, my_tag);
            query.findInBackground(new FindCallback<Hashtag>() {
                @Override
                public void done(List<Hashtag> objects, ParseException e) {
                    if (e== null)
                    {
                        if (objects.isEmpty())
                        {
                            saveHashtag(my_tag);
                        }
                        else {
                            int count = objects.get(0).getCount() + 1;
                            objects.get(0).setCount(count);
                            objects.get(0).saveInBackground();
                        }
                    }
                    else
                    {
                        saveHashtag(my_tag);
                    }
                }
            });
        }
    }

    private void saveHashtag(String s) {
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
                    Toast.makeText(ComposeActivity.this,"Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }
                et_question_title.setText("");
                et_question_content.setText("");
                iv_compose_image.setImageResource(0);
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();

            }
        });
    }

    public void handle_cancel_button(View view) {
        finish();
    }
}