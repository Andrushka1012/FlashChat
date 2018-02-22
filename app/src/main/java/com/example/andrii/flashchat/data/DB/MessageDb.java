package com.example.andrii.flashchat.data.DB;

import com.example.andrii.flashchat.data.Message;
import com.example.andrii.flashchat.data.Person;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class MessageDb extends RealmObject{

    @PrimaryKey
    @Required
    private String msgID;
    private String text;
    private String senderId;
    private String recipient_id;
    private Date date;
    private int read;
    private int type;

    public MessageDb(){
        super();
    }

    public MessageDb(Message msg, Person recipient) {
        msgID = msg.getID().toString();
        text = msg.getText();
        senderId = (msg.getFrom().getId());
        recipient_id = recipient.getId();
        date = new Date();
        read = 0;
        type = msg.getType();
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(String recipient_id) {
        this.recipient_id = recipient_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return msgID + " " + text + " " + date + " " + type;
    }
}
