package com.tcn.sdk.springdemo.Model;

public class MachineModel {

    int Id;
    int fid;
    String BannerURL;
    String BgURL;

    public MachineModel(int id, int fid, String bannerURL, String bgURL) {
        Id = id;
        this.fid = fid;
        this.BannerURL = bannerURL;
        this.BgURL = bgURL;
    }

    public MachineModel() {
    }

    public String getBgURL() {
        return BgURL;
    }

    public void setBgURL(String bgURL) {
        BgURL = bgURL;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getBannerURL() {
        return BannerURL;
    }

    public void setBannerURL(String bannerURL) {
        BannerURL = bannerURL;
    }
}