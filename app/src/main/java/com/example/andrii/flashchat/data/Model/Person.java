package com.example.andrii.flashchat.data.Model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

@SuppressLint("ParcelCreator")
public class Person implements Parcelable {
    private String id;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String email;
    private String gender;
    private String photoUrl;
    private Bitmap photoBitmap;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

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
    //parcel part
    public Person(Parcel in){
        String[] data= new String[7];

        in.readStringArray(data);
        this.id= data[0];
        this.name= data[1];
        this.birthDate= data[2];
        this.phoneNumber= data[3];
        this.email= data[4];
        this.gender= data[5];
        this.photoUrl= data[6];
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.id,this.name,this.birthDate,this.phoneNumber,this.email,this.gender,this.photoUrl});
    }
    public static final Parcelable.Creator<Person> CREATOR= new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel parcel) {
            return new Person(parcel);
        }

        @Override
        public Person[] newArray(int i) {
            return new Person[i];
        }
    };

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Person)) return false;
        Person person = (Person) object;

        return id.equals(person.id)
                && name.equals(person.name)
                && birthDate.equals(person.birthDate)
                && phoneNumber.equals(person.phoneNumber)
                && email.equals(person.email)
                && gender.equals(person.gender)
                && photoUrl.equals(person.photoUrl);
    }
}
