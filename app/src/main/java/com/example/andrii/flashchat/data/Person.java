package com.example.andrii.flashchat.data;

import android.graphics.Bitmap;

import java.util.UUID;

public class Person {
    private String id;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String email;
    private String gender;
    private String photoUrl;
    private Bitmap photoBitmap;

    public Person(String id, String name, String birthDate, String phoneNumber, String email, String gender, String photoUrl) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.photoUrl = photoUrl;
    }

    public Person(String mName, String birthDate, String phoneNumber, String email, String gender) {
        this.name = mName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        id = UUID.randomUUID().toString();
    }

    public Person(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;

    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String url) {
        this.photoUrl = url;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
