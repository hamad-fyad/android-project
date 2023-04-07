package com.example.myapplication;

public class User {
    String name ,address,email,number;
    boolean LookingForWork;
    public User (String name ,String address,String number){
        this.name=name;
        this.address=address;
        this.number=number;
        this.LookingForWork=false;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public User(){}
    public void setLookingforwork(boolean lookingforwork) {
        LookingForWork = lookingforwork;
    }



    public boolean isLookingforwork() {
        return LookingForWork;
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
