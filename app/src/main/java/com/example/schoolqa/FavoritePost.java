package com.example.schoolqa;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = FavoritePost.class)
@ParseClassName("FavoritePost")
public class FavoritePost extends ParseObject implements Cloneable {
    public static final String KEY_USER ="user";
    public static final String KEY_FAV_POST_ID="objectId";
    public static final String KEY_POST="post";


    //empty constructor needed by the Parceler library
    public FavoritePost() {
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    public Post getPost(){
        Post post = new Post();
        try {
            post  = this.getParseObject(KEY_POST).fetchIfNeeded();
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }

        return post;
    }
    public void setPost(Post post){
        put(KEY_POST, post);
    }

    public String getFavPostId(){
        return getString(KEY_FAV_POST_ID);
    }



}
