package com.tcn.sdk.springdemo.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    public static final String Lpayment = "Lpayment";
    public static final String Ldispense = "Ldispense";
    public static final String LcartEnable = "LcartEnable";
    public static final String LiframeEnable = "LiframeEnable";
    public static final String SarawakPay = "SarawakPay";
    public static final String Token = "Token";
    public static final String CashCoinPay = "CashCoinPay";
    public static final String SarawakPayMid = "SarawakPayMid";
    public static final String SarawakPayMname = "SarawakPayMname";
    public static final String TestDispensePassword = "TestDispensePassword";
    public static final String SarawakPayOnly = "SarawakPayOnly";
    public static final String CashOnly = "CashOnly";
    public static final String DuitNowOnly = "DuitNowOnly";
    public static final String DuitNowOnlyNew = "DuitNowOnlyNew";
    public static final String PUBLIC_BANK_QR_DUITNOW = "PUBLIC_BANK_QR_DUITNOW";
    public static final String MQTT = "MQTT";
    public static final String TIMER = "TIMER";
    public static final String FasspayOnly = "FasspayOnly";
    public static final String type = "type";
    public static final String gtypepaywave = "gtypepaywave";
    public static final String gtypewallet = "gtypewallet";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String dispensetest = "dispensetest";
    public static final String adminpassword = "adminpassword";
    public static final String noteone = "noteone";
    public static final String notefive = "notefive";
    public static final String noteten = "noteten";
    public static final String notetwenty = "notetwenty";
    public static final String notefifty = "notefifty";
    public static final String nitehundred = "nitehundred";
    public static final String logopath = "logopath";
    public static final String martext = "martext";
    public static final String ADMIN_PASSWORD = "adminpassword";
    public static final String VENDING_VERSION = "VENDING_VERSION";
    public static final String FIRE_LOG = "FireLog";
    public static final String MERCHANT_CODE = "merchantCode";
    public static final String MERCHANT_KEY = "merchantKey";
    public static final String FRANCHISE_ID = "franchiseId";
    public static final String MACHINE_ID = "machineId";
    public static final String setBoardSerPortFirst = "setBoardSerPortFirst";
    public static final String setBoardSerPortSecond = "setBoardSerPortSecond";
    public static final String webviewURL = "webviewURL";

    private static SharedPreferences mSharedPref;

    private SharedPref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}

