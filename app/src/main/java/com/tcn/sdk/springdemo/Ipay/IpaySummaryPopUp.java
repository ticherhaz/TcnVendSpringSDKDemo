package com.tcn.sdk.springdemo.Ipay;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
//import com.tcn.sdk.springdemo.ComAssistant.DispensePopUpM5;
import com.tcn.sdk.springdemo.Dispense.AppLogger;
import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.TempTrans;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.PortsandVeriables;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.Utilities.Uti;
import com.tcn.sdk.springdemo.paymentipay;
import com.tcn.sdk.springdemo.tcnSpring.MainActDispenseM4;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class IpaySummaryPopUp {
    private final String TAG = "IpaySummaryPopUp";
    private final long delay = 1000;
    private final Handler handler = new Handler();
    public Dialog customDialogDispense;
    public SweetAlertDialog pDialog, globalDialog;
    String paymentmethod = "";
    String barcodetext = "";
    CountDownTimer cd;
    Boolean Paymentsuccess = false;
    String ErrDesc = "Payment Successful";
    private TypeProfuctActivity activity;
    private UserObj userObj;
    private PortsandVeriables portsandVeriables;
    private TextView txtmtd, ttltxt;
    private Button backbtn;
    private String LPayment = "", type = "", fid, mid, productsids = "", vids = "", paytype = "", mtd,
            merchantcode, merchantkey, payid, paystatus;
    private boolean isloggedin = false, isVocher = false, isuserpaying = false, checkRunOneTimeOnly = false;
    private double rrp = 0.00;
    private EditText et;
    private long last_text_edit = 0;
    private RequestQueue requestQueue;
    private boolean checkdisablecancel = false;
    private boolean checkNotAbleScan = false;
    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                try {
                    checkdisablecancel = true;
                    String maybankReplace = et.getText().toString();

                    Log.d("test", "test-" + maybankReplace);

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
                    }
                    RollingLogger.i(TAG, "call api");
                    if (!checkNotAbleScan) {
                        new CallAPI().execute(maybankReplace, "0", userObj.getChargingprice() + "");
                        checkNotAbleScan = true;
                    }
//                        if (pDialog.getTitleText().toLowerCase().contains("touch'n go")) {
//                            new CallAPI().execute(et.getText().toString(), "336", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("boost")) {
//                            new CallAPI().execute(et.getText().toString(), "320", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("maybank")) {
//                            new CallAPI().execute(maybankReplace, "354", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("wechat")) {
//                            new CallAPI().execute(et.getText().toString(), "343", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("wechat (cny)")) {
//                            new CallAPI().execute(et.getText().toString(), "305", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("alipay")) {
//                            new CallAPI().execute(et.getText().toString(), "234", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("grabpay")) {
//                            new CallAPI().execute(et.getText().toString(), "379", userObj.getChargingprice() + "");
//                        }
//                        if (pDialog.getTitleText().toLowerCase().contains("shopee")) {//for shopee
//                            new CallAPI().execute(et.getText().toString(), "19", userObj.getChargingprice() + "");
//                        } else {
//
//                        }
                    System.out.println("the code written : " + et.getText());
                    pDialog.setTitle("Processing..");
//                    }else{
//                        activity.isPaymentinProgress(false);
//                    }
                } catch (Exception ex) {

                }
            }
        }
    };

    public void IpaySummaryPopUp(UserObj userobj, TypeProfuctActivity activity, Dialog customDialogDispense, RequestQueue requestQueue) {
        checkdisablecancel = false;
        this.requestQueue = requestQueue;
        this.customDialogDispense = customDialogDispense;
        this.activity = activity;
        this.userObj = userobj;
        this.mtd = userobj.getMtd();

        RollingLogger.i(TAG, "Start Summary Payment Activity");

        SharedPref.init(activity);
        LPayment = SharedPref.read(SharedPref.Lpayment, "");
        type = userobj.getIpaytype();
        portsandVeriables = new PortsandVeriables();
        setupDialog();
        setupLayoutId();
        setUpValue();
    }

    private void setupDialog() {
        activity.clearCustomDialogDispense();
        customDialogDispense = activity.getCustomDialogDispense("summary");

        if (!activity.isFinishing()) {
            customDialogDispense.show();
        }
    }

    private void setupLayoutId() {
        txtmtd = customDialogDispense.findViewById(R.id.mtd);
        ttltxt = customDialogDispense.findViewById(R.id.pricetext);
        backbtn = customDialogDispense.findViewById(R.id.backbtn);
    }

    private void setUpValue() {
        for (CongifModel cn : userObj.getConfigModel()) {
            fid = cn.getFid();
            mid = cn.getMid();
            merchantcode = cn.getMerchantcode();
            merchantkey = cn.getMerchantkey();
        }
        for (CartListModel cart : userObj.getCartModel()) {
            RollingLogger.i(TAG, "cart item number-" + cart.getItemnumber());
            RollingLogger.i(TAG, "cart item qty-" + cart.getItemqty());
            RollingLogger.i(TAG, "cart item name-" + cart.getItemname());
            RollingLogger.i(TAG, "cart item price-" + cart.getItemprice());

            int qty;
            qty = Integer.parseInt(cart.getItemqty());
            for (int x = 0; x < qty; x++) {
                productsids += cart.getProdid() + ",";
            }
            if (isloggedin) {
                rrp += ((Double.parseDouble(cart.itemprice) * Double.parseDouble(cart.rrp_percent)) / 100);

                if (cart.getVoucher() != null) {
                    vids += cart.getVoucher() + ",";
                    isVocher = true;
                }
            }
        }
        isloggedin = userObj.getIsloggedin();

        RollingLogger.i(TAG, "TOTAL : RM " + String.format("%.2f", userObj.getChargingprice()));
        ttltxt.setText("TOTAL : RM " + String.format("%.2f", userObj.getChargingprice()));
        txtmtd.setText(mtd);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.finish();
                Button pay = activity.getpaybuttonenable();

                pay.setEnabled(true);
                activity.isPaymentinProgress(true);
                activity.clearCustomDialogDispense();
                activity.setEnableaddproduct(true);
                customDialogDispense.dismiss();
                RollingLogger.i(TAG, "back button clicked");
            }
        });
        checkIfInternetProceed();
    }

    private void checkIfInternetProceed() {
        if (!Uti.chkinternet(activity)) {
            if (globalDialog != null) {
                globalDialog.dismissWithAnimation();
            }

            globalDialog = activity.getDialogIpayGlobal("warning")
                    .setTitleText("No internet")
                    .setContentText("Please contact careline");
            if (!activity.isFinishing()) {
                globalDialog.show();
            }
        } else {
            paymentload();
        }
    }

    void paymentload() {
        try {
            backbtn.setEnabled(false);
            activity.isPaymentinProgress(true);
            RollingLogger.i(TAG, "paymentload function");
            RollingLogger.i(TAG, "mtd-" + mtd);

            isuserpaying = true;
            et = new EditText(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(80, 80, 80, 80);
            lp.gravity = Gravity.CENTER;

            pDialog = activity.getDialogIpay();
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            pDialog.setTitleText("Show your payment QR code to machine to proceed.");

            final LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            if (mtd.equals("Paywave")) {
                if (globalDialog != null) {
                    globalDialog.dismissWithAnimation();
                }

                globalDialog = activity.getDialogIpayGlobal("progress");
                globalDialog.setTitleText("Please Tap your card on Paywave Device.");
                if (!activity.isFinishing()) {
                    globalDialog.show();
                }
                RollingLogger.i(TAG, "paywave dialog");
            } else if (mtd.equals("Member Points")) {
                Paymentsuccess = true;
                paytype = "RRP";
                rrp = 0.00;
                setupfordispense();
                RollingLogger.i(TAG, "setupfordispense");
            } else {
                Log.d("???", "not memberpont:: " + mtd);
                RollingLogger.i(TAG, "show payment dialog");
                Drawable myIcon = activity.getResources().getDrawable(userObj.getImage());
                showmypaymentdialog(mtd, myIcon);
            }

            et.setInputType(InputType.TYPE_NULL);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (handler != null) {
                        handler.removeCallbacks(input_finish_checker);
                    }
                    // pDialog.setTitle("Please Wait verifying code..");
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        last_text_edit = System.currentTimeMillis();
                        if (handler != null) {
                            handler.postDelayed(input_finish_checker, delay);
                        }
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

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(activity);
            }
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = portsandVeriables.com + "TempTrans";
            TempTrans transactionModel = new TempTrans();
            transactionModel.setAmount(userObj.getChargingprice());
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(userObj.getUserid());
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType(mtd);
            transactionModel.setPaymentMethod(paytype);
            transactionModel.setPaymentStatus(Status);
            transactionModel.setFreePoints(String.valueOf(rrp));
            transactionModel.setPromocode(userObj.getPromname());
            transactionModel.setPromoAmt(userObj.getPromoamt() + "");
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
        final LinearLayout linearLayout = new LinearLayout(activity);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setAlpha(0.0f);
        linearLayout.addView(et);

        if (pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismissWithAnimation();
            }
        }
        pDialog = activity.getDialogIpay();
        pDialog.setContentText("");

//        pDialog.setConfirmButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                pDialog.dismissWithAnimation();
//            }
//        });
        try {
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
            pDialog.setTitleText("Show your QR Code to the scanner.");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    if (!checkdisablecancel) {
                        pDialog.dismissWithAnimation();
                        if (cd != null) cd.cancel();
                        backbtn.setEnabled(true);
                        activity.enableSelectionProduct();
                        if (customDialogDispense != null) {
                            if (customDialogDispense.isShowing()) {
                                customDialogDispense.dismiss();
                            }
                        }
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }

                }
            });
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
            if (!activity.isFinishing()) {
                pDialog.show();
            }
        } catch (Exception ex) {
        }

    }

    private void showerrdialoga(String err) {
        try {
            globalDialog.dismissWithAnimation();
        } catch (Exception Ex) {

        }
        isuserpaying = false;
        if (globalDialog != null) {
            globalDialog.dismissWithAnimation();
        }

        globalDialog = activity.getDialogIpayGlobal("error").setTitleText("Error Found!").setContentText(err);
        globalDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        if (!activity.isFinishing()) {
            globalDialog.show();
        }
    }

    private void setupfordispense() {
        RollingLogger.i(TAG, "DispensePopUpM5.class call");

        checkRunOneTimeOnly = true;
        customDialogDispense.dismiss();
        if (activity.getCustomDialogProduct() != null) {
            if (activity.getCustomDialogProduct().isShowing()) {
                activity.getCustomDialogProduct().dismiss();
            }
        }
        SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String traceNo = apiDateTimeFormat.format(new Date());

        String versionName = "";
        try {
            versionName = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        activity.setEnableaddproduct(false);
        userObj.setMtd(userObj.getMtd() + " (" + payid + ") " + versionName);

//        DispensePopUpNew dispensePopUpNew = new DispensePopUpNew();
//        dispensePopUpNew.DispensePopUp(activity, userObj, paystatus, payid, requestQueue);
//        DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//        dispensePopUpM5.DispensePopUp(activity, userObj, paystatus, payid, requestQueue);
        Gson gson = new Gson();
        String jsonString = gson.toJson(userObj);
        Intent intent = new Intent(activity, MainActDispenseM4.class);
        intent.putExtra("userObjStr", jsonString);

        activity.startActivity(intent);

    }

    private class CallAPI extends AsyncTask<String, String, String> {


        String[] paraams = null;
        JSONObject jsonObject;

        @Override
        protected void onPostExecute(String result) {
            //   super.onPostExecute(result);
            System.out.println(result);
            if (result.contains("bhunparakki")) {
                checkNotAbleScan = false;
                checkdisablecancel = false;
                backbtn.setEnabled(true);
                activity.isPaymentinProgress(false);
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!checkRunOneTimeOnly) {
                            setupfordispense();
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
                checkNotAbleScan = false;
                checkdisablecancel = false;
                backbtn.setEnabled(true);
                activity.isPaymentinProgress(false);
                try {
                    mtd = ipayidtostring(jsonObject.getInt("paymentid"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ErrDesc = "";

                TempTrans(0);
                Toast.makeText(activity, "Wrong Qr Code..", Toast.LENGTH_LONG);
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
                paymentipay ipay88ewallet = new paymentipay();

                Log.d("???", "merchantcode:: " + merchantcode);
                Log.d("???", "merchantkey:: " + merchantkey);

                JSONObject result = ipay88ewallet.Merchant_ScanNew((userObj.getChargingprice()), params[0], Integer.valueOf(params[1]),
                        activity, merchantcode, merchantkey, mid);
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
