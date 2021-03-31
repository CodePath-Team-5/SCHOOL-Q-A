package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    public static String tag = "ProfileActivity";
    TextView tv_username;
    TextView tv_major;
    TextView tv_year;
    TextView tv_intro;
    TextView tv_post_count;
    ImageView iv_user_image;
    RecyclerView recyclerView_User_postResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //link layout components
        tv_username = findViewById(R.id.tv_profile_username);
        tv_major = findViewById(R.id.tv_profile_major);
        tv_year = findViewById(R.id.tv_profile_year);
        tv_intro = findViewById(R.id.tv_profile_description);
        tv_post_count = findViewById(R.id.tv_profile_post_count);
        iv_user_image = findViewById(R.id.iv_profile_image);
        recyclerView_User_postResults = findViewById(R.id.rv_profile_UserPosts);


    }

    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen
    }
}