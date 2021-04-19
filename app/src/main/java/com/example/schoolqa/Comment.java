package com.example.schoolqa;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.io.Serializable;
@Parcel(analyze = Comment.class)
@ParseClassName("Comment")
public class Comment extends ParseObject{
    public static final String KEY_CONTENT ="comment_content";
    public static final String KEY_POST="postId";
    public static final String KEY_USER="author";
    public static final String KEY_IMAGE="image";
    public static final String KEY_CREATED="createdAt";

    //empty constructor needed by the Parceler library
    public Comment() {
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
        return getString(KEY_POST);
    }
    public void setPostId(String post){
        put(KEY_POST, post);
    }

}
