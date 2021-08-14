package com.example.tft_instagram.Models;

import android.text.format.DateUtils;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_BODY = "body";

    public Comment(){}

    /*public String getFormattedTimestamp(){
        return getRelativeTimeAgo(getCreatedAt().toString());
    }

    //Helper method to get timestamp:
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }*/

    public void setBody(String body){ put(KEY_BODY, body); }

    public String getBody(){ return getString(KEY_BODY); }

    public void setPost(Post post){ put(KEY_POST, post); }

    public String getPost(){ return getString(KEY_POST); }

    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public ParseUser getUser(){ return getParseUser(KEY_USER); }
}