package com.example.schoolqa;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Comment extends ParseObject {
    public static final String KEY_CONTENT ="comment_content";
    public static final String KEY_USER="postID";
    public static final String KEY_IMAGE="image";
    public static final String KEY_CREATED="createdAt";

    public String getContent(){
        return getString(KEY_CONTENT);
    }
    public void setContent(String content){
        put(KEY_CONTENT, content);
    }

    public void setQuestion(String question){
        put(KEY_CONTENT, question);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
}
