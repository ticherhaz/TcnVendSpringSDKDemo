package com.tcn.sdk.springdemo.tcnSpring;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;


/**
 * Created by Administrator on 2016/8/26.
 */
public class Titlebar extends RelativeLayout {

    public static final int BUTTON_TYPE_BACK_AND_EXIT = 1;
    public static final int BUTTON_TYPE_BACK = 2;

    public static final int BUTTON_ID_BACK = 1;
    public static final int BUTTON_ID_EXIT = 2;

    private Button title_bar_back = null;
    private TextView title_bar_name = null;
    private Button title_bar_exit = null;
    private TitleBarListener m_TitleBarListener = null;
    private ClickListener m_ClickListener = new ClickListener();

    public Titlebar(Context context) {
        super(context);
        initView(context);
    }

    public Titlebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Titlebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.ui_base_tcn_title_bar, this);
        title_bar_back = findViewById(R.id.title_bar_back);
        title_bar_back.setOnClickListener(m_ClickListener);
        title_bar_name = findViewById(R.id.title_bar_name);
        title_bar_exit = findViewById(R.id.title_bar_exit);
        title_bar_exit.setOnClickListener(m_ClickListener);
    }

    public void setButtonName(int resid) {
        if (title_bar_name != null) {
            title_bar_name.setText(resid);
        }
    }

    public void setButtonName(String data) {
        if (title_bar_name != null) {
            title_bar_name.setText(data);
        }
    }

    public void setButtonSecondName(int resid) {
        if (title_bar_exit != null) {
            title_bar_exit.setText(resid);
        }
    }

    public void setButtonType(int type) {
        if (BUTTON_TYPE_BACK_AND_EXIT == type) {
            title_bar_back.setVisibility(VISIBLE);
            title_bar_exit.setVisibility(VISIBLE);
        } else if (BUTTON_TYPE_BACK == type) {
            title_bar_back.setVisibility(VISIBLE);
            title_bar_exit.setVisibility(INVISIBLE);
        } else {

        }
    }

    public void setTitleBarListener(TitleBarListener listener) {
        m_TitleBarListener = listener;
    }

    public void removeButtonListener() {
        if (title_bar_back != null) {
            title_bar_back.setText(null);
            title_bar_back.setOnClickListener(null);
            title_bar_back = null;
        }
        if (title_bar_exit != null) {
            title_bar_exit.setText(null);
            title_bar_exit.setOnClickListener(null);
            title_bar_exit = null;
        }
        title_bar_name.setText(null);
        title_bar_name = null;
        m_ClickListener = null;
        m_TitleBarListener = null;
    }

    public interface TitleBarListener {
        void onClick(View v, int buttonId);
    }

    private class ClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (null == v) {
                return;
            }
            int id = v.getId();
            if (R.id.title_bar_back == id) {
                if (m_TitleBarListener != null) {
                    m_TitleBarListener.onClick(Titlebar.this, BUTTON_ID_BACK);
                }
            } else if (R.id.title_bar_exit == id) {
                if (m_TitleBarListener != null) {
                    m_TitleBarListener.onClick(Titlebar.this, BUTTON_ID_EXIT);
                }
            } else {

            }
        }
    }
}
