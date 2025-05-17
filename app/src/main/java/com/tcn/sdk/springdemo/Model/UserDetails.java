package com.tcn.sdk.springdemo.Model;

import java.io.Serializable;

public class UserDetails implements Serializable {


    int id;
    int UserID;
    double Points;
    String ExpireDate;
    int UserStatus;
    String FirstName;
    String LastName;
    PointsModel mypoints;
    UserName myuser;

    public UserDetails(PointsModel pointsModel, UserName userName) {

        this.mypoints = pointsModel;
        this.myuser = userName;


        UserID = pointsModel.getUserID();
        Points = pointsModel.getPoints();
        ExpireDate = pointsModel.getExpireDate();
        UserStatus = pointsModel.getUserStatus();
        FirstName = userName.getFirstName();
        LastName = userName.getLastName();
        id = pointsModel.getID();

    }

    public UserDetails() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PointsModel getMypoints() {
        return mypoints;
    }

    public void setMypoints(PointsModel mypoints) {
        this.mypoints = mypoints;
    }

    public UserName getMyuser() {
        return myuser;
    }

    public void setMyuser(UserName myuser) {
        this.myuser = myuser;
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

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
