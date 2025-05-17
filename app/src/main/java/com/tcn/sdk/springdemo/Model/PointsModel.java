package com.tcn.sdk.springdemo.Model;

public class PointsModel {

    int ID;
    int UserID;
    double Points;
    String ExpireDate;
    int UserStatus;

    public PointsModel() {
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

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public int getUserStatus() {
        return UserStatus;
    }

    public void setUserStatus(int userStatus) {
        UserStatus = userStatus;
    }
}
