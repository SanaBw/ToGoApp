package com.example.sanida.togoapp;

public class User {

    public String name;
    public String email;
    public String id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id=id;
    }

}
