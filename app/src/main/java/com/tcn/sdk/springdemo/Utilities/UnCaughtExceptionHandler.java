package com.tcn.sdk.springdemo.Utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tcn.sdk.springdemo.MainActivity;

public class UnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Activity activity;

    public UnCaughtExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Intent intent;
        PendingIntent pendingIntent;
        AlarmManager mgr;

        intent = new Intent(activity, MainActivity.class);
        intent.putExtra("crash", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(MainActivity.getInstance().getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mgr = (AlarmManager) MainActivity.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);


        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        activity.finish();
        System.exit(2);
    }
}
