package com.tcn.sdk.springdemo.Model;

import java.io.Serializable;

public class dataspassmodel implements Serializable {

    public String Paywave;
    public double achargingprice;
    public double promoamt;
    public String promname;
    public int getDrawable;


    public dataspassmodel(String paywave, double achargingprice, double promoamt, String promname, int getDrawable) {
        Paywave = paywave;
        this.achargingprice = achargingprice;
        this.promoamt = promoamt;
        this.promname = promname;
        this.getDrawable = getDrawable;
    }
}