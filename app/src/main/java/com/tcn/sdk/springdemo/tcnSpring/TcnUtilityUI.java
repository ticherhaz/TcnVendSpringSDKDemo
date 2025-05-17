package com.tcn.sdk.springdemo.tcnSpring;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tcn.sdk.springdemo.R;

public class TcnUtilityUI {
    private static Toast toast;
    private static long lastClickTime;

    public static void getsToastSign(Context context, String text) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.ui_base_toast, null);
            if (toast == null) {
                toast = new Toast(context);
                TextView msg = view.findViewById(R.id.msg);
                msg.setText(text);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
            } else {
                TextView msg = view.findViewById(R.id.msg);
                msg.setText(text);
            }
            view.setBackgroundResource(R.drawable.ui_base_toaststyle);
            toast.setView(view);
            toast.show();
        } catch (Exception e) {

        }

    }

    /**
     * 自定义toast的样式 Custom toast style
     *
     * @param context
     * @param text
     */
    public static void getToast(Context context, String text) {
        try {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.ui_base_toast, null);
            TextView msg = view.findViewById(R.id.msg);
            msg.setText(text);
            view.setBackgroundResource(R.drawable.ui_base_toaststyle);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(500);
            toast.show();
        } catch (Exception e) {

        }

    }

    public static void getToast(Context context, String text, float textSize) {
        try {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.ui_base_toast, null);
            TextView msg = view.findViewById(R.id.msg);
            msg.setText(text);
            msg.setTextSize(textSize);
            view.setBackgroundResource(R.drawable.ui_base_toaststyle);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {

        }

    }

    public static Toast getToast(Context context, String text, int duration) {
        Toast toast = new Toast(context);
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.ui_base_toast, null);
            TextView msg = view.findViewById(R.id.msg);
            msg.setText(text);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
        } catch (Exception e) {

        }
        return toast;
    }

    public static Toast getToastAndShow(Context context, String text, int duration) {
        Toast toast = new Toast(context);
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.ui_base_toast, null);
            TextView msg = view.findViewById(R.id.msg);
            msg.setText(text);
            toast.setView(view);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
            toast.show();
        } catch (Exception e) {

        }
        return toast;
    }

    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
