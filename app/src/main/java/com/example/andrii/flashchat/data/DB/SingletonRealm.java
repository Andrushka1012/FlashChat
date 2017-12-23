package com.example.andrii.flashchat.data.DB;


import io.realm.Realm;

public class SingletonRealm {
    private static final SingletonRealm ourInstance = new SingletonRealm();
    private Realm mRealm = null;

    private SingletonRealm() {
    }
    public static SingletonRealm getInstance() {
        return ourInstance;
    }
/*
    public Realm getRealm (Context context){
        if (mRealm == null){
            mRealm = Realm.getInstance(context);
        }
        return mRealm;
    }
    public void close(){
        mRealm.close();
        mRealm = null;
    }*/

}
