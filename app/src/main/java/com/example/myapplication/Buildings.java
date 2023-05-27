package com.example.myapplication;


import java.util.ArrayList;

import java.util.Random;

public class Buildings  {

    private String Address;
    private double Price;
    private int number;
    private String Uid;

    public Buildings(String address, double buildingPrice, double buildingSize, String userId, ArrayList<String> imageUrls, String buildingUid, String selectedOption, String typeofbuilding) {
        this.picture=imageUrls;
        this.Uid=buildingUid;
        this.Address = address;
        this.Price = buildingPrice;
        this.Size = buildingSize;
        this.useruid = userId;
        Random random = new Random();
        this.number = random.nextInt();
        this.type=selectedOption;
        this.typeofbuilding=typeofbuilding;
    }

    public String getTypeofbuilding() {
        return typeofbuilding;
    }

    public void setTypeofbuilding(String typeofbuilding) {
        this.typeofbuilding = typeofbuilding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private double Size;
    private String useruid,typeofbuilding,type;
    private ArrayList<String> picture;


    public Buildings( String address, double price, double size, String user,ArrayList<String> picture,String Uid) {
        this.picture=picture;
        this.Uid=Uid;
        this.Address = address;
        this.Price = price;
        this.Size = size;
        this.useruid = user;
        Random random = new Random();
        this.number = random.nextInt();
    }

    public Buildings() {}

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
