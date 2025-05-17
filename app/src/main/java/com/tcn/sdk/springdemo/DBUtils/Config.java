package com.tcn.sdk.springdemo.DBUtils;

import android.os.Environment;

import java.io.File;

public class Config {
    public static final String DOWN_FILE_SAVEDIR = (getSourceRootDir("Sourcefile") + "/");
    public static final String USB_SAVEDIR = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sourcefile/");
    public static int no = 0;

    private static String getRootDir() {
        boolean sdk_can_use;
        sdk_can_use = Environment.getExternalStorageState().equals("mounted");
        if (sdk_can_use) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return Environment.getDataDirectory().toString();
    }

    public static String getSourceRootDir(String subDir) {
        String rootDir = getRootDir() + "/" + subDir;
        File file = new File(rootDir);
        if (!file.exists()) {
            file.mkdir();
        }
        return rootDir;
    }

    public static int getNextNo() {
        no++;
        if (no > 255) {
            no = 0;
        }
        return no;
    }
}