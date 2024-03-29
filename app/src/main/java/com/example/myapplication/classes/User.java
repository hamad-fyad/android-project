package com.example.myapplication.classes;

import java.util.ArrayList;
import java.util.List;

public class User {
    private  String name ,address,email,number,photoURL,uid,role;
    private long buildingcount;
    private boolean lookingForWork,lookingforservice;
    private double longitude,latitude;
    private List<String> interestedUsers;
    private long sellTimeSum;
    private long soldCount;

    public User(){}

    public User(String name, String address, String email, String number, String uid) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.number = number;
        this.photoURL = "";
        this.buildingcount = 0;
        this.lookingForWork = false;
        this.lookingforservice=false;
        this.uid=uid;
        this.latitude=1;
        this.longitude=1;
        this.interestedUsers = new ArrayList<>();
        this.sellTimeSum=0;
        this.soldCount=0;
        this.role="user";

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public boolean isLookingforservice() {
        return lookingforservice;
    }

    public void setLookingforservice(boolean lookingforservice) {
        this.lookingforservice = lookingforservice;
    }

    public void setPhoto(String photo) {
        this.photoURL = photo;
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


    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public List<String> getInterestedUsers() {
        return interestedUsers;
    }

    public void setInterestedUsers(List<String> interestedUsers) {
        this.interestedUsers = interestedUsers;
    }
    public long getSellTimeSum() {
        return sellTimeSum;
    }

    public void setSellTimeSum(long sellTimeSum) {
        this.sellTimeSum = sellTimeSum;
    }

    public long getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(long soldCount) {
        this.soldCount = soldCount;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", number='" + number + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", uid='" + uid + '\'' +
                ", buildingcount=" + buildingcount +
                ", lookingForWork=" + lookingForWork +
                ", lookingforservice=" + lookingforservice +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", interestedUsers=" + interestedUsers +
                '}';
    }
}
