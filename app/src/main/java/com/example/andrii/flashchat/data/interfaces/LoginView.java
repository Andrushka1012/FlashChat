package com.example.andrii.flashchat.data.interfaces;

public interface LoginView {

    void showProgress(boolean show);
    void showEmailError(String error);
    void showPasswordError(String error);
}
