package com.example.myapplication;

public class Message {
    private String currentuser;
    private String sentBy;
    private String senderName;
    private String message;
    private long timestamp;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public String getCurrentuser() {
        return currentuser;
    }

    public void setCurrentuser(String currentuser) {
        this.currentuser = currentuser;
    }

    public void setSentBy(String senderId) {
        this.sentBy = senderId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Message(String currentuser, String senderId, String senderName, String message, long timestamp) {
        this.currentuser = currentuser;
        this.sentBy = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message(String senderId, String senderName, String text, long timestamp) {
        this.sentBy = senderId;
        this.senderName = senderName;
        this.message = text;
        this.timestamp = timestamp;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getText() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
