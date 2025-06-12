package com.tcn.sdk.springdemo.Utilities

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log
import com.tcn.sdk.springdemo.MainActivity
import net.ticherhaz.firelog.FireLog
import kotlin.system.exitProcess

class AppCrashHandler(
    private val application: Application,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val RESTART_DELAY_MS = 1000L
        private const val TAG = "AppCrashHandler"
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        try {
            Log.e(TAG, "App crashed, restarting...")

            FireLog.log(
                FireLog.LogType.ERROR,
                TAG,
                "uncaughtException",
                "Throwable",
                ex.message + " "
            )

            // **Method 1: Using PendingIntent (Works on Android 10+)**
            val restartIntent = Intent(application, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("CRASH_RESTART", true)
            }

            // **Use PendingIntent to ensure restart even if app is killed**
            val pendingIntent = PendingIntent.getActivity(
                application,
                0,
                restartIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            // **Use AlarmManager to schedule restart (Works even if process dies)**
            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + RESTART_DELAY_MS,
                pendingIntent
            )

            // **Kill the current process**
            killProcess()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule restart", e)
            defaultHandler?.uncaughtException(thread, ex)
            killProcess()
        }
    }

    private fun killProcess() {
        try {
            Process.killProcess(Process.myPid())
            exitProcess(1)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill process", e)
        }
    }
}