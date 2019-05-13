package com.example.codeonandroid;

public class User {
    private String name;
    private int exp;
    private int avatar;

    public User(String name, int exp, int avatar) {
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

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
}
