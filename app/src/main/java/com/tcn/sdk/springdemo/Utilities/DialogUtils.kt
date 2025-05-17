package com.tcn.sdk.springdemo.Utilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.tcn.sdk.springdemo.R

object DialogUtils {

    fun showDialogVendingVersion(context: Context, callback: DialogUtilsCallback) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_vending_version)

        // Make dialog background transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnVendingM3 = dialog.findViewById<Button>(R.id.btn_vending_m3)
        val btnVendingM4 = dialog.findViewById<Button>(R.id.btn_vending_m4)
        val btnVendingM5 = dialog.findViewById<Button>(R.id.btn_vending_m5)

        btnVendingM3.setOnClickListener {
            callback.omButtonClicked("M3")
            dialog.dismiss()
        }

        btnVendingM4.setOnClickListener {
            callback.omButtonClicked("M4")
            dialog.dismiss()
        }

        btnVendingM5.setOnClickListener {
            callback.omButtonClicked("M5")
            dialog.dismiss()
        }

        dialog.show()
    }

    interface DialogUtilsCallback {
        fun omButtonClicked(version: String)
    }
}