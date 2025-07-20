package com.tcn.sdk.springdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class TestDuitNowActivity extends AppCompatActivity {
    private JSONObject jsonObject;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testduitnow);

        btnTest = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(view -> {
            String traceNo = UUID.randomUUID().toString().toUpperCase();

            double payment = 0.01;
            paymentipay ipay88ewallet = new paymentipay();
            JSONObject result = ipay88ewallet.Merchant_Scan_Duitnow(payment, "", 0,
                    TestDuitNowActivity.this, "M41288", "m4d6TpUkUp", "49", traceNo);
            jsonObject = result;
            try {
                String QRCode = jsonObject.getString("QRCode");
                String QRValue = jsonObject.getString("QRValue");
                ImageView ivQrcode = findViewById(R.id.ivQrcode);
                TextView tvLogs = findViewById(R.id.tvLogs);
                tvLogs.setText(QRValue);

                Glide.with(this)
                        .load(QRCode)
                        .override(500, 500)  // Resize (similar to Picasso)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache both original & resized
                        .into(ivQrcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}