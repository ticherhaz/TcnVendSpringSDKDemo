package com.tcn.sdk.springdemo.tcnSpring;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MySlipSwitch extends View implements OnTouchListener {

    //打开时的背景，关闭时的背景，滑动按钮  Background when open, background when closed, sliding buttons
    private Bitmap switch_on_Bkg, switch_off_Bkg, slip_Btn;
    private Rect on_Rect, off_Rect;

    //是否正在滑动 Is it sliding
    private boolean isSlipping = false;
    //当前状态?true为开启，false为关闿  Current status? True is on, false is off
    private boolean isSwitchOn = false;

    //手指按下时的水平坐标X，当前的水平坐标X  The horizontal coordinate X when the finger is pressed, the current horizontal coordinate X
    private float previousX, currentX;


    private OnSwitchListener onSwitchListener;
    //是否设置了开关监听器 Whether or not to set the listener switch

    private boolean isSwitchListenerOn = false;


    public MySlipSwitch(Context context) {
        super(context);
        init();
    }


    public MySlipSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySlipSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setOnTouchListener(this);
    }


    public void setImageResource(int switchOnBkg, int switchOffBkg, int slipBtn) {
        switch_on_Bkg = BitmapFactory.decodeResource(getResources(), switchOnBkg);
        switch_off_Bkg = BitmapFactory.decodeResource(getResources(), switchOffBkg);
        slip_Btn = BitmapFactory.decodeResource(getResources(), slipBtn);

        //右半边Rect，即滑动按钮在右半边时表示开关开吿 Rect on the right half, that is, the switch is on when the sliding button is on the right half
        on_Rect = new Rect(switch_off_Bkg.getWidth() - slip_Btn.getWidth(), 0, switch_off_Bkg.getWidth(), slip_Btn.getHeight());
        //左半边Rect，即滑动按钮在左半边时表示开关关闭 Left half Rect, that is, the switch is off when the sliding button is in the left half
        off_Rect = new Rect(0, 0, slip_Btn.getWidth(), slip_Btn.getHeight());
    }

    protected boolean getSwitchState() {
        return isSwitchOn;
    }

    public void setSwitchState(boolean switchState) {
        isSwitchOn = switchState;
        updateSwitchState(switchState);
    }

    protected void updateSwitchState(boolean switchState) {
        isSwitchOn = switchState;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        //滑动按钮的左边 Left of the slide button
        float left_SlipBtn;

        //手指滑动到左半边的时候表示开关为关闭状态，滑动到右半边的为开启状态 When you slide your finger to the left half, the switch is off, and when you slide your finger to the right half, it is on.
        if (currentX < (switch_on_Bkg.getWidth() / 2)) {
            canvas.drawBitmap(switch_off_Bkg, matrix, paint);
        } else {
            canvas.drawBitmap(switch_on_Bkg, matrix, paint);
        }

        //判断当前是否正在滑动 Determine if the current is sliding or not
        if (isSlipping) {
            if (currentX > switch_on_Bkg.getWidth()) {
                left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
            } else {
                left_SlipBtn = currentX - slip_Btn.getWidth() / 2;
            }
        } else {
            //根据当前的开关状态设置滑动按钮的位置 Set the position of the slide button according to the current switch state
            if (isSwitchOn) {
                left_SlipBtn = on_Rect.left;
            } else {
                left_SlipBtn = off_Rect.left;
            }
        }

        //对滑动按钮的位置进行异常判断 judgment on the position of the slide button whether it is anomaly or not
        if (left_SlipBtn < 0) {
            left_SlipBtn = 0;
        } else if (left_SlipBtn > switch_on_Bkg.getWidth() - slip_Btn.getWidth()) {
            left_SlipBtn = switch_on_Bkg.getWidth() - slip_Btn.getWidth();
        }

        canvas.drawBitmap(slip_Btn, left_SlipBtn, 0, paint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null == switch_on_Bkg) {
            return;
        }
        // TODO Auto-generated method stub
        setMeasuredDimension(switch_on_Bkg.getWidth(), switch_on_Bkg.getHeight());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            //滑动 slide
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                break;

            //按下 Press
            case MotionEvent.ACTION_DOWN:
//				if(event.getX() > switch_on_Bkg.getWidth() || event.getY() > switch_on_Bkg.getHeight()) {
//					return false;
//				}
                isSlipping = true;
                previousX = event.getX();
                currentX = previousX;
                break;

            //松开 release
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isSlipping = false;
                //松开前开关的状态 The state of the switch before releasing
                boolean previousSwitchState = isSwitchOn;

                isSwitchOn = currentX >= (switch_on_Bkg.getWidth() / 2);

                //如果设置了监听器，则调用此方法 This method is called if a listener is set
                if (isSwitchListenerOn && (previousSwitchState != isSwitchOn)) {
                    onSwitchListener.onSwitched(isSwitchOn);
                }
                break;

            default:
                break;
        }

        //重新绘制控件 Repaint control
        invalidate();
        return true;
    }


    public void setOnSwitchListener(OnSwitchListener listener) {
        onSwitchListener = listener;
        isSwitchListenerOn = true;
    }


    public interface OnSwitchListener {
        void onSwitched(boolean isSwitchOn);
    }

}
