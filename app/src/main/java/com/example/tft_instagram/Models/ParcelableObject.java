package com.example.tft_instagram.Models;

import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
public class ParcelableObject {
    ParseUser user;
    Post post;

    public ParcelableObject(){}         //required empty constructor

    public ParcelableObject(Post post){
        this.post = post;
    }

    public ParcelableObject(ParseUser parseUser){
        user = parseUser;
    }

    public Post getPost(){
        return post;
    }
    public ParseUser getParseUser(){
        return user;
    }
}