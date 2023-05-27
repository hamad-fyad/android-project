package com.example.myapplication;



public class User {
    private  String name ,address,email,number,photo,uid;
    private long buildingcount;
    private boolean LookingForWork,lookingforservice;
    private double longitude,latitude;



    public User(String name, String address, String email, String number, String photoURL, long buildingcount, boolean lookingforwork, double longitude, double latitude, boolean lookingforservice, String uid) {
        this.name=name;
        this.address=address;
        this.email=email;
        this.number=number;
        this.photo=photoURL;
        this.buildingcount=buildingcount;
        this.LookingForWork=lookingforwork;
        this.longitude=longitude;
        this.latitude=latitude;
        this.lookingforservice=lookingforservice;
        this.uid=uid;
    }
    public User (String name , String address, String number, String email){
        this.buildingcount=0;
        this.photo="";
        this.name=name;
        this.email=email;
        this.address=address;
        this.number=number;
        this.LookingForWork=false;
        this.lookingforservice=false;
        this.latitude=0;
        this.longitude=0;
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

    public User(String name, String address, String email, String number, String uid) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.number = number;
        this.photo = "";
        this.buildingcount = 0;
        this.LookingForWork = false;
        this.uid=uid;
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
