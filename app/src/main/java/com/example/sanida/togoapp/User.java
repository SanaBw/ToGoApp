package com.example.sanida.togoapp;

public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String firstName, String lastName, String email, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id=id;
    }

}
