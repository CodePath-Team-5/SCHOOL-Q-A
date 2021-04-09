package com.example.schoolqa;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    private List<Comment> commentList;

    public Post() {
        this.commentList = new ArrayList<>();;
    }

    public static final String KEY_CONTENT ="content";
    public static final String KEY_USER="user";
    public static final String KEY_IMAGE="image";
    public static final String KEY_CREATED="createdAt";
    public static final String KEY_QUESTION="question";

    public String getContent(){
        return getString(KEY_CONTENT);
    }
    public void setContent(String content){
        put(KEY_CONTENT, content);
    }

    public String getQuestion(){
        return getString(KEY_QUESTION);
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

    private List<Comment> getCommentList() { return commentList; }
    private  void setCommentList(List<Comment> list)
    {
        commentList.addAll(list);
    }
}
