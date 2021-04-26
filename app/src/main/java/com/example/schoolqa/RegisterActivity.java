package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    public static String tag = "RegisterActivity";
    EditText et_username;
    EditText et_password;
    EditText et_email;
    EditText et_major; //user's major/profession
    EditText et_year; //user's year of experience / year of graduation
    EditText et_intro;
    private TextView tvNameError,  tvPasswordError, tvColor;
    private CardView frameOne, frameTwo, frameThree, frameFour;
    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isRegistrationClickable = false;


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
        String email_id = et_email.getText().toString(),password = et_password.getText().toString();
        if(!email_id.contains("csueastbay.edu"))
        {
            Toast.makeText(RegisterActivity.this, "Sign up Failed.Only csueb email is valid / No duplicates allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        //String specialChars = "(.*[@,#,$,%].*$)";
        if(!(password.length() >= 8) || !(password.matches("(.*[A-Z].*)")) || !(password.matches("(.*[0-9].*)")) ){
            Toast.makeText(RegisterActivity.this, "Password has to meet given criteria", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseUser user = new ParseUser();
        user.setUsername(et_username.getText().toString());
        user.setPassword(et_password.getText().toString());
        user.setEmail(et_email.getText().toString());
        user.put("major_profession", et_major.getText().toString());
        user.put("year_experience", et_year.getText().toString());
        user.put("description", et_intro.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Sign up Failed. Enter correct credentials", Toast.LENGTH_SHORT).show();
                    Log.e(tag, "Issue with Sign up", e);
                    return;
                }

            }

        });

    }


    public void handle_cancel(View view) {
        Log.d(tag,"Cancel button clicked");
        finish();
    }
}