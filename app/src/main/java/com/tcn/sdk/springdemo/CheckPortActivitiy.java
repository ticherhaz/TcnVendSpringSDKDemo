//package com.tcn.sdk.springdemo;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.tcn.sdk.springdemo.Utilities.CheckMachineStatus;
//
//public class CheckPortActivitiy extends AppCompatActivity {
//    public boolean onlinestatus = false;
//    private Button btn_check;
//    private TextView tv_ms;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_machinesettinginfo);
//
//        tv_ms = findViewById(R.id.tv_ms);
//        btn_check = findViewById(R.id.btn_check);
//        btn_check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CheckMachineStatus machineStatus = new CheckMachineStatus();
//                machineStatus.status(CheckPortActivitiy.this, tv_ms);
//            }
//        });
//    }
//}
