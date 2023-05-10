package com.example.myapplication;

import android.net.Uri;

public class User {
   private  String name ,address,email,number,photo;
    private long buildingcount;
   private boolean LookingForWork;

    public User (String name ,String address,String number,String email){
        this.buildingcount=0;
        this.photo="";
        this.name=name;
        this.email=email;
        this.address=address;
        this.number=number;
        this.LookingForWork=false;
    }

    public User(String name, String address, String email, String number, String photo, long buildingcount, boolean lookingForWork) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.number = number;
        this.photo = photo;
        this.buildingcount = buildingcount;
        this.LookingForWork = lookingForWork;
    }

    public User(int count) {
        this.buildingcount = count;
    }

    public long getCount() {
        return buildingcount;
    }

    public void setCount(int count) {
        this.buildingcount = count;
    }

    public boolean isLookingForWork() {
        return LookingForWork;
    }

    public void setLookingForWork(boolean lookingForWork) {
        LookingForWork = lookingForWork;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public User(){}


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
