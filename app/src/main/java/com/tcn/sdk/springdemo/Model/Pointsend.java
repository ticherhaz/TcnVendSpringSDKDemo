package com.tcn.sdk.springdemo.Model;


import java.util.Date;

public class Pointsend {


    int ID;
    int UserID;
    double Points;
    Date ExpireDate;
    int UserStatus;

    public Pointsend() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public double getPoints() {
        return Points;
    }

    public void setPoints(double points) {
        Points = points;
    }

    public Date getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(Date expireDate) {
        ExpireDate = expireDate;
    }

    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }
}
