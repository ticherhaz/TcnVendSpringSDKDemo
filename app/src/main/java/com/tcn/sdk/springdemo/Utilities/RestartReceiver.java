package com.tcn.sdk.springdemo.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.proembed.service.MyService;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            SharedPref.init(context);
            SharedPref.write(SharedPref.APP_AUTO_RESTART_ENABLE, "true");
            MyService myService = new MyService(context);
            myService.setAppListen(context.getPackageName(), 5); // Re-enable with 5s delay
//            Toast.makeText(context, "Auto-restart re-enabled (via Alarm)", Toast.LENGTH_SHORT).show();
            Log.d("RestartReceiver", "Auto-restart enabled after 15-minute delay");
        } catch (Exception e) {
            Log.e("RestartReceiver", "Failed to re-enable auto-restart", e);
        }
    }
}
