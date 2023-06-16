package com.example.myapplication.classes;


import android.content.Context;
import android.location.Location;

import java.util.ArrayList;

import java.util.Date;
import java.util.Random;

public class Buildings  {
    private double Size;
    private String useruid,type;
    private ArrayList<String> picture;
    private String Address;
    private double Price;
    private int number;
    private String Uid;
    private Date postCreatedDate,sellDate;
    private boolean isSold;
    public Buildings() {}
    public Buildings( String address, double buildingPrice, double buildingSize, String userId, ArrayList<String> imageUrls, String buildingUid, String selectedOption) {

        this.picture = imageUrls;
        this.Uid = buildingUid;
        this.Address = address;
        this.Price = buildingPrice;
        this.Size = buildingSize;
        this.useruid = userId;
        Random random = new Random();
        this.number = random.nextInt();
        this.type = selectedOption;
        this.postCreatedDate = new Date();
        this.sellDate=null;
        this.isSold=false;
    }





    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }


    public Date getPostCreatedDate() {
        return postCreatedDate;
    }

    public void setPostCreatedDate(Date postCreatedDate) {
        this.postCreatedDate = postCreatedDate;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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
