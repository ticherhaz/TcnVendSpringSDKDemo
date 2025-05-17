package com.tcn.sdk.springdemo.Model;

public class VoucherModel {
    public int Id;
    public int ProdID;
    public String Title;
    public int Status;
    public String StartDate;
    public String ExpireDate;
    public String ImageURL;

    public VoucherModel() {
    }

    public VoucherModel(int id, int prodID, String title, int status, String startDate, String expireDate, String imageURL) {
        Id = id;
        ProdID = prodID;
        Title = title;
        Status = status;
        StartDate = startDate;
        ExpireDate = expireDate;
        ImageURL = imageURL;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getProdID() {
        return ProdID;
    }

    public void setProdID(int prodID) {
        ProdID = prodID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getExpireDate() {
        return ExpireDate;
    }

    public void setExpireDate(String expireDate) {
        ExpireDate = expireDate;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
