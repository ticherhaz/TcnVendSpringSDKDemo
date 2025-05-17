package com.tcn.sdk.springdemo.SarawakPay;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

@Database(entities = {DebugLog.class, Transaction.class}, version = 1)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    private static RoomDatabase INSTANCE;

    public static RoomDatabase getSarawakPayDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, RoomDatabase.class, "sarawak_pay")
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
                    //.allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract RoomConfigurationDao sarawakPayConfigurationDao();
}