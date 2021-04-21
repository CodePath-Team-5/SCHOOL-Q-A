package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ComposeActivity extends AppCompatActivity {
    public static String TAG = "ComposeActivity";
    EditText et_question_title;
    EditText et_question_content;
    Button btn_compose_addPhoto_button;
    Button btn_compose_submit_button;
    ImageView iv_compose_image;
    Button btn_compose_cancel_button;


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
    }

    public void handle_add_image_button(View view) {
        //Add image button clicked
        Log.d(TAG,"Add image button clicked");
    }

    public void handle_submit_button(View view) {
        // Submit button clicked
        Log.d(TAG,"Submit button clicked");
        String question_title = et_question_title.getText().toString();
        String question_content = et_question_content.getText().toString();
        if (question_title.isEmpty() || question_content.isEmpty()) {
            Toast.makeText(ComposeActivity.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        savePost(question_title, question_content, currentUser);
    }

    private void savePost(String title, String content, ParseUser currentUser) {
        Post post = new Post();
        post.setQuestion(title);
        post.setContent(content);
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this,"Error while saving", Toast.LENGTH_SHORT).show();
                } else {
                Log.i(TAG, "Post save was successful");
                }
                et_question_title.setText("");
                et_question_content.setText("");
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