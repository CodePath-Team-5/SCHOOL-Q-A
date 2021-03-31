package com.example.schoolqa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchActivity extends AppCompatActivity {
    public static String tag = "SearchActivity";
    EditText et_user_input;
    ImageButton bttn_user_profile;
    ImageButton bttn_logout;
    RecyclerView recyclerView_postResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //link layout components
        et_user_input= findViewById(R.id.et_search_input_text);
        bttn_logout = findViewById(R.id.bttn_logout_button);
        bttn_user_profile = findViewById(R.id.bttn_profile_button);
        recyclerView_postResults= findViewById(R.id.rv_search_results);

        //Logout button clicked
        bttn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_logout_button();
            }
        });
        //Profile button clicked
        bttn_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_profile_button();
            }
        });

    }


    public void handle_search_button(View view) {
        //Search button clicked
        Log.d(tag,"Search button clicked");

    }
    private void handle_profile_button() {
        Log.d(tag,"Profile button clicked");
        //go to Profile activity
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void handle_logout_button() {
        Log.d(tag,"Logout button clicked");
        //logout account

        //exit app
        finish();
    }
}