package com.tcn.sdk.springdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.Dispense.AppLogger;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.TempTrans;
import com.tcn.sdk.springdemo.Model.dataspassmodel;
import com.tcn.sdk.springdemo.Utilities.PortsandVeriables;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.Utilities.Uti;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SummaryActivity extends AppCompatActivity {

    private final String TAG = "SummaryActivity";
    private final String remarks = "N/A";
    private final String exdate = "";
    private final Handler handler = new Handler();
    long delay = 1000, last_text_edit = 0;
    String paymentmethod = "";
    String barcodetext = "";
    CountDownTimer cd;
    Boolean Paymentsuccess = false;
    String ErrDesc = "Payment Successful";
    private Button back;
    private List<CartListModel> cartListModels;
    private CartDBHandler db;
    private TextView ttltxt, txtmtd;
    private String ipforpaywave;
    private String mtd;
    private String promname;
    private SweetAlertDialog hide, dispense;
    private double chargingprice, discountamt = 0.00, points, newpoints, rrp = 0.00;
    private int paymentdrawable, userid = 0, userstatus, pid;
    private String fid;
    private String mid;
    private String LPayment = "";
    private String payid;
    private String paystatus;
    private String paytype = "";
    private String productsids = "";
    private String vids = "";
    private String type = "";
    private configdata dbconfig;
    private List<CongifModel> congifModels;
    private SweetAlertDialog sweetAlertDialog, sd, pDialog;
    private CountDownTimer[] ct;
    private EditText et;
    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                // ............
                // ............

                try {
                    String maybankReplace = et.getText().toString();

                    if (maybankReplace.length() > 17) {
                        if (type.equalsIgnoreCase("grab")) {
                            maybankReplace = maybankReplace.substring(0, 18);
                        } else if (type.equalsIgnoreCase("touchngo")) {
                            if (maybankReplace.length() > 23) {
                                maybankReplace = maybankReplace.substring(0, 24);
                            }
                        } else {
                            if (Character.isDigit(maybankReplace.charAt(0))) {
                                String maybankDigit = "";
                                for (int i = 0; i < maybankReplace.length(); i++) {
                                    if (Character.isDigit(maybankReplace.charAt(i))) {
                                        maybankDigit += maybankReplace.charAt(i);
                                    } else {
                                        maybankReplace = maybankDigit;
                                        break;
                                    }
                                }
                            } else {
                                String result = "";
                                int count = 0, getIndex = 0;
                                for (int i = 0; i < maybankReplace.length(); i++) {
                                    if (Character.isDigit(maybankReplace.charAt(i))) {
                                        count = 0;
                                        if (maybankReplace.length() > i + 1) {
                                            if (Character.isDigit(maybankReplace.charAt(i + 1))) {
                                                count++;
                                            }
                                            if (maybankReplace.length() > i + 2) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 2))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 3) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 3))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 4) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 4))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 5) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 5))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 6) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 6))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 7) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 7))) {
                                                    count++;
                                                }
                                            }
                                            if (maybankReplace.length() > i + 8) {
                                                if (Character.isDigit(maybankReplace.charAt(i + 8))) {
                                                    if (count == 7) {
                                                        getIndex = i;
                                                        count++;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (count == 8) {
                                    maybankReplace = maybankReplace.substring(0, getIndex);
                                }
                            }
                        }
                        RollingLogger.i(TAG, "call api");
                        new CallAPI().execute(maybankReplace, "0", chargingprice + "");

                        if (pDialog.getTitleText().toLowerCase().contains("touch'n go")) {
                            new CallAPI().execute(et.getText().toString(), "336", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("boost")) {
                            new CallAPI().execute(et.getText().toString(), "320", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("maybank")) {
                            new CallAPI().execute(maybankReplace, "354", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("wechat")) {
                            new CallAPI().execute(et.getText().toString(), "343", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("wechat (cny)")) {
                            new CallAPI().execute(et.getText().toString(), "305", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("alipay")) {
                            new CallAPI().execute(et.getText().toString(), "234", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("grabpay")) {
                            new CallAPI().execute(et.getText().toString(), "379", chargingprice + "");
                        }
                        if (pDialog.getTitleText().toLowerCase().contains("shopee")) {//for shopee
                            new CallAPI().execute(et.getText().toString(), "19", chargingprice + "");
                        } else {

                        }
                        System.out.println("the code written : " + et.getText());
                        pDialog.setTitle("Processing..");
                    }
                } catch (Exception ex) {

                }
            }
        }
    };
    private boolean isloggedin = false, isuserpaying = false, threadintrupt = false, oncreate = false, isVocher = false;
    private PortsandVeriables portsandVeriables;
    private wait30 w30;
    private boolean checkRunOneTimeOnly = false;

    private void setupfordispense() {
        if (ct != null) {
            if (ct[0] != null)
                ct[0].cancel();
        }

        RollingLogger.i(TAG, "dispenseactivitynew.class call");

        checkRunOneTimeOnly = true;
//        DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//        dispensePopUpM5.DispensePopUp(SummaryActivity.this, mtd, isloggedin, paytype,
//                chargingprice, payid, points, userid, pid,
//                exdate, userstatus, paystatus, remarks, cartListModels);
    }

    private void showsweetalerttimeout(final CountDownTimer[] ct) {
        sweetAlertDialog = new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
        sweetAlertDialog.setContentText("This session will end in 10");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                threadintrupt = true;
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sweetAlertDialog.dismissWithAnimation();
                ct[0].cancel();
            }
        });
        sweetAlertDialog.show();
    }

    private void setvalues() {
        dataspassmodel cc = (dataspassmodel) getIntent().getSerializableExtra("mval");
        this.paymentdrawable = cc.getDrawable;
        this.mtd = cc.Paywave;
        this.chargingprice = cc.achargingprice;
        this.discountamt = cc.promoamt;
        this.promname = cc.promname;

        try {
            ipforpaywave = "commented for test";
            System.out.println("IP FOR PAYWAVE = " + ipforpaywave);

        } catch (Exception ex) {
            ipforpaywave = null;
        }
        isloggedin = getIntent().getBooleanExtra("isloggedin", false);

        if (isloggedin) {
            points = getIntent().getDoubleExtra("points", 0.00);
            userid = getIntent().getIntExtra("userid", 0);
            //exdate = getIntent().getStringExtra("exdate");
            userstatus = getIntent().getIntExtra("ustatus", 0);
            pid = getIntent().getIntExtra("pid", 0);

            newpoints = points;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        checkRunOneTimeOnly = false;
        SharedPref.init(this);
        LPayment = SharedPref.read(SharedPref.Lpayment, "");

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        RollingLogger.i(TAG, "Start Summary Payment Activity");

        txtmtd = findViewById(R.id.mtd);
        ttltxt = findViewById(R.id.pricetext);

        cartListModels = new ArrayList<CartListModel>();

        portsandVeriables = new PortsandVeriables();

        back = findViewById(R.id.backbtn);
        db = new CartDBHandler(this);

        w30 = new wait30();
        w30.start();
        oncreate = true;
        dbconfig = new configdata(this);
        congifModels = new ArrayList<CongifModel>();
        congifModels = dbconfig.getAllItems();

        try {
            for (CongifModel cn : congifModels) {
                fid = cn.getFid();
                mid = cn.getMid();
            }

            cartListModels = (List<CartListModel>) getIntent().getSerializableExtra("cart");
            for (CartListModel cart : cartListModels) {
                int qty;
                qty = Integer.parseInt(cart.getItemqty());
                for (int x = 0; x < qty; x++) {

                    productsids += cart.getProdid() + ",";
                }


            }
            isloggedin = getIntent().getBooleanExtra("login", false);
            setvalues();
            if (isloggedin) {
                for (CartListModel cart : cartListModels) {
                    RollingLogger.i(TAG, "cart item number-" + cart.getItemnumber());
                    RollingLogger.i(TAG, "cart item qty-" + cart.getItemqty());
                    RollingLogger.i(TAG, "cart item name-" + cart.getItemname());
                    RollingLogger.i(TAG, "cart item price-" + cart.getItemprice());

                    rrp += ((Double.parseDouble(cart.itemprice) * Double.parseDouble(cart.rrp_percent)) / 100);

                    if (cart.getVoucher() != null) {

                        vids += cart.getVoucher() + ",";
                        System.out.println("Oncreate Voucher id =" + vids);
                        isVocher = true;
                        System.out.println("Voucher True");
                    }
                }

            }

            RollingLogger.i(TAG, "TOTAL : RM " + String.format("%.2f", chargingprice));
            ttltxt.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
            txtmtd.setText(mtd);
            //  txtpromo.setText(""+discountamt+"0");

            if (!Uti.chkinternet(SummaryActivity.this)) {
                new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("No internet")
                        .setContentText("Please contact careline")
                        .show();
            } else {
                paymentload();
            }
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handler13.removeCallbacksAndMessages(null);
                    finish();
                    RollingLogger.i(TAG, "back button clicked");
                    // mdbutils.closeSerialPort();
                }
            });
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Summary oncreate error - " + ex);
        }

    }

    void paymentload() {
        try {
            RollingLogger.i(TAG, "paymentload function");
            RollingLogger.i(TAG, "mtd-" + mtd);

            isuserpaying = true;
            et = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(80, 80, 80, 80);
            lp.gravity = Gravity.CENTER;

            pDialog = new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            pDialog.setTitleText("Show your payment QR code to machine to proceed.");

            final LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            if (mtd.equals("Paywave")) {

                hide = new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Please Tap your card on Paywave Device.");
                hide.show();
                RollingLogger.i(TAG, "paywave dialog");
                System.out.println("IP FOR PAYWAVE at loading = " + ipforpaywave);

            } else if (mtd.equals("Member Points")) {

                Paymentsuccess = true;
                paytype = "RRP";
                rrp = 0.00;

                SummaryActivity.this.setupfordispense();
                RollingLogger.i(TAG, "setupfordispense");

            } else {
                RollingLogger.i(TAG, "show payment dialog");
                Drawable myIcon = getResources().getDrawable(paymentdrawable);
                showmypaymentdialog(mtd, myIcon);
            }


            et.setInputType(InputType.TYPE_NULL);

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    handler.removeCallbacks(input_finish_checker);
                    // pDialog.setTitle("Please Wait verifying code..");
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        last_text_edit = System.currentTimeMillis();
                        handler.postDelayed(input_finish_checker, delay);

                    } else {

                    }

                }
            });
        } catch (Exception ex) {
            if (LPayment.equalsIgnoreCase("true")) {
                RollingLogger.i(TAG, "Payment Load - " + ex);
            }
        }
    }

    String ipayidtostring(int id) {
        switch (id) {
            case 234:
                return "AliPay";
            case 373:
                return "AliPay Pre-auth";
            case 320:
                return "Boost";
            case 354:
                return "MaybankQR";
            case 329:
                return "MCash";
            case 336:
                return "TouchNGo";
            case 338:
                return "Unionpay";
            case 343:
                return "WeChatPay My";
            case 305:
                return "WeChatPay CN";
            case 164:
                return "PrestoPay";
            case 379:
                return "GrabPay";
            case 19:
                return "ShopeePay";
            default:
                return "na";
        }

    }

    private void TempTrans(int Status) {

        try {
            RollingLogger.i(TAG, "temp api call start");

            Date currentTime = Calendar.getInstance().getTime();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = portsandVeriables.com + "TempTrans";
            TempTrans transactionModel = new TempTrans();
            transactionModel.setAmount(chargingprice);
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(userid);
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType(mtd);
            transactionModel.setPaymentMethod(paytype);
            transactionModel.setPaymentStatus(Status);
            transactionModel.setFreePoints(String.valueOf(rrp));
            transactionModel.setPromocode(promname);
            transactionModel.setPromoAmt(discountamt + "");
            transactionModel.setVouchers(vids);
            transactionModel.setPaymentStatusDes(ErrDesc);


            try {
                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject finalJsonParam = jsonParam;

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println(response.toString());
                    RollingLogger.i(TAG, "temp api call response-" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    RollingLogger.i(TAG, "temp api call error-" + error.toString());
                }
            });
            // pDialog.dismissWithAnimation();

            requestQueue.add(myReq);
        } catch (Exception ex) {
            if (LPayment.equalsIgnoreCase("true")) {
                RollingLogger.i(TAG, "TempTrans202301 - " + ex);
            }
        }
    }

    void showmypaymentdialog(String mtd, Drawable res) {

        paymentmethod = mtd;

        //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setAlpha(0.0f);
        linearLayout.addView(et);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setContentText("");

        pDialog.setConfirmButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        try {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
            pDialog.setTitleText("Show your QR Code to the scanner.");
            barcodetext = "";
            if (cd != null) cd.cancel();
            cd = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (pDialog.getTitleText().equals("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.")) {
                        //  pDialog.setContentText("This dialog will close in " + millisUntilFinished / 1000 + " seconds");
                    } else {
                        // pDialog.setContentText("loading...");
                        this.cancel();
                    }

                }

                public void onFinish() {
                    pDialog.dismissWithAnimation();
                }

            }.start();
            pDialog.setCustomView(linearLayout);
            pDialog.show();//showing the dialog
        } catch (Exception ex) {
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        RollingLogger.i(TAG, "onstop");
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

    }

    @Override
    public void onDestroy() {
        try {
            RollingLogger.i(TAG, "ondestroy");
            dispense.dismissWithAnimation();
            hide.dismissWithAnimation();
            // mdbutils.closeSerialPort();

        } catch (Exception Ex) {

        }
        super.onDestroy();
    }

    public void showerrdialoga(String err) {
        try {
            dispense.dismissWithAnimation();
            hide.dismissWithAnimation();

        } catch (Exception Ex) {

        }
        isuserpaying = false;
        sd = new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error Found!")
                .setContentText(err);
        sd.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        if (!this.isFinishing()) {
            sd.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt) {

                try {
                    Thread.sleep(150000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ct = new CountDownTimer[1];
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ct[0] = new CountDownTimer(10000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    if (!isuserpaying) {
                                        if (millisUntilFinished > 0) {
                                            sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
                                        } else {
                                            threadintrupt = true;
                                            try {
                                                sweetAlertDialog.dismissWithAnimation();
                                            } catch (Exception ex) {
                                            }
                                            ct[0].cancel();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }
                                }

                                public void onFinish() {
                                    try {
                                        if (sd != null) {
                                            if (sd.isShowing()) {
                                                sd.dismiss();
                                            }
                                        }
                                    } catch (Exception ex) {
                                    }
                                    try {
                                        sweetAlertDialog.dismissWithAnimation();
                                    } catch (Exception ex) {
                                    }
                                    threadintrupt = true;
                                    ct[0].cancel();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            };

                            if (!isuserpaying) {
                                showsweetalerttimeout(ct);
                                ct[0].start();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        }
    }

    public class CallAPI extends AsyncTask<String, String, String> {


        String[] paraams = null;
        JSONObject jsonObject;

        @Override
        protected void onPostExecute(String result) {
            //   super.onPostExecute(result);
            System.out.println(result);
            if (result.contains("bhunparakki")) {

                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                try {
                    //TempTrns
                    ErrDesc = jsonObject.getString("ErrDesc");
                    showerrdialoga(ErrDesc);
                    mtd = ipayidtostring(jsonObject.getInt("paymentid"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TempTrans(0);
            } else if (result.contains("success:1")) {
                Paymentsuccess = true;
                //TempTrns
                try {
                    payid = jsonObject.getString("TransId");
                    paystatus = "Success";
                    mtd = ipayidtostring(jsonObject.getInt("paymentid")) + " (" + jsonObject.getString("TransId") + ")";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TempTrans(1);
                paytype = "Wallet";
                SummaryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkRunOneTimeOnly) {
                            SummaryActivity.this.setupfordispense();
                        }
                    }
                });
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
            } else {
                //TempTrns
                try {
                    mtd = ipayidtostring(jsonObject.getInt("paymentid"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TempTrans(0);
                Toast.makeText(getApplication(), "Wrong Qr Code..", Toast.LENGTH_LONG);
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }
                try {
                    ErrDesc = jsonObject.getString("ErrDesc");
                    showerrdialoga(ErrDesc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected String doInBackground(String... params) {
            // Create GetText Metod
            paraams = params;
            String text = "";
            try {

                paymentipay ipay88ewallet = new paymentipay(SummaryActivity.this);
                JSONObject result = ipay88ewallet.Merchant_Scan((chargingprice), params[0], Integer.valueOf(params[1]), SummaryActivity.this);
                jsonObject = result;
                System.out.println("ipay result is : " + result.toString());
                if (result.get("Status").equals("1")) {
                    //  dismiss();
                    text = "success:1";
                } else {
                    return "bhunparakki";
                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }
            // Show response on activity
            return text;
        }
    }
}