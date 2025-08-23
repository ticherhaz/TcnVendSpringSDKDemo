package com.tcn.sdk.springdemo.Utilities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

public class Uti {
    public static boolean chkinternet(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void freeMemory() {
        new Thread(() -> {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        }).start();
    }

    public static void optimizeMemory(@NonNull final Application application) {
        new Thread(() -> {
            // Trim memory in background
            Runtime.getRuntime().gc();
            System.runFinalizersOnExit(true);

            // Suggest VM to release memory
            ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
        }).start();
    }
}
