package com.tcn.sdk.springdemo;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DownloadAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_app);
        final Button back = findViewById(R.id.backbtn);
        back.setOnClickListener(v -> finish());
    }
}