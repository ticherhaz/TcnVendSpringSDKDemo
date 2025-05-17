package com.tcn.sdk.springdemo.Model;

import java.util.Date;

public class TransactionModel {
    public int TransID;
    public int UserID;
    public Date TransDate;
    public String FranID;
    public String MachineID;
    public String ProductIDs;
    public double Amount;
    public String PaymentType;
    public String PaymentMethod;
    public String FreePoints;
    public String Promocode;
    public String PromoAmt;
    public String Vouchers;
    public String PaymentID;
    public String PaymentStatus;
    public String Remarks;


    public TransactionModel() {
    }

    public String getVouchers() {
        return Vouchers;
    }

    public void setVouchers(String vouchers) {
        Vouchers = vouchers;
    }

    public int getTransID() {
        return TransID;
    }

    public void setTransID(int transID) {
        TransID = transID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public Date getmDate() {
        return TransDate;
    }

    public void setmDate(Date mDate) {
        this.TransDate = mDate;
    }

    public String getFranchiseID() {
        return FranID;
    }

    public void setFranchiseID(String franchiseID) {
        FranID = franchiseID;
    }

    public String getMachineID() {
        return MachineID;
    }

    public void setMachineID(String machineID) {
        MachineID = machineID;
    }

    public String getProductsIdes() {
        return ProductIDs;
    }

    public void setProductsIdes(String productsIdes) {
        ProductIDs = productsIdes;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getFreePoints() {
        return FreePoints;
    }

    public void setFreePoints(String freePoints) {
        FreePoints = freePoints;
    }

    public String getPromocode() {
        return Promocode;
    }

    public void setPromocode(String promocode) {
        Promocode = promocode;
    }

    public String getPromoamount() {
        return PromoAmt;
    }

    public void setPromoamount(String promoamount) {
        PromoAmt = promoamount;
    }

    public String getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(String paymentID) {
        PaymentID = paymentID;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
