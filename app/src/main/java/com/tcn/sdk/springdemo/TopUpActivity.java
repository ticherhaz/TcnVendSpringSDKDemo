package com.tcn.sdk.springdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class TopUpActivity extends AppCompatActivity {
    private final String TAG = "TopUpActivity";
    private EditText et_password;
    private Button btn_password, btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_topup);


    }
}
