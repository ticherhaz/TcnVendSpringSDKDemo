package com.tcn.sdk.springdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.multidex.MultiDex
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tcn.sdk.springdemo.DBUtils.configdata
import com.tcn.sdk.springdemo.Utilities.AppCrashHandler
import com.tcn.sdk.springdemo.Utilities.SharedPref
import com.ys.springboard.control.TcnShareUseData
import com.ys.springboard.control.TcnVendApplication
import com.ys.springboard.control.TcnVendIF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ticherhaz.firelog.FireLog

class MyApplication : TcnVendApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        SharedPref.init(this)
        initTcnVend()

        // Launch FireLog initialization in background
        initFireLog()
        setupNotificationChannel()

        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(
            AppCrashHandler(this, defaultHandler)
        )
    }

    private fun initTcnVend() {
        TcnVendIF.getInstance().run {
            init(this@MyApplication)
            setConfig()
            startWorkThread()
        }
        val setBoardSerPortFirst = SharedPref.read(SharedPref.setBoardSerPortFirst, "/dev/ttyS1")
        val setBoardSerPortSecond = SharedPref.read(SharedPref.setBoardSerPortSecond, "/dev/ttyS3")
        TcnShareUseData.getInstance().boardSerPortFirst = setBoardSerPortFirst
        TcnShareUseData.getInstance().boardSerPortSecond = setBoardSerPortSecond

        TcnShareUseData.getInstance().serPortGroupMapFirst =
            "0"    //设置主柜组号，也可不设置，默认就是0. Set master machine group number, you can do not set it as well, the default is 0
        TcnShareUseData.getInstance().serPortGroupMapSecond =
            "0"   //设置副柜组号为0,副柜需要接安卓另外一个串口 Set the slave machine group number to 0.The slave machine needs to be connect to another serial port of Android.
        TcnShareUseData.getInstance().boardTypeSecond = "thj"
//        TcnShareUseData.getInstance().setBoardSerPortMDB("/dev/ttyS2");
    }

    private fun initFireLog() = applicationScope.launch {
        // initialize everytime
        // Initialize FireLog first (check if this needs main thread)
        FireLog.initialize(applicationContext, FireLog.VendingMachineType.VENDING_MACHINE_M4)

        // Database operations should be on IO thread
        val configModels = withContext(Dispatchers.IO) {
            val databaseConfig = configdata(applicationContext)
            databaseConfig.getAllItems() // Ensure this is a suspend function or Room DAO call
        }

        // Process configurations
        for (itemConfig in configModels) {
            val merchantCode =
                itemConfig.getMerchantcode()?.takeIf { it.isNotEmpty() } ?: continue
            val merchantKey =
                itemConfig.getMerchantkey()?.takeIf { it.isNotEmpty() } ?: continue
            val franchiseId = itemConfig.getFid()?.takeIf { it.isNotEmpty() } ?: continue
            val machineId = itemConfig.getMid()?.takeIf { it.isNotEmpty() } ?: continue

            // Update FireLog (check if thread-safe)
            FireLog.updateMerchantCode(merchantCode)
            FireLog.updateMerchantKey(merchantKey)
            FireLog.updateFranchiseId(franchiseId)
            FireLog.updateMachineId(machineId)

            initFirebaseCrashlyticsCustomKey("merchantCode", merchantCode)
            initFirebaseCrashlyticsCustomKey("merchantKey", merchantKey)
            initFirebaseCrashlyticsCustomKey("franchiseId", franchiseId)
            initFirebaseCrashlyticsCustomKey("machineId", machineId)

            // Persist values
            withContext(Dispatchers.IO) {
                SharedPref.write(SharedPref.MERCHANT_CODE, merchantCode)
                SharedPref.write(SharedPref.MERCHANT_KEY, merchantKey)
                SharedPref.write(SharedPref.FRANCHISE_ID, franchiseId)
                SharedPref.write(SharedPref.MACHINE_ID, machineId)
            }

            break // Stop after first valid configuration
        }
    }

    private fun initFirebaseCrashlyticsCustomKey(key: String, value: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(key, value)
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "restart_channel",
                "App Restart Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for app restart requests"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onTerminate() {
        applicationScope.cancel()
        super.onTerminate()
    }
}