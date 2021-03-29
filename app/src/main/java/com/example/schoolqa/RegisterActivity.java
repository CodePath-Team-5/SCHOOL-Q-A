package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    public static String tag = "RegisterActivity";
    EditText et_username;
    EditText et_password;
    EditText et_email;
    EditText et_major; //user's major/profession
    EditText et_year; //user's year of experience / year of graduation
    EditText et_intro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //link layout components
          et_username= findViewById(R.id.et_signup_username);
          et_password= findViewById(R.id.et_signup_password);
          et_email= findViewById(R.id.et_signup_email);
          et_major= findViewById(R.id.et_signup_major);
          et_year= findViewById(R.id.et_signup_yearGraduate);
          et_intro = findViewById(R.id.et_signup_intro);

    }

    public void handle_signUp(View view) {
        //Sign up button clicked
        Log.d(tag,"Signup button clicked");
        //Register account

        //Log-in user

    }

    public void handle_cancel(View view) {
        //Cancel button clicked
        Log.d(tag,"Cancel button clicked");
        finish();
    }
}