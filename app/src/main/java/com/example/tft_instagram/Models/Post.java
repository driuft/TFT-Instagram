package com.example.tft_instagram.Models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_LIKE_STATUS = "likeStatus";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_PROFILE_PIC = "profilePic";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public Post () {}

    public Comment addComment(String body, SaveCallback callback){
        Comment newComment = new Comment();
        newComment.setBody(body);
        newComment.setPost(this);
        newComment.setUser(ParseUser.getCurrentUser());
        newComment.saveInBackground();
        return newComment;
    }

    public void getComments(FindCallback<Comment> callback){
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(KEY_USER);
        query.whereEqualTo(Comment.KEY_POST, this);
        query.addDescendingOrder(Comment.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
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

    public ParseFile getUserProfilePic(){ return getParseUser(KEY_USER).getParseFile("profilePic"); }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public int getLikes(){
        return getInt(KEY_LIKES);
    }

    public void setLikes(int likes){ put(KEY_LIKES, likes); }

    public Boolean getLikeStatus(){ return getBoolean(KEY_LIKE_STATUS); }

    public void setLikeStatus(boolean status) { put(KEY_LIKE_STATUS, status); }

    public String getFormattedTimestamp(){
        return getRelativeTimeAlgo(getCreatedAt().toString());
    }

    //Helper method to get timestamp:
    public static String getRelativeTimeAlgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getRelativeTimeAgo(String dateStr) {
        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(dateStr).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i("Tweet", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}