package com.tcn.sdk.springdemo.PayWave;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaywaveChoosePopUp {
    private final String TAG = "PaywaveChoosePopUp";
    private TypeProfuctActivity activity;
    private double totalprice;
    private Dialog customDialog;
    private CountDownTimer[] ct;
    private ImageButton iv_scan_boost, iv_scan_maybank, iv_scan_ali, iv_scan_grab, iv_scan_wechat, iv_scan_touch;
    private Intent intent;
    private Button backbtn;
    private List<ProductModel> productapiModelList;
    private List<CartListModel> cartListModelList;
    private Dialog customDialogPaywaveChoose;
    private SweetAlertDialog pDialogQr;
    private RequestQueue requestQueue;

    public void paywaveChoosePopUp(TypeProfuctActivity activity, UserObj userObj, RequestQueue requestQueue) {
        RollingLogger.i(TAG, "paywavechoosepopup start");

        this.requestQueue = requestQueue;
        this.activity = activity;
        totalprice = userObj.getChargingprice();
        this.cartListModelList = userObj.getCartModel();

        customDialogPaywaveChoose = activity.getCustomDialogPaywaveChoose();
        customDialogPaywaveChoose.show();

        backbtn = customDialogPaywaveChoose.findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "back button click");
                customDialogPaywaveChoose.dismiss();
            }
        });

        TextView total = customDialogPaywaveChoose.findViewById(R.id.total);
        total.setText("TOTAL : RM " + String.format("%.2f", totalprice));
        RollingLogger.i(TAG, "total-" + String.format("%.2f", totalprice));

        iv_scan_boost = customDialogPaywaveChoose.findViewById(R.id.iv_scan_boost);
        iv_scan_boost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "boost click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "boost", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
        iv_scan_maybank = customDialogPaywaveChoose.findViewById(R.id.iv_scan_maybank);
        iv_scan_maybank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "maybank click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "maybank", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
        iv_scan_ali = customDialogPaywaveChoose.findViewById(R.id.iv_scan_ali);
        iv_scan_ali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "ali click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "ali", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
        iv_scan_grab = customDialogPaywaveChoose.findViewById(R.id.iv_scan_grab);
        iv_scan_grab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "grab click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "grab", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
        iv_scan_wechat = customDialogPaywaveChoose.findViewById(R.id.iv_scan_wechat);
        iv_scan_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "wechat click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "wechat", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
        iv_scan_touch = customDialogPaywaveChoose.findViewById(R.id.iv_scan_touch);
        iv_scan_touch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "touch click");
                PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                paywavepopup.summaryPopUp(activity, String.valueOf(totalprice), "touch", "wallet", userObj,
                        customDialogPaywaveChoose, requestQueue);
            }
        });
    }
}
