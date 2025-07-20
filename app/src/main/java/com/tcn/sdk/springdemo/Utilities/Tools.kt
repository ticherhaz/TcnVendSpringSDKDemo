package com.tcn.sdk.springdemo.Utilities

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager


object Tools {

    fun logSimple(message: String) {
        Log.d("???", message)
    }

    fun hideKeyboard(context: Context, view: View?) {
        // Check if no view has focus:
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}