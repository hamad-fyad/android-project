package com.example.myapplication;

public class User {
    private  String name ,address,email,number,photo,uid;
    private long buildingcount;
    private boolean lookingForWork,lookingforservice;
    private double longitude,latitude;

    public User(String name, String address, String email, String number, String photoURL, long buildingcount, boolean lookingforwork, double longitude, double latitude, boolean lookingforservice, String uid) {
        this.name=name;
        this.address=address;
        this.email=email;
        this.number=number;
        this.photo=photoURL;
        this.buildingcount=buildingcount;
        this.lookingForWork=lookingforwork;
        this.longitude=longitude;
        this.latitude=latitude;
        this.lookingforservice=lookingforservice;
        this.uid=uid;
    }

    public User(String name, String address, String email, String number, String uid) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.number = number;
        this.photo = "";
        this.buildingcount = 0;
        this.lookingForWork = false;
        this.lookingforservice=false;
        this.uid=uid;
        this.latitude=1;
        this.longitude=1;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPhoto() {
        return photo;
    }

    public boolean isLookingforservice() {
        return lookingforservice;
    }

    public void setLookingforservice(boolean lookingforservice) {
        this.lookingforservice = lookingforservice;
    }
    public User(){}
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getBuildingcount() {
        return buildingcount;
    }

    public void setBuildingcount(long buildingcount) {
        this.buildingcount = buildingcount;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
        return lookingForWork;
    }

    public void setLookingForWork(boolean lookingForWork) {
        this.lookingForWork = lookingForWork;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }




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
