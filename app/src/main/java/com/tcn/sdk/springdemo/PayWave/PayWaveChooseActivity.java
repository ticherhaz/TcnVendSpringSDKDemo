package com.tcn.sdk.springdemo.PayWave;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tcn.sdk.springdemo.R;

public class PayWaveChooseActivity extends AppCompatActivity {

    private ImageButton iv_scan_boost, iv_scan_maybank, iv_scan_ali, iv_scan_grab, iv_scan_wechat, iv_scan_touch;
    private Intent intent;
    private Button backbtn;
    private String totalprice, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pay_wave);

        intent = getIntent();
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        totalprice = intent.getStringExtra("totalprice");
        TextView total = findViewById(R.id.total);
        total.setText("TOTAL : RM " + String.format("%.2f", Double.valueOf(totalprice)));

        iv_scan_boost = findViewById(R.id.iv_scan_boost);
        iv_scan_boost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "boost");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });
        iv_scan_maybank = findViewById(R.id.iv_scan_maybank);
        iv_scan_maybank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "maybank");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });
        iv_scan_ali = findViewById(R.id.iv_scan_ali);
        iv_scan_ali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "ali");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });
        iv_scan_grab = findViewById(R.id.iv_scan_grab);
        iv_scan_grab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "grab");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });
        iv_scan_wechat = findViewById(R.id.iv_scan_wechat);
        iv_scan_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "wechat");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });
        iv_scan_touch = findViewById(R.id.iv_scan_touch);
        iv_scan_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(PayWaveChooseActivity.this, PayWaveSummaryActivity.class);
                it.putExtra("totalprice", totalprice);
                it.putExtra("type", "touch");
                it.putExtra("typechoose", "wallet");
                startActivity(it);
                finish();
            }
        });


    }
}