package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class EditProfileActivity extends AppCompatActivity {

    public static String tag = "EditProfileActivity";
    EditText et_username;
    EditText et_major;
    EditText et_year;
    EditText et_intro;
    EditText et_password;
    ImageView iv_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //link layout components
        et_username = findViewById(R.id.editText_edit_username);
        et_year = findViewById(R.id.editText_edit_year);
        et_intro = findViewById(R.id.editText_edit_intro);
        et_major = findViewById(R.id.editText_edit_major);
        et_password = findViewById(R.id.editText_edit_password);
        iv_profile_image = findViewById(R.id.iv__edit_profile_image);

        //set user 's current info in text box & image view
        initial_setup();
    }


    public void initial_setup()
    {
        //fill user current info to edit texts
        et_username.setText(ParseUser.getCurrentUser().getUsername());
        et_year.setText(ParseUser.getCurrentUser().getString("year_experience"));
        et_major.setText(ParseUser.getCurrentUser().getString("major_profession").toString());
        et_intro.setText(ParseUser.getCurrentUser().getString("description"));
        et_password.setText(ParseUser.getCurrentUser().getString("password"));

        //get user current image
        ParseFile image = ParseUser.getCurrentUser().getParseFile("user_image");

        if (image != null) {
            // Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            Glide.with(this).load(image.getUrl()).into(iv_profile_image);
        } else {
            Glide.with(this).load(getApplicationContext().getResources().getDrawable(R.drawable.ic_user)).into(iv_profile_image);
        }

    }
    public void handle_change_profile_image(View view) {
        //Change Profile Image button clicked
        Log.d(tag,"Edit Profile Image button clicked");
    }

    public void handle_save_button(View view) {
        //Save button clicked
        Log.d(tag,"Save button clicked");

        //get user input

        //update change on back4app
    }

    public void handle_back_button(View view) {
        //Back button clicked
        Log.d(tag,"Back button clicked");
        finish(); //go back to previous screen - Search screen
    }

    public void handle_cancel_button(View view) {
        //Cancel button clicked
        Log.d(tag,"Cancel button clicked");
        finish(); //go back to previous screen - Search screen
    }


}