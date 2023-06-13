package com.example.myapplication;

import com.google.firebase.Timestamp;

public class Message {
    private String sentBy;
    private String message;
    private Timestamp timestamp;

    public Message() {
    }

    public Message(String sentBy, String message, Timestamp timestamp) {
        this.sentBy = sentBy;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}