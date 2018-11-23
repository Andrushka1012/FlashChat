package com.example.andrii.flashchat.tools

import com.example.andrii.flashchat.data.SingletonConnection
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIDService : FirebaseInstanceIdService(){
    override fun onTokenRefresh() {
        super.onTokenRefresh()
    }
}