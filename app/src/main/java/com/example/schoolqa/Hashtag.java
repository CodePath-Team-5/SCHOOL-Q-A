package com.example.schoolqa;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = Hashtag.class)
@ParseClassName("HashTag")
public class Hashtag extends ParseObject implements Cloneable {

    public static final String KEY_WORD ="keyword";
    public static final String KEY_COLOR="color";
    public static final String KEY_COUNT="count";
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_CREATED="createdAt";

    //empty constructor needed by the Parceler library
    public Hashtag() {
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getKeyWord(){
        return getString(KEY_WORD);
    }
    public void setKeyWord(String kw){
        put(KEY_WORD, kw);
    }

    public String getColor(){
        return getString(KEY_COLOR);
    }
    public void setColor(String color){
        put(KEY_COLOR, color);
    }

    public int getCount(){
        return getInt(KEY_COUNT);
    }
    public void setCount(int number){
        put(KEY_COUNT, number);
    }





}
