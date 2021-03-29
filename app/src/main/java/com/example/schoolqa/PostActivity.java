package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {

    public static String tag = "PostActivity";
    TextView tv_title;
    TextView tv_question_content;
    TextView tv_author_name; //name of user who created the post
    EditText et_user_comment;
    ImageView iv_question_image;
    RecyclerView recyclerView_post_comments;
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

    }

    public void handle_send_button(View view) {
        //Send button clicked
        Log.d(tag,"Send button clicked");

        //get user comment & add comment to Post's list of comments

    }
    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen/Profile screen
    }


}