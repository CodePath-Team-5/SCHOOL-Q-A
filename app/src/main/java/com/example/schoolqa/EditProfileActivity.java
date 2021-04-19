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

        //link layout components
        et_username = findViewById(R.id.editText_edit_username);
        et_year = findViewById(R.id.editText_edit_year);
        et_intro = findViewById(R.id.editText_edit_intro);
        et_major = findViewById(R.id.editText_edit_major);
        et_password = findViewById(R.id.editText_edit_password);
        iv_profile_image = findViewById(R.id.iv__edit_profile_image);
        bttn_save = findViewById(R.id.button_edit_save);
        bttn_photo = findViewById(R.id.button_change_image);
        //set user 's current info in text box & image view
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

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(tag, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void handle_change_profile_image(View view) {
        //Change Profile Image button clicked
        Log.d(tag,"Edit Profile Image button clicked");
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intentsharing-files-with-api-24-or-highers#
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                iv_profile_image.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handle_save_button(View view) throws ParseException {
        //Save button clicked
        Log.d(tag,"Save button clicked");

        //username and passord cannot be empty
        if(et_username.getText().toString().isEmpty()){
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(et_password.getText().toString().isEmpty()){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //get user input
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(et_username.getText().toString());
        user.setPassword(et_password.getText().toString());
        user.put("major_profession", et_major.getText().toString());
        user.put("year_experience", et_year.getText().toString());
        user.put("description", et_intro.getText().toString());
        user.put("user_image",new ParseFile(photoFile));

        //update change on back4app
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(updatetag, "error while saving", e);
                }
                Log.i(updatetag, "save successful");
            }
        });
        finish();
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