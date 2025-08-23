package com.tcn.sdk.springdemo.Utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
    private static final int MAX_FILE_SIZE_MB = 5; // Not enforced, just placeholder

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped Storage (Android 10+)
            ContentResolver resolver = appContext.getContentResolver();
            Uri downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

            Uri fileUri = null;

            // Try to query existing file
            try (Cursor cursor = resolver.query(
                    downloadsUri,
                    new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.RELATIVE_PATH},
                    MediaStore.Downloads.DISPLAY_NAME + "=?",
                    new String[]{fileName},
                    null
            )) {
                if (cursor != null && cursor.moveToFirst()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                    fileUri = ContentUris.withAppendedId(downloadsUri, id);
                }
            }

            // If not found, try to insert new row
            if (fileUri == null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/VendingMachine/Logs/");

                try {
                    fileUri = resolver.insert(downloadsUri, values);
                } catch (Exception insertEx) {
                    Log.w(TAG, "Insert failed, retrying query: " + insertEx.getMessage());
                    // If insert failed due to existing file, query again
                    try (Cursor cursor = resolver.query(
                            downloadsUri,
                            new String[]{MediaStore.Downloads._ID},
                            MediaStore.Downloads.DISPLAY_NAME + "=?",
                            new String[]{fileName},
                            null
                    )) {
                        if (cursor != null && cursor.moveToFirst()) {
                            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                            fileUri = ContentUris.withAppendedId(downloadsUri, id);
                        }
                    }
                }
            }

            if (fileUri != null) {
                try (OutputStream out = resolver.openOutputStream(fileUri, "wa")) {
                    out.write(content.getBytes());
                    out.flush();
                    Log.d(TAG, "Appended log to file (MediaStore): " + fileName);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to write log (MediaStore)", e);
                }
            } else {
                Log.e(TAG, "File URI is null (MediaStore)");
            }
        } else {
            // Legacy file I/O (Android 9 and below)
            File logDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VendingMachine/Logs");
            if (!logDir.exists() && !logDir.mkdirs()) {
                Log.e(TAG, "Failed to create log directory");
                return;
            }

            File logFile = new File(logDir, fileName);
            try (OutputStream out = new java.io.FileOutputStream(logFile, true)) {
                out.write(content.getBytes());
                out.flush();
                Log.d(TAG, "Appended log to file (Legacy): " + logFile.getAbsolutePath());
            } catch (IOException e) {
                Log.e(TAG, "Failed to write log (Legacy)", e);
            }
        }
    }

    // Convenience wrappers
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
        long now = System.currentTimeMillis();
        long threeDaysMillis = 3 * 24 * 60 * 60 * 1000L;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped Storage delete
            ContentResolver resolver = appContext.getContentResolver();
            Uri downloadsUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

            try (Cursor cursor = resolver.query(
                    downloadsUri,
                    new String[]{MediaStore.Downloads._ID, MediaStore.Downloads.DATE_MODIFIED, MediaStore.Downloads.DISPLAY_NAME},
                    MediaStore.Downloads.DISPLAY_NAME + " LIKE ?",
                    new String[]{LOG_FILE_PREFIX + "%"},
                    null
            )) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                        long modified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)) * 1000L;
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME));

                        if (now - modified > threeDaysMillis) {
                            Uri fileUri = ContentUris.withAppendedId(downloadsUri, id);
                            int rows = resolver.delete(fileUri, null, null);
                            Log.d(TAG, "Deleted old log file (MediaStore): " + name + " rows=" + rows);
                        }
                    }
                }
            }
        } else {
            // Legacy delete (File API)
            File logDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VendingMachine/Logs");

            if (!logDir.exists() || !logDir.isDirectory()) return;

            File[] logFiles = logDir.listFiles((dir, name) -> name.startsWith(LOG_FILE_PREFIX));
            if (logFiles == null || logFiles.length <= 3) return;

            // Sort by last modified (oldest first)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.sort(logFiles, Comparator.comparingLong(File::lastModified));
            }

            for (File file : logFiles) {
                if (now - file.lastModified() > threeDaysMillis) {
                    boolean deleted = file.delete();
                    Log.d(TAG, "Deleted old log file: " + file.getName() + " = " + deleted);
                }
            }
        }
    }
}
