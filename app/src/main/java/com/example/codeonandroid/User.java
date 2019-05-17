package com.example.codeonandroid;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String UID;
    private String name;
    private int exp;
    private String avatar;
    private String email;


    public User(String name, int exp, String avatar) {
        this.name = name;
        this.exp = exp;
        this.avatar = avatar;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }



    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


}
