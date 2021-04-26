package com.example.schoolqa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    TextView et_has8;
    TextView et_uppercase;
    TextView et_onenumber;
    ImageView frameOne, frameTwo, frameThree, frameFour;


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
        String email_id = et_email.getText().toString();
        String password = et_password.getText().toString();
        String name = et_username.getText().toString();
        String major = et_major.getText().toString();
        String year = et_year.getText().toString();

        if(!(email_id.contains("csueastbay.edu") || email_id.contains("@horizon.csueastbay.edu")))
        {
            Toast.makeText(RegisterActivity.this, "Sign up Failed. Only csueb email is valid", Toast.LENGTH_SHORT).show();
            return;
        }
        password_strength(password);
        if(!(password.length() >= 8) || !(password.matches("(.*[A-Z].*)")) || !(password.matches("(.*[0-9].*)"))){
            Toast.makeText(RegisterActivity.this, "Password has to meet criteria", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!check_empty(name,major,year))
            return ;

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
                    Toast.makeText(RegisterActivity.this, "Sign up Failed.Enter correct credentials/Duplicates not allowed", Toast.LENGTH_SHORT).show();
                    Log.e(tag, "Issue with Sign up", e);
                    return;
                }

            }

        });

    }

   public void password_strength(String password){
       et_has8 = findViewById(R.id.atleasteight);
       et_uppercase = findViewById(R.id.uppercase);
       et_onenumber = findViewById(R.id.onenumber);
       frameOne = findViewById(R.id.frameOne);
       frameTwo = findViewById(R.id.frameTwo);
       frameThree = findViewById(R.id.frameThree);
      // frameFour = findViewById(R.id.frameFour);
     if(!(password.length() >= 8)){
         frameOne.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
       }
     else {
         frameOne.setBackgroundTintList(getResources().getColorStateList(R.color.Green));
     }


       if(!(password.matches("(.*[A-Z].*)"))){
         frameTwo.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
     }
       else {
           frameTwo.setBackgroundTintList(getResources().getColorStateList(R.color.Green));
       }

     if(!(password.matches("(.*[0-9].*)"))){
         frameThree.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
     }
     else {
         frameThree.setBackgroundTintList(getResources().getColorStateList(R.color.Green));
     }

       return;
   }

   public boolean check_empty(String name, String major, String year){
        if(name.length()<=0) {
            Toast.makeText(RegisterActivity.this, "Name field empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(major.length()<=0) {
            Toast.makeText(RegisterActivity.this, "Enter your Major", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(year.length()<=0) {
            Toast.makeText(RegisterActivity.this, "Year field not entered", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
   }
    public void handle_cancel(View view) {
        Log.d(tag,"Cancel button clicked");
        finish();
    }
}