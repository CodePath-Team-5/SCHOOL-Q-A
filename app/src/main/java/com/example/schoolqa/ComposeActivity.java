package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ComposeActivity extends AppCompatActivity {
    public static String tag = "ComposeActivity";
    EditText et_question_title;
    EditText et_question_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        et_question_title = findViewById(R.id.et_compose_title);
        et_question_content = findViewById(R.id.et_compost_questionContent);

    }

    public void handle_add_image_button(View view) {
        //Add image button clicked
        Log.d(tag,"Add image button clicked");

    }

    public void handle_submit_button(View view) {
        // Submit button clicked
        Log.d(tag,"Submit button clicked");

    }

    public void handle_cancel_button(View view) {
        // Cancel button clicked
        Log.d(tag,"Cancel button clicked");
        finish();
    }
}