package com.example.andrii.flashchat.data;

import java.util.Date;
import java.util.UUID;

public class Message {
    public static final int MESSAGE_TEXT_TYPE = 0;
    public static final int MESSAGE_IMAGE_TYPE = 1;

    private int type;
    private UUID mID;
    private String mText;
    private Person mFrom;
    private Date mWhen;
    private String mImagePath;

    public Message(String text, Person from, int type){
        this.type = type;
        if (type == MESSAGE_TEXT_TYPE){
            this.mText = text;
            mImagePath = null;

        }else{
            this.mText = null;
            mImagePath = text;
        }
        this.mFrom = from;
        mWhen = new Date();
        mID = UUID.randomUUID();
    }

    public String getText() {
        return mText;
    }

    public Person getFrom() {
        return mFrom;
    }

    public Date getWhen() {
        return mWhen;
    }

    public int getType() {
        return type;
    }

    public UUID getID() {
        return mID;
    }

    public void setID(UUID id) {
        this.mID = id;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }
    
}
