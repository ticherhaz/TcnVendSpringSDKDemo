package com.tcn.sdk.springdemo;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class CustomLongClickListener implements View.OnTouchListener {

    private static long LONG_CLICK_DURATION = 5000; // 10 seconds
    private final Handler handler;
    private final Runnable runnable;
    private final OnLongClickListener longClickListener;
    private boolean isLongPress;

    public CustomLongClickListener(OnLongClickListener listener, long timerlong) {
        this.longClickListener = listener;
        handler = new Handler();
        LONG_CLICK_DURATION = timerlong;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isLongPress) {
                    longClickListener.onLongClick(null); // pass the view if needed
                }
            }
        };
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isLongPress = true;
                handler.postDelayed(runnable, LONG_CLICK_DURATION);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isLongPress = false;
                handler.removeCallbacks(runnable);
                break;
        }
        return true;
    }

    public interface OnLongClickListener {
        void onLongClick(View v);
    }
}