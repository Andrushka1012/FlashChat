package com.example.andrii.flashchat.data.Model;

public class SearchItem {
    private String name;
    private String id;
    private String imageSrc;
    private boolean online;

    public SearchItem(String name, String id, boolean online,String imageSrc) {
        this.name = name;
        this.id = id;
        this.online = online;
        this.imageSrc = imageSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}
