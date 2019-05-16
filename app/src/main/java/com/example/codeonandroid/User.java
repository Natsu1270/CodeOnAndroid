package com.example.codeonandroid;

import android.net.Uri;

public class User {
    private String UID;
    private String name;
    private int exp;
    private String avatar;


    public User(String UID, String name, int exp, String avatar) {
        this.UID = UID;
        this.name = name;
        this.exp = exp;
        this.avatar = avatar;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public void setExp(int exp) {
        this.exp = exp;
    }


}
