package com.example.schoolqa;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(analyze = Post.class)
@ParseClassName("Post")
public class Post extends ParseObject implements Cloneable {

    public Post()
    {

    }
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static final String KEY_CONTENT ="content";
    public static final String KEY_USER="user";
    public static final String KEY_IMAGE="image";
    public static final String KEY_CREATED="createdAt";
    public static final String KEY_QUESTION="question";
    public static final String KEY_VOTE="vote";
    public static final String KEY_HASHTAG_LIST="hashtag_list";



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
        put(KEY_QUESTION, question);}

    public int getVote(){
        return getInt(KEY_VOTE);
    }
    public void setVote(int vote){
        put(KEY_VOTE, vote);
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

    public List<String> getHashtags(){
        JSONArray jsonArr =  getJSONArray(KEY_HASHTAG_LIST);
        List<String> listdata = new ArrayList();

        if (jsonArr != null) {
            for (int i=0;i<jsonArr.length();i++){
                try {
                    listdata.add(jsonArr.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return listdata;
    }

    public void  setHashtags(List<String> list)
    {
        addAllUnique(KEY_HASHTAG_LIST,list);
    }


}
