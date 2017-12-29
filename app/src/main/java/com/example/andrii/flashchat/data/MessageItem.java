package com.example.andrii.flashchat.data;


import com.example.andrii.flashchat.data.DB.MessageDb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

public class MessageItem {

    private String msgID;
    private String text;
    private String senderId;
    private String recipient_id;
    private Date date;
    private int read;
    private int type;

    public MessageItem(String msgID, String text, String senderId, String recipient_id, Date date, int read, int type) {
        this.msgID = msgID;
        this.text = text;
        this.senderId = senderId;
        this.recipient_id = recipient_id;
        this.date = date;
        this.read = read;
        this.type = type;
    }

    public MessageItem(Message msg,Person recipient){
        msgID = msg.getID().toString();
        text = msg.getText();
        senderId = (msg.getFrom().getId());
        recipient_id = recipient.getId();
        date = new Date();
        read = 0;
        type = msg.getType();
    }

    public MessageItem(MessageDb msg){
        msgID = msg.getMsgID();
        text = msg.getText();
        senderId = msg.getSenderId();
        recipient_id = msg.getRecipient_id();
        date = msg.getDate();
        read = msg.getRead();
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

    public static List<MessageItem>convertToList(RealmResults<MessageDb> result){
        List<MessageItem> list = new ArrayList<>();
        for (MessageDb msg:result){
            MessageItem item = new MessageItem(msg);
            list.add(item);
        }
        return list;
    }


}
