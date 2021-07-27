package com.example.schoolqa;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Hashtag.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("BjxUjW3jGo300iPCHbUSXVEjXwY5T7a49d7hDmT6")
                .clientKey("u45uIQMJtNtSYcrAsf9EejaNuZKjT8JeFmp0rLnk")
                .server("https://parseapi.back4app.com")
                .build()
        );

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("GCMSenderId", "45077241050"); //sender id on firebase
        parseInstallation.saveInBackground();
    }
}
