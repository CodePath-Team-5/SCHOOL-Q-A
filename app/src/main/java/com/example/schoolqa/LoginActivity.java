package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static String tag = "LoginActivity";
    EditText et_username;
    EditText et_password;
    Button bt_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser() != null)
        {
            goToSearchActivity();
        }
        //link layout components
        et_password = findViewById(R.id.et_login_password);
        et_username = findViewById(R.id.et_login_username);

        bt_login = findViewById(R.id.bttn_login_button);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "onClick login button");
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                System.out.println(password);
                System.out.println(username);

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e != null) {

                            Toast.makeText(LoginActivity.this, "Login Failed. Enter correct credentials", Toast.LENGTH_SHORT).show();
                            Log.e(tag, "Issue with login", e);
                            return;
                        }
                        goToSearchActivity();
                    }
                });
            }
        });
    }

    public void goToSearchActivity()
    {
        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
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