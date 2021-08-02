package com.example.schoolqa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

    public static String tag = "EditProfileActivity";
    public static String updatetag = "Update user info";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    EditText et_username;
    EditText et_major;
    EditText et_year;
    EditText et_intro;
    EditText et_password;
    ImageView iv_profile_image;
    Button bttn_save;
    Button bttn_photo;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        et_username = findViewById(R.id.editText_edit_username);
        et_year = findViewById(R.id.editText_edit_year);
        et_intro = findViewById(R.id.editText_edit_intro);
        et_major = findViewById(R.id.editText_edit_major);
        et_password = findViewById(R.id.editText_edit_password);
        iv_profile_image = findViewById(R.id.iv__edit_profile_image);
        bttn_save = findViewById(R.id.button_edit_save);
        bttn_photo = findViewById(R.id.button_change_image);
        initial_setup();

        bttn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_change_profile_image(v);
            }
        });

        bttn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handle_save_button(v);
                } catch (ParseException e) {
                    Log.e(updatetag, "update failed");
                }
            }
        });
    }


    public void initial_setup()
    {
        et_username.setText(ParseUser.getCurrentUser().getUsername());
        et_year.setText(ParseUser.getCurrentUser().getString("year_experience"));
        et_major.setText(ParseUser.getCurrentUser().getString("major_profession").toString());
        et_intro.setText(ParseUser.getCurrentUser().getString("description"));
        et_password.setText(ParseUser.getCurrentUser().getString("password"));

        ParseFile image = ParseUser.getCurrentUser().getParseFile("user_image");

        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(iv_profile_image);
        } else {
            Glide.with(this).load(getApplicationContext().getResources().getDrawable(R.drawable.ic_user)).into(iv_profile_image);
        }

    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void handle_change_profile_image(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                iv_profile_image.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handle_save_button(View view) throws ParseException {
        Log.d(tag,"Save button clicked");

        if(et_username.getText().toString().isEmpty()){
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(et_password.getText().toString().isEmpty()){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(et_username.getText().toString());
        user.setPassword(et_password.getText().toString());
        user.put("major_profession", et_major.getText().toString());
        user.put("year_experience", et_year.getText().toString());
        user.put("description", et_intro.getText().toString());
        if(photoFile!=null){
            user.put("user_image",new ParseFile(photoFile));
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){

                    Log.e(updatetag, "error while saving", e);
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED,returnIntent);
                    finish();
                }
                else
                {
                    Log.i(updatetag, "save successful");
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK,returnIntent);
                    finish();
                }
            }
        });

    }

    public void handle_back_button(View view) {
        finish(); //go back to previous screen - Search screen
    }

    public void handle_cancel_button(View view) {
        finish(); //go back to previous screen - Search screen
    }


}