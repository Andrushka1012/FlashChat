package com.example.andrii.flashchat.data;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    public static List<Person> getPersons(int count){
        List<Person> list = new ArrayList<>();
        for (int i=0;i<count;i++){
            Person p = new Person("Andrii");
            list.add(p);
        }
        return list;
    }
    public static List<Message>getMessages(Person I){
        List<Message> list = new ArrayList<>();
        Message msg;
        Person person = new Person("Andruszka");
        msg = new Message("Hi",I,Message.MESSAGE_TEXT_TYPE);
        list.add(msg);
        msg = new Message("Kak dela?",I,Message.MESSAGE_TEXT_TYPE);
        list.add(msg);
        msg = new Message("Hi",person,Message.MESSAGE_TEXT_TYPE);
        list.add(msg);
        msg = new Message("zajebis",person,Message.MESSAGE_TEXT_TYPE);
        list.add(msg);

        return list;
    }


}
