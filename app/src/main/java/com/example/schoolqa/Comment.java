package com.example.schoolqa;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject implements Cloneable{

    public static final String KEY_CONTENT ="comment_content";
    public static final String KEY_POST_ID="postId";
    public static final String KEY_USER="author";
    public static final String KEY_IMAGE="image";
    public static final String KEY_CREATED="createdAt";

    public Comment() {
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getContent(){
        return getString(KEY_CONTENT);
    }
    public void setContent(String content){
        put(KEY_CONTENT, content);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        ParseUser user = new ParseUser();
        try {
            user  = this.getParseObject(KEY_USER).fetchIfNeeded();
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }

        return user;
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public String getPostId(){
        return getString(KEY_POST_ID);
    }
    public void setPostId(String post){
        put(KEY_POST_ID, post);
    }



}
