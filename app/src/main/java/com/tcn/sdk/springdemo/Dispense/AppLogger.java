package com.tcn.sdk.springdemo.Dispense;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    private static final boolean isDebug = false;//BuildConfig.DEBUG;

    public static void i(String tag, String string) {

        Logger log = LoggerFactory.getLogger(" | " + tag);
        log.info(string);

        if (isDebug) {
            Log.i(tag, string);
        }
    }

    public static void e(String tag, String string) {

        Logger log = LoggerFactory.getLogger(" | " + tag);
        log.error(string);

        if (isDebug) {
            Log.e(tag, string);
        }
    }

    public static void d(String tag, String string) {

        Logger log = LoggerFactory.getLogger(" | " + tag);
        log.debug(string);

        if (isDebug) {
            Log.d(tag, string);
        }
    }

    public static void v(String tag, String string) {
        if (isDebug) Log.v(tag, string);
    }

    public static void w(String tag, String string) {

        Logger log = LoggerFactory.getLogger(" | " + tag);
        log.warn(string);

        if (isDebug) {
            Log.w(tag, string);
        }
    }
}
