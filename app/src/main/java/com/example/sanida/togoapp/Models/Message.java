package com.example.sanida.togoapp.Models;

public class Message {

    public Message(){}

    public Message(long id, User sender, User reciever, String date, String time, String content) {
        this.id = id;
        this.sender = sender;
        this.receiver = reciever;
        this.date = date;
        this.time = time;
        this.content = content;
    }

    long id;
    User sender,receiver;
    String date;
    String time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User reciever) {
        this.receiver = reciever;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content;
}
