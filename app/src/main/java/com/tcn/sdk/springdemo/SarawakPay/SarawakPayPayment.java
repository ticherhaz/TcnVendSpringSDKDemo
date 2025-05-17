package com.tcn.sdk.springdemo.SarawakPay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
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
import com.tcn.sdk.springdemo.tcnSpring.MainActDispenseM4;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

public class SarawakPayPayment {
    private final double Points = 0.00;
    private final String TAG = "SarawakPayPayment";
    private final String remarks = "N/A";
    private final String promname = " ";
    private final long delay = 1000;
    private final Handler handler = new Handler();
    private final SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    private final int startInvoice = 13;
    private final int UserID = 0;
    private final int pid = 0;
    private final int UserStatus = 0;
    private final boolean isloggedin = false;
    private final String payid = "";
    private final String ExpireDate = "";
    private final String discountamt = "";
    private final String vids = "";
    String barcodetext = "";
    CountDownTimer cd;
    private TypeProfuctActivity activity;
    private Dialog customDialogSarawak;
    private List<CartListModel> cartListModelList;
    private EditText et_sarawaypay;
    private SweetAlertDialog pDialog;
    private TextView datee, monthtxt;
    private Transaction transaction;
    private double chargingprice = 0;
    private String ErrDesc = "";
    private DonutProgress donutProgress;
    private AlertDialog alertDialogWaiting;
    private CountDownTimer cdtPayment;
    private RequestQueue requestQueue;
    private PortsandVeriables portsandVeriables;
    private long last_text_edit = 0;
    private int checkRetry = 0;
    private boolean waiting;
    private boolean responded;
    private String fid = "";
    private String mid = "";
    private String productsids = "";
    private Button backbtn;
    private List<CongifModel> congifModels;
    private boolean checkdisablecancel = false;
    private final Runnable input_finish_checker_sarawakpay = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                // TODO: do what you need here
                try {
                    checkdisablecancel = true;
                    String scanText = et_sarawaypay.getText().toString();
                    System.out.println("the code written : " + scanText);
                    backbtn.setEnabled(false);
                    redirectPayment(scanText);
                } catch (Exception ex) {
                }
            }
        }
    };

    public void SarawakPayPayment(TypeProfuctActivity activity, List<CartListModel> cartListModelList,
                                  String fid, String mid, String productsids,
                                  List<CongifModel> congifModels, RequestQueue requestQueue) {
        checkdisablecancel = false;
        this.requestQueue = requestQueue;
        this.activity = activity;
        this.cartListModelList = cartListModelList;
        this.fid = fid;
        this.mid = mid;
        this.productsids = productsids;
        this.congifModels = congifModels;

        waiting = true;
        responded = true;

        if (portsandVeriables == null) {
            portsandVeriables = new PortsandVeriables();
        }

        popoutdialogSummary();
        paymentload();
    }

    private void popoutdialogSummary() {
        try {
            activity.clearCustomDialogDispense();
            customDialogSarawak = activity.getCustomDialogDispense("sarawakpay");

            customDialogSarawak.show();

            for (CartListModel cn : cartListModelList) {
                String log = cn.getItemprice();
                chargingprice = chargingprice + Double.parseDouble(log);
            }

            TextView mtd = customDialogSarawak.findViewById(R.id.mtd);
            TextView pricetext = customDialogSarawak.findViewById(R.id.pricetext);
            pricetext.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
            mtd.setText("SarawakPay");
            backbtn = customDialogSarawak.findViewById(R.id.backbtn);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialogSarawak.dismiss();
                }
            });
        } catch (Exception ex) {
            RollingLogger.i(TAG, "pop out dialog error - " + ex);
        }
    }

    private void paymentload() {
        try {
            activity.isPaymentinProgress(true);
            et_sarawaypay = new EditText(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(80, 80, 80, 80);
            lp.gravity = Gravity.CENTER;

            pDialog = activity.getDialogSarawak("progress");

            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            pDialog.setTitleText("Show your payment QR code to machine to proceed.");
            pDialog.show();

            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            Drawable myIcon = activity.getResources().getDrawable(R.drawable.sarawakpay);
            showmypaymentdialog("SarawakPay", myIcon);

            et_sarawaypay.setInputType(InputType.TYPE_NULL);
            et_sarawaypay.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    handler.removeCallbacks(input_finish_checker_sarawakpay);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        last_text_edit = System.currentTimeMillis();
                        handler.postDelayed(input_finish_checker_sarawakpay, delay);
                    }

                }
            });
        } catch (Exception ex) {
        }
    }

    private void showmypaymentdialog(String mtd, Drawable res) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        et_sarawaypay.setAlpha(0.0f);
        linearLayout.addView(et_sarawaypay);

        pDialog = activity.getDialogSarawak("progress");
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
            pDialog.setTitleText("SHOW YOUR SARAWAKPAY QR TO PROCEED.");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    if (!checkdisablecancel) {
                        pDialog.dismissWithAnimation();
                        backbtn.setEnabled(true);

                        if (customDialogSarawak != null) {
                            if (customDialogSarawak.isShowing()) {
                                customDialogSarawak.dismiss();
                            }
                        }
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            });
            pDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            pDialog.dismiss();
                            customDialogSarawak.dismiss();
                            activity.totalCost(0);
                        }
                    }
            );
            barcodetext = "";
            if (cd != null) cd.cancel();
            cd = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    if (pDialog.getTitleText().equals("SHOW YOUR " + mtd.toUpperCase() + " QR TO PROCEED.")) {
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
            pDialog.show();
        } catch (Exception ex) {
        }

    }

    private void redirectPayment(String barcodetext) {
        // Certificate Initialization
        ISecurity security = new ISecurity();

        try {
            security.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        postPayment(security, barcodetext);
    }

    public void postPayment(final ISecurity iSecurity, String barcodetext) {
        RollingLogger.i(TAG, "post payment api call");
        final AlertDialog alertDialogPayOrder = SarawakMainActivity.showLoading(activity, "Requesting Payment");

        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            final RoomCaller dbCaller = new RoomCaller(activity);
            Transaction latestTransaction = dbCaller.getLatestTransaction();
            SharedPref.init(activity);
            String midShare = SharedPref.read(SharedPref.SarawakPayMid, "");
            String batchNumber = "00001";
            String mid = midShare; //M100089640
            String tid = "KIOSKFOUNDPAD000";
            String traceNo = apiDateTimeFormat.format(new Date());
            String invoiceNumber, merchantBankNo, amount, qrCode, currency, transactionType;
            JSONObject jsonObject = new JSONObject();

            //get new invoice number
            if (latestTransaction != null) {
                if (Integer.valueOf(latestTransaction.getInvoiceNo()) == 999999) { // roll back and increase batch num on hitting 999,999 invoice num
                    invoiceNumber = String.format("%06d", startInvoice);
                } else {
                    invoiceNumber = String.format("%06d", Integer.valueOf(latestTransaction.getInvoiceNo()) + 1);
                }
            } else {
                invoiceNumber = String.format("%06d", startInvoice);
            }

            merchantBankNo = "PMT" + apiDateFormat.format(new Date()) + invoiceNumber;
            currency = "RM";
            transactionType = "1";

            // Message
            try {
                jsonObject.put("merchantId", mid);
                jsonObject.put("qrCode", barcodetext);
                jsonObject.put("curType", currency);
                jsonObject.put("notifyURL", "https://www.google.com/");
                jsonObject.put("merOrderNo", merchantBankNo);
                jsonObject.put("orderAmt", String.valueOf(chargingprice));
                jsonObject.put("goodsName", "Test Item");
                jsonObject.put("detailURL", "");
                jsonObject.put("transactionType", transactionType);
                jsonObject.put("remark", "remark value");
            } catch (Exception e) {
            }

            String requestData = jsonObject.toString().replaceAll("\\\\", "");

            String signedData = iSecurity.sign(requestData);

            if ("".equals(signedData)) {
                return;
            }

            JSONObject signData = new JSONObject(signedData);
            String signature = signData.getString("sign");

            String mnameShared = SharedPref.read(SharedPref.SarawakPayMname, "");
            //insert transaction
            transaction = new Transaction();
            transaction.setBackerName(mnameShared);
            transaction.setBackerPhone("123456789");
            transaction.setBackerEmail("");
            transaction.setCampaignCode("M3");
            transaction.setCampaignCategory("Vending Machine");
            transaction.setCampaignName("Vending Machine");
            transaction.setType("Sale");
            transaction.setKeyIndex("N/A");
            transaction.setSignature(signature);
            transaction.setSignatureMethod("SHA256withRSA");
            transaction.setAmount(String.valueOf(chargingprice));
            transaction.setBankRefNum(merchantBankNo);
            transaction.setBatchNo(batchNumber);
            transaction.setResponseCode("");
            transaction.setResponseCodeDesc("");
            transaction.setCurrency(currency);
            transaction.setCustomerId("");
            transaction.setDisplayMid("");
            transaction.setDisplayTid("");
            transaction.setEntryMode(transactionType);
            transaction.setFunction("");
            transaction.setInvoiceNo(invoiceNumber);
            transaction.setMid(mid);
            transaction.setQrCode(barcodetext);
            transaction.setScheme("SWK");
            transaction.setTid(tid);
            transaction.setTraceNo(traceNo);
            transaction.setTransactionDesc("Core System Technologies (CST)");
            transaction.setTransactionDt(apiDateTimeFormat.format(new Date()));
            transaction.setTransactionId("");
            transaction.setStatus("");
            transaction.setOriInvoiceNo("");
            transaction.setOriBatchNo("");
            transaction.setVoidedAt("");
            transaction.setCreatedAt(timestamp);
            transaction.setUpdatedAt(timestamp);
            dbCaller.insertTransaction(transaction);
            transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());

            //insert debug log
            DebugLog debugLog = new DebugLog();
            debugLog.setBatchNo(batchNumber);
            debugLog.setRequest(requestData);
            debugLog.setCreatedAt(timestamp);
            dbCaller.insertDebugLog(debugLog);
            final DebugLog newInsertLog = dbCaller.getLatestDebugLog();

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.spayglobal.my/").create(RetrofitAPICollection.class);
            Call<String> callOrderPayment = service.orderPayment("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderPayment.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    alertDialogPayOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(response.errorBody().string());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    try {
                        if (response.isSuccessful()) {
                            RollingLogger.i(TAG, "post payment api success");
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                                SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                                processDone();
                            } else {
                                String responseData = iSecurity.decrypt(encryptedResponseData);

                                //update debug log
                                try {
                                    newInsertLog.setResponse(responseData);
                                    dbCaller.updateDebugLogEntry(newInsertLog);
                                } catch (Exception e) {
                                }

                                if (!iSecurity.checkSign(responseData)) {
                                    SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                                    processDone();
                                } else {
                                    JSONObject responseJSON = new JSONObject(responseData);

                                    try {
                                        transaction.setResponseCode(responseJSON.getString("ResStatus") == null ? "" : responseJSON.getString("ResStatus"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setResponseCodeDesc(responseJSON.getString("ResMsg") == null ? "" : responseJSON.getString("ResMsg"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setTransactionDt(responseJSON.getString("orderDate") == null ? "" : responseJSON.getString("orderDate"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setTransactionId(responseJSON.getString("orderNo") == null ? "" : responseJSON.getString("orderNo"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setCustomerId(responseJSON.getString("customerId") == null ? "" : responseJSON.getString("customerId"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setStatus(responseJSON.getString("orderStatus") == null ? "" : responseJSON.getString("orderStatus"));
                                    } catch (Exception e) {
                                    }

                                    transaction.setUpdatedAt(timestamp);
                                    dbCaller.updateTransactionEntry(transaction);
                                    transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());

                                    //textResult.setText(responseData);

                                    if (transaction.getStatus().trim().equals("1")) { //success
                                        processDoneSuccess();
                                    } else if (transaction.getStatus().trim().equals("0")) { //need query
                                        inquirePayment(iSecurity);
                                    } else if (transaction.getStatus().equals("")) { // fail or timeout
                                        processFail();
                                    } else {
                                        processDone();
                                    }
                                }
                            }
                        } else {
                            SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        SarawakMainActivity.updateFailTransactionWithResponseException(response, activity, transaction, e);
                        processDone();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    alertDialogPayOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(t.getMessage());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    SarawakMainActivity.updateFailTransaction(activity, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogPayOrder.dismiss();

            if (transaction != null) {
                SarawakMainActivity.updateFailTransactionException(activity, transaction, e);
                processDone();
            } else {
                SarawakMainActivity.showMessage(activity, "Oops!", "Something went wrong. Please try again.");
            }
        }
    }

    private void processDone() {
        RollingLogger.i(TAG, "processDone");
        RoomCaller dbCaller = new RoomCaller(activity);
        transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(activity, "Payment Success", "Payment status: " + status);
        waiting = true;
        responded = true;

        if (status.equalsIgnoreCase("")) {
            checkdisablecancel = false;
            backbtn.setEnabled(true);
            activity.isPaymentinProgress(false);
            ErrDesc = transaction.getResponseCodeDesc();
            TempTrans(0);
            pDialog = activity.getDialogSarawak("warning");
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            pDialog.setTitleText(transaction.getResponseCodeDesc());
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    pDialog.dismissWithAnimation();

                    Uti.freeMemory();
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
            if (!activity.isFinishing()) {
                pDialog.show();
            }
        } else if (status.equalsIgnoreCase("1")) {
            ErrDesc = "Payment Successfully";
            SarawakMainActivity.alertDialogMessage.dismiss();
            TempTrans(1);
            setupfordispense();
        } else if (status.equalsIgnoreCase("FAIL")) {
            checkdisablecancel = false;
            backbtn.setEnabled(true);
            activity.isPaymentinProgress(false);
            ErrDesc = "Payment Fail";
            RollingLogger.i(TAG, "payment Fail");
            TempTrans(0);
            if (checkRetry == 0) {
                checkRetry++;
                pDialog = activity.getDialogSarawak("warning");
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                pDialog.setTitleText("Fail, Retry");
                pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        pDialog.dismissWithAnimation();
                    }
                });
                if (!activity.isFinishing()) {
                    pDialog.show();
                }
            } else {
                pDialog = activity.getDialogSarawak("warning");
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                pDialog.setTitleText("FAIL");
                pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        RollingLogger.i(TAG, "payment Fail jump back main activity");
                        pDialog.dismissWithAnimation();
                        Uti.freeMemory();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                if (!activity.isFinishing()) {
                    pDialog.show();
                }
            }
        }
    }

    private void inquirePayment(ISecurity iSecurity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_waiting, null);

        donutProgress = dialogView.findViewById(R.id.donut_progress_pop_up_waiting);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel_pop_up_waiting);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);

        alertDialogWaiting = dialogBuilder.create();
        alertDialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!activity.isFinishing()) {
            alertDialogWaiting.show();
        }

        cdtPayment = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                float progress = (millisUntilFinished) / 1000;
                donutProgress.setProgress(progress);
                donutProgress.setText(Math.round(progress) + "s");

                // add time gap 3s
                if (Math.round(progress) % 3 == 0) {
                    if (waiting) {
                        if (responded) {
                            responded = false;

                            postQuery(iSecurity);
                        }
                    } else {
                        cdtPayment.cancel();
                        alertDialogWaiting.dismiss();

                        if (responded) {
                            processDoneSuccess();
                        } else {
                            processFail();
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                waiting = false; //in case ticking escape
                donutProgress.setProgress(0);
                donutProgress.setTextSize(30);
                donutProgress.setText("Time Out!");
                alertDialogWaiting.dismiss();

                String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
                RoomCaller dbCaller = new RoomCaller(activity);
                transaction.setStatus("FAIL");
                transaction.setUpdatedAt(timestamp);
                dbCaller.updateTransactionEntry(transaction);

                postCancel(iSecurity);
            }
        }.start();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdtPayment.cancel();
                alertDialogWaiting.dismiss();

                String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
                RoomCaller dbCaller = new RoomCaller(activity);
                transaction.setStatus("CANCEL");
                transaction.setUpdatedAt(timestamp);
                dbCaller.updateTransactionEntry(transaction);

                processCancel();
            }
        });

        cancelButton.setVisibility(View.GONE);
    }

    private void processDoneSuccess() {
        processDone();
    }

    private void processCancel() {
        backbtn.setEnabled(true);
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(activity, "Payment Canceled!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    private void processFail() {
        backbtn.setEnabled(true);
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(activity, "Payment Fail!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    public void postQuery(final ISecurity iSecurity) {
        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            JSONObject jsonObject = new JSONObject();
            RoomCaller dbCaller = new RoomCaller(activity);

            // Message
            try {
                jsonObject.put("merchantId", transaction.getMid());
                jsonObject.put("merOrderNo", transaction.getBankRefNum());
            } catch (Exception e) {
            }

            String requestData = jsonObject.toString().replaceAll("\\\\", "");

            String signedData = iSecurity.sign(requestData);

            if ("".equals(signedData)) {
                return;
            }

            //insert debug log
            DebugLog debugLog = new DebugLog();
            debugLog.setBatchNo(transaction.getBatchNo());
            debugLog.setRequest(requestData);
            debugLog.setCreatedAt(timestamp);
            dbCaller.insertDebugLog(debugLog);
            final DebugLog newInsertLog = dbCaller.getLatestDebugLog();

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.spayglobal.my/").create(RetrofitAPICollection.class);
            Call<String> callOrderQuery = service.orderQuery("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderQuery.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    //update debug log
                    try {
                        newInsertLog.setResponse(response.errorBody().string());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    try {
                        if (response.isSuccessful()) {
                            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                            } else {
                                String responseData = iSecurity.decrypt(encryptedResponseData);

                                if (!iSecurity.checkSign(responseData)) {
                                    alertDialogWaiting.dismiss();
                                    cdtPayment.cancel();

                                    SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                                    SarawakMainActivity.showMessage(activity, "Oops!", "Invalid Signature. Please contact support.");
                                } else {
//                                    textResult.setText(responseData);
                                    JSONObject responseJSON = new JSONObject(responseData);

                                    //update debug log
                                    try {
                                        newInsertLog.setResponse(responseData);
                                        dbCaller.updateDebugLogEntry(newInsertLog);
                                    } catch (Exception e) {
                                    }

                                    try {
                                        transaction.setResponseCode(responseJSON.getString("ResStatus") == null ? "" : responseJSON.getString("ResStatus"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setResponseCodeDesc(responseJSON.getString("ResMsg") == null ? "" : responseJSON.getString("ResMsg"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setTransactionDt(responseJSON.getString("orderDate") == null ? "" : responseJSON.getString("orderDate"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setTransactionId(responseJSON.getString("orderNo") == null ? "" : responseJSON.getString("orderNo"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setStatus(responseJSON.getString("orderStatus") == null ? "" : responseJSON.getString("orderStatus"));
                                    } catch (Exception e) {
                                    }

                                    transaction.setUpdatedAt(timestamp);
                                    dbCaller.updateTransactionEntry(transaction);
                                    transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());

                                    if (transaction.getStatus().trim().equals("1")) { //success
                                        waiting = false;
                                        responded = true;
                                    } else if (transaction.getStatus().trim().equals("0")) { //need query
                                        responded = true;
                                    } else { // fail or timeout
                                        waiting = false;
                                    }
                                }
                            }
                        } else {
                            alertDialogWaiting.dismiss();
                            cdtPayment.cancel();

                            SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        alertDialogWaiting.dismiss();
                        cdtPayment.cancel();

                        SarawakMainActivity.updateFailTransactionWithResponseException(response, activity, transaction, e);
                        processDone();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    alertDialogWaiting.dismiss();
                    cdtPayment.cancel();

                    //update debug log
                    try {
                        newInsertLog.setResponse(t.getMessage());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    SarawakMainActivity.updateFailTransaction(activity, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogWaiting.dismiss();
            cdtPayment.cancel();

            SarawakMainActivity.updateFailTransactionException(activity, transaction, e);
            processDone();
        }
    }

    private void processTimeOut() {
        RollingLogger.i(TAG, "process time out");
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(activity, "Payment Timeout!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    public void postCancel(final ISecurity iSecurity) {
        RollingLogger.i(TAG, "post cancel");
        final AlertDialog alertDialogCancelOrder = SarawakMainActivity.showLoading(activity, "Cancelling Payment");

        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            JSONObject jsonObject = new JSONObject();
            RoomCaller dbCaller = new RoomCaller(activity);

            // Message
            try {
                jsonObject.put("merchantId", transaction.getMid());
                jsonObject.put("merOrderNo", transaction.getBankRefNum());
            } catch (Exception e) {
            }

            String requestData = jsonObject.toString().replaceAll("\\\\", "");

            String signedData = iSecurity.sign(requestData);

            if ("".equals(signedData)) {
                return;
            }

            //insert debug log
            DebugLog debugLog = new DebugLog();
            debugLog.setBatchNo(transaction.getBatchNo());
            debugLog.setRequest(requestData);
            debugLog.setCreatedAt(timestamp);
            dbCaller.insertDebugLog(debugLog);
            final DebugLog newInsertLog = dbCaller.getLatestDebugLog();

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.spayglobal.my/").create(RetrofitAPICollection.class);
            Call<String> callOrderCancel = service.orderCancel("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderCancel.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    alertDialogCancelOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(response.errorBody().string());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    try {
                        if (response.isSuccessful()) {
                            RollingLogger.i(TAG, "post cancel success");
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                                SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                                processDone();
                            } else {
                                String responseData = iSecurity.decrypt(encryptedResponseData);

                                //update debug log
                                try {
                                    newInsertLog.setResponse(responseData);
                                    dbCaller.updateDebugLogEntry(newInsertLog);
                                } catch (Exception e) {
                                }

                                if (!iSecurity.checkSign(responseData)) {
                                    SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                                    processDone();
                                } else {
                                    JSONObject responseJSON = new JSONObject(responseData);

                                    try {
                                        transaction.setResponseCode(responseJSON.getString("ResStatus") == null ? "" : responseJSON.getString("ResStatus"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setResponseCodeDesc(responseJSON.getString("ResMsg") == null ? "" : responseJSON.getString("ResMsg"));
                                    } catch (Exception e) {
                                    }
                                    try {
                                        transaction.setStatus(responseJSON.getString("orderStatus") == null ? "" : responseJSON.getString("orderStatus"));
                                    } catch (Exception e) {
                                    }

                                    transaction.setUpdatedAt(timestamp);
                                    dbCaller.updateTransactionEntry(transaction);
                                    transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());

//                                    textResult.setText(responseData);

                                    if (transaction.getStatus().trim().equals("4")) { //timeout
                                        processTimeOut();
                                    } else { // fail
                                        processTimeOut();
                                    }
                                }
                            }
                        } else {
                            SarawakMainActivity.updateFailTransactionWithResponse(response, activity, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        SarawakMainActivity.updateFailTransactionWithResponseException(response, activity, transaction, e);
                        processDone();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    alertDialogCancelOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(t.getMessage());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    SarawakMainActivity.updateFailTransaction(activity, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogCancelOrder.dismiss();

            if (transaction != null) {
                SarawakMainActivity.updateFailTransactionException(activity, transaction, e);
                processDone();
            } else {
                SarawakMainActivity.showMessage(activity, "Oops!", "Something went wrong. Please try again.");
            }
        }
    }

    private void setupfordispense() {

        customDialogSarawak.dismiss();
        RollingLogger.i(TAG, "setupfordispense sarawakpay");

        if (activity.getCustomDialogProduct() != null) {
            if (activity.getCustomDialogProduct().isShowing()) {
                activity.getCustomDialogProduct().dismiss();
            }
        }

        String versionName = "";
        try {
            versionName = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String traceNo = apiDateTimeFormat.format(new Date());

        UserObj obj = new UserObj();
        obj.setConfigModel(congifModels);
        obj.setCartModel(cartListModelList);
        obj.setIpaytype("SarawakPay");
        obj.setIsloggedin(isloggedin);
        obj.setUserid(UserID);
        obj.setPoints(Points);
        obj.setPid(pid);
        obj.setExpiredate(ExpireDate);
        obj.setUserstatus(UserStatus);
        obj.setImage(0);
        obj.setMtd("SarawakPay (T" + traceNo + ")" + versionName);
        obj.setChargingprice(chargingprice);

        activity.setEnableaddproduct(false);

//        DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//        dispensePopUpM5.DispensePopUp(activity, obj, "Success", payid, requestQueue);
        Gson gson = new Gson();
        String jsonString = gson.toJson(obj);
        Intent intent = new Intent(activity, MainActDispenseM4.class);
        intent.putExtra("userObjStr", jsonString);

        activity.startActivity(intent);
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
            transactionModel.setAmount(chargingprice);
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(UserID);
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType("SarawakPay");
            transactionModel.setPaymentMethod("SarawakPay");
            transactionModel.setPaymentStatus(Status);
            transactionModel.setFreePoints("");
            transactionModel.setPromocode(promname);
            transactionModel.setPromoAmt(discountamt);
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

            requestQueue.add(myReq);
        } catch (Exception ex) {
        }
    }

    public void alertDialogWaiting() {
        if (alertDialogWaiting != null) {
            if (alertDialogWaiting.isShowing()) {
                alertDialogWaiting.dismiss();
            }
        }
    }
}
