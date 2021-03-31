package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public static String tag = "LoginActivity";
    EditText et_username;
    EditText et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //link layout components
        et_password = findViewById(R.id.et_login_password);
        et_username = findViewById(R.id.et_login_username);

    }

    public void handle_login(View view) {
        //when Login button clicked
        Log.d(tag,"Login button clicked");
        //check user

        //go to Search activity
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();

    }

    public void handle_signUp(View view) {
        //when Sign Up button clicked
        Log.d(tag,"Signup button clicked");

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}