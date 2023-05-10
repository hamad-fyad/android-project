package com.example.myapplication;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Buildings extends ArrayList {

    private String Address;
    private double Price;

    private String Uid;
    private double Size;
    private String useruid;
    private ArrayList<String> picture;

    public Buildings( String address, double price, double size, String user,ArrayList<String> picture,String Uid) {
        this.picture=picture;
        this.Uid=Uid;
        this.Address = address;
        this.Price = price;
        this.Size = size;
        this.useruid = user;
    }

    public Buildings() {}


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public double getPrice() {
        return Price;
    }

    public ArrayList<String> getPicture() {
        return picture;
    }

    public void setPicture(ArrayList<String> picture) {
        this.picture = picture;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getSize() {
        return Size;
    }

    public void setSize(double size) {
        Size = size;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }
}
