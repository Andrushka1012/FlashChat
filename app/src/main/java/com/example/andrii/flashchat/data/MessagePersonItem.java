package com.example.andrii.flashchat.data;

import com.example.andrii.flashchat.data.DB.MessageDb;
import com.example.andrii.flashchat.data.DB.UserNamesBd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MessagePersonItem extends MessageItem{
    private String name;

    public MessagePersonItem(String msgID, String text, String senderId, String recipient_id, Date date, int read, int type, String name) {
        super(msgID, text, senderId, recipient_id, date, read, type);
        this.name = name;
    }

    public MessagePersonItem(Message msg, Person recipient, String name) {
        super(msg, recipient);
        this.name = name;
    }

    public MessagePersonItem(MessageDb msg, String name) {
        super(msg);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<MessagePersonItem> convertToList(RealmResults<MessageDb> resultSender, RealmResults<MessageDb> resultRecipient, String userId){
        List<MessagePersonItem> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        for (MessageDb msgS:resultSender){
            MessagePersonItem item;
            String id = msgS.getSenderId();
            boolean founded = false;
            for (MessageDb msgR:resultRecipient){
                if (msgR.getRecipient_id().equals(id) && !id.equals(userId)){
                    founded = true;
                    if (msgS.getDate().after(msgR.getDate())){
                        item = new MessagePersonItem(msgS.getMsgID(),msgS.getText(),msgS.getSenderId(),userId,msgS.getDate(),msgS.getType(),msgS.getRead(),null);
                        UserNamesBd unbd = realm.where(UserNamesBd.class)
                                .equalTo("userId",item.getSenderId())
                                .findFirst();
                        String name = unbd == null?null:unbd.getName();
                        item.setName(name);
                        list.add(item);
                    }else{
                        item = new MessagePersonItem(msgR.getMsgID(),msgR.getText(),msgR.getRecipient_id(),userId,msgR.getDate(),msgR.getType(),msgR.getRead(),null);
                        UserNamesBd unbd = realm.where(UserNamesBd.class)
                                .equalTo("userId",item.getSenderId())
                                .findFirst();
                        String name = unbd == null?null:unbd.getName();
                        item.setName(name);
                        list.add(item);
                    }
                }
            }
            if (!founded){
                if (!msgS.getSenderId().equals(userId)){
                    item = new MessagePersonItem(msgS.getMsgID(),msgS.getText(),msgS.getSenderId(),userId,msgS.getDate(),msgS.getType(),msgS.getRead(),null);
                    UserNamesBd unbd = realm.where(UserNamesBd.class)
                            .equalTo("userId",item.getSenderId())
                            .findFirst();
                    String name = unbd == null?null:unbd.getName();
                    item.setName(name);
                    list.add(item);
                }
            }
        }
        for (MessageDb msgR:resultRecipient){
            boolean founded = false;
            String id = msgR.getRecipient_id();
            for (MessageDb msgS:resultSender){
                if (msgS.getSenderId().equals(id) && !id.equals(userId)){
                    founded = true;
                }
            }
            if (!founded){
                if (!msgR.getRecipient_id().equals(userId)){
                    MessagePersonItem item = new MessagePersonItem(msgR.getMsgID(),msgR.getText(),msgR.getRecipient_id(),userId,msgR.getDate(),msgR.getType(),msgR.getRead(),null);
                    UserNamesBd unbd = realm.where(UserNamesBd.class)
                            .equalTo("userId",item.getSenderId())
                            .findFirst();
                    String name = unbd == null?null:unbd.getName();
                    item.setName(name);
                    list.add(item);
                }
            }
        }
        realm.close();
        return list;
    }


}
