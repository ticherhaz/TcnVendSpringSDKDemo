package com.tcn.sdk.springdemo.Utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class RollingLogger {

    private static final String TAG = "RollingLogger";
    private static final String LOG_FILE_PREFIX = "VendingMachine_log";
    private static final int MAX_FILE_SIZE_MB = 5; // Not enforced due to MediaStore limitations

    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static void log(String level, String message) {
        if (appContext == null) {
            throw new IllegalStateException("RollingLogger not initialized. Call init(context) first.");
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS a", Locale.getDefault()).format(new Date());
        String logLine = timestamp + " [" + Thread.currentThread().getName() + "] " + level + " - " + message + "\n";

        writeLogToDownloads(logLine);
    }

    private static void writeLogToDownloads(String content) {
        deleteOldLogs();

        String fileName = LOG_FILE_PREFIX + "-" +
                new SimpleDateFormat("yyyy-MM-dd-HH", Locale.getDefault()).format(new Date()) + ".txt";

        ContentResolver resolver = appContext.getContentResolver();
        Uri downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

        // Query to find existing file
        Uri fileUri = null;
        try (android.database.Cursor cursor = resolver.query(
                downloadsUri,
                new String[]{MediaStore.Downloads._ID},
                MediaStore.Downloads.DISPLAY_NAME + "=? AND " +
                        MediaStore.Downloads.RELATIVE_PATH + "=?",
                new String[]{fileName, Environment.DIRECTORY_DOWNLOADS + "/VendingMachine/Logs/"},
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                fileUri = Uri.withAppendedPath(downloadsUri, String.valueOf(id));
            }
        }

        // If file not found, create it
        if (fileUri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/VendingMachine/Logs");

            fileUri = resolver.insert(downloadsUri, values);
        }

        if (fileUri != null) {
            try (OutputStream out = resolver.openOutputStream(fileUri, "wa")) { // "wa" = write/append mode
                out.write(content.getBytes());
                out.flush();
                Log.d(TAG, "Appended log to file: " + fileName);
            } catch (IOException e) {
                Log.e(TAG, "Failed to write log", e);
            }
        } else {
            Log.e(TAG, "File URI is null");
        }
    }


    // Convenience methods
    public static void i(String tag, String msg) {
        log(tag, msg);
    }

    public static void e(String msg) {
        log("ERROR", msg);
    }

    public static void d(String msg) {
        log("DEBUG", msg);
    }

    private static void deleteOldLogs() {
        File logDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VendingMachine/Logs");

        if (!logDir.exists() || !logDir.isDirectory()) return;

        File[] logFiles = logDir.listFiles((dir, name) -> name.startsWith(LOG_FILE_PREFIX));
        if (logFiles == null || logFiles.length <= 3) return;

        // Sort by last modified (oldest first)
        Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified));

        long now = System.currentTimeMillis();
        long threeDaysMillis = 3 * 24 * 60 * 60 * 1000L;

        for (File file : logFiles) {
            if (now - file.lastModified() > threeDaysMillis) {
                boolean deleted = file.delete();
                Log.d(TAG, "Deleted old log file: " + file.getName() + " = " + deleted);
            }
        }
    }

}
