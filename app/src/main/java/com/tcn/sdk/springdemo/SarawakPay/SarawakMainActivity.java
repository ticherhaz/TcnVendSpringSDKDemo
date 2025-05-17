package com.tcn.sdk.springdemo.SarawakPay;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.Utilities.SharedPref;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SarawakMainActivity extends AppCompatActivity {

    public static final SimpleDateFormat databaseDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
    public static final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    public static AlertDialog alertDialogMessage;
    private final int startInvoice = 13;
    private Button buttonDemo;
    private TextView textResult;
    private EditText inputBarcode, inputAmount;
    private DonutProgress donutProgress;
    private AlertDialog alertDialogWaiting;
    private Transaction transaction;
    private CountDownTimer cdtPayment;
    private boolean waiting, responded;

    public static void updateFailTransaction(Context context, Transaction transaction) {
        if (transaction != null) {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            RoomCaller dbCaller = new RoomCaller(context);
            transaction.setStatus("FAIL");
            transaction.setUpdatedAt(timestamp);
            dbCaller.updateTransactionEntry(transaction);
        }
    }

    public static void updateFailTransactionException(Context context, Transaction transaction, Exception exception) {
        if (transaction != null) {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            RoomCaller dbCaller = new RoomCaller(context);
            transaction.setResponseCodeDesc(exception.getMessage());
            transaction.setStatus("FAIL");
            transaction.setUpdatedAt(timestamp);
            dbCaller.updateTransactionEntry(transaction);
        }
    }

    public static String updateFailTransactionWithResponse(Response<String> response, Context context, Transaction transaction) {
        String error = "";

        if (transaction != null) {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            RoomCaller dbCaller = new RoomCaller(context);

            try {
                error = new JSONObject(response.errorBody().string()).getString("message");
            } catch (Exception e) {
            }

            try {
                error = new JSONObject(response.body()).getString("message");
            } catch (Exception e) {
            }

            if (error.trim().isEmpty()) {
                try {
                    error = response.message();
                } catch (Exception e) {
                }
            }

            try {
                transaction.setResponseCode(String.valueOf(response.code()));
            } catch (Exception e) {
            }

            try {
                transaction.setResponseCodeDesc(error);
            } catch (Exception e) {
            }

            transaction.setStatus("FAIL");
            transaction.setUpdatedAt(timestamp);
            dbCaller.updateTransactionEntry(transaction);
        }

        return error;
    }

    public static String updateFailTransactionWithResponseException(Response<String> response, Context context, Transaction transaction, Exception exception) {
        String error = "";

        if (transaction != null) {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            RoomCaller dbCaller = new RoomCaller(context);

            try {
                error = new JSONObject(response.errorBody().string()).getString("message");
            } catch (Exception e) {
            }

            try {
                error = new JSONObject(response.body()).getString("message");
            } catch (Exception e) {
            }

            if (error.trim().isEmpty()) {
                try {
                    error = response.message();
                } catch (Exception e) {
                }
            }

            try {
                transaction.setResponseCode(String.valueOf(response.code()));
            } catch (Exception e) {
            }

            try {
                transaction.setResponseCodeDesc(error + "\nException: " + exception.getMessage());
            } catch (Exception e) {
            }

            transaction.setStatus("FAIL");
            transaction.setUpdatedAt(timestamp);
            dbCaller.updateTransactionEntry(transaction);
        }

        return error;
    }

    public static AlertDialog showLoading(Context context, String title) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_loading, null);

        TextView textTitle = dialogView.findViewById(R.id.text_title_pop_up_loading);
        TextView textMessage = dialogView.findViewById(R.id.text_message_pop_up_loading);
        //Button buttonCancel = dialogView.findViewById(R.id.button_cancel_pop_up_loading);

        textTitle.setText(title);
        textMessage.setText("Please wait...");

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialogLoading = dialogBuilder.create();
        alertDialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogLoading.show();

        /*buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogLoading.dismiss();
            }
        });*/

        return alertDialogLoading;
    }

    public static void showMessage(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_message_no_option, null);

        TextView textTitle = dialogView.findViewById(R.id.text_title_pop_up_message_no_option);
        TextView textMessage = dialogView.findViewById(R.id.text_message_pop_up_message_no_option);
        Button buttonOk = dialogView.findViewById(R.id.button_ok_pop_up_message_no_option);

        textTitle.setText(title);
        textMessage.setText(message);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);

        alertDialogMessage = dialogBuilder.create();
        alertDialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogMessage.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogMessage.dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarawakpay);

        buttonDemo = findViewById(R.id.button_demo_ma);
        textResult = findViewById(R.id.text_result_ma);
        inputBarcode = findViewById(R.id.input_barcode_ma);
        inputAmount = findViewById(R.id.input_amount_ma);

        Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        waiting = true;
        responded = true;

        buttonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo();
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
        }
    }

    private void demo() {
        // Certificate Initialization
        ISecurity security = new ISecurity();

        try {
            security.init();
        } catch (Exception e) {
            //LOG.info("Exception in security.init:" + e.getMessage());
            e.printStackTrace();
        }

        //postPayment(security);
        postQuery1(security);
    }

    public void postQuery1(final ISecurity iSecurity) {
        try {
            JSONObject jsonObject = new JSONObject();
            SharedPref.init(this);
            String midShare = SharedPref.read(SharedPref.SarawakPayMid, "");
            // Message
            try {
                jsonObject.put("merchantId", midShare);
                jsonObject.put("merOrderNo", "PMT20210125000016");
            } catch (Exception e) {
            }

            String requestData = jsonObject.toString().replaceAll("\\\\", "");

            String signedData = iSecurity.sign(requestData);

            if ("".equals(signedData)) {
                return;
            }

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.spayglobal.my/").create(RetrofitAPICollection.class);
            Call<String> callOrderQuery = service.orderQuery("FAPView=JSON&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderQuery.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if (response.isSuccessful()) {
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                            } else {
                                String responseData = iSecurity.decrypt(encryptedResponseData);

                                if (!iSecurity.checkSign(responseData)) {
                                } else {
                                    textResult.setText(responseData);
                                }
                            }
                        } else {
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        } catch (Exception e) {
        }
    }

    public void postPayment(final ISecurity iSecurity) {
        final AlertDialog alertDialogPayOrder = SarawakMainActivity.showLoading(SarawakMainActivity.this, "Requesting Payment");

        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            final RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);
            Transaction latestTransaction = dbCaller.getLatestTransaction();
            String batchNumber = "00001";
            String mid = "M100002101"; //cst test
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
            amount = inputAmount.getText().toString();
            qrCode = inputBarcode.getText().toString();
            currency = "RM";
            transactionType = "1";

            // Message
            try {
                jsonObject.put("merchantId", mid);
                jsonObject.put("qrCode", qrCode);
                jsonObject.put("curType", currency);
                jsonObject.put("notifyURL", "https://www.google.com/");
                jsonObject.put("merOrderNo", merchantBankNo);
                jsonObject.put("orderAmt", amount);
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

            //insert transaction
            transaction = new Transaction();
            transaction.setBackerName("Sapik");
            transaction.setBackerPhone("123456789");
            transaction.setBackerEmail("");
            transaction.setCampaignCode("TBSBMK");
            transaction.setCampaignCategory("health");
            transaction.setCampaignName("bantuan Covid");
            transaction.setType("Sale");
            transaction.setKeyIndex("N/A");
            transaction.setSignature(signature);
            transaction.setSignatureMethod("SHA256withRSA");
            transaction.setAmount(amount);
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
            transaction.setQrCode(qrCode);
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

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.sains.com.my").create(RetrofitAPICollection.class);
            Call<String> callOrderPayment = service.orderPayment("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderPayment.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    alertDialogPayOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(response.errorBody().string());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    try {
                        if (response.isSuccessful()) {
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                                SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
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
                                    SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
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

                                    textResult.setText(responseData);

                                    if (transaction.getStatus().trim().equals("1")) { //success
                                        processDoneSuccess();
                                    } else if (transaction.getStatus().trim().equals("0")) { //need query
                                        inquirePayment(iSecurity);
                                    } else { // fail or timeout
                                        processDone();
                                    }
                                }
                            }
                        } else {
                            SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        SarawakMainActivity.updateFailTransactionWithResponseException(response, SarawakMainActivity.this, transaction, e);
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

                    SarawakMainActivity.updateFailTransaction(SarawakMainActivity.this, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogPayOrder.dismiss();

            if (transaction != null) {
                SarawakMainActivity.updateFailTransactionException(SarawakMainActivity.this, transaction, e);
                processDone();
            } else {
                SarawakMainActivity.showMessage(SarawakMainActivity.this, "Oops!", "Something went wrong. Please try again.");
            }
        }
    }

    public void postQuery(final ISecurity iSecurity) {
        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            JSONObject jsonObject = new JSONObject();
            RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);

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

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.sains.com.my").create(RetrofitAPICollection.class);
            Call<String> callOrderQuery = service.orderQuery("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderQuery.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
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

                                    SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
                                    SarawakMainActivity.showMessage(SarawakMainActivity.this, "Oops!", "Invalid Signature. Please contact support.");
                                } else {
                                    textResult.setText(responseData);
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

                            SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        alertDialogWaiting.dismiss();
                        cdtPayment.cancel();

                        SarawakMainActivity.updateFailTransactionWithResponseException(response, SarawakMainActivity.this, transaction, e);
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

                    SarawakMainActivity.updateFailTransaction(SarawakMainActivity.this, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogWaiting.dismiss();
            cdtPayment.cancel();

            SarawakMainActivity.updateFailTransactionException(SarawakMainActivity.this, transaction, e);
            processDone();
        }
    }

    public void postCancel(final ISecurity iSecurity) {
        final AlertDialog alertDialogCancelOrder = SarawakMainActivity.showLoading(SarawakMainActivity.this, "Cancelling Payment");

        try {
            String timestamp = SarawakMainActivity.databaseDateTimeFormat.format(new Date());
            JSONObject jsonObject = new JSONObject();
            RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);

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

            RetrofitAPICollection service = RetrofitClient.getRetrofitClient("https://xservice.sains.com.my").create(RetrofitAPICollection.class);
            Call<String> callOrderCancel = service.orderCancel("FAPView=JSON&version=1.0&formData=" + iSecurity.encrypt(signedData).replaceAll("\\+", "%2B"));
            callOrderCancel.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    alertDialogCancelOrder.dismiss();

                    //update debug log
                    try {
                        newInsertLog.setResponse(response.errorBody().string());
                        dbCaller.updateDebugLogEntry(newInsertLog);
                    } catch (Exception e) {
                    }

                    try {
                        if (response.isSuccessful()) {
                            String encryptedResponseData = response.body();

                            if ("".equals(encryptedResponseData)) {
                                SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
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
                                    SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
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

                                    textResult.setText(responseData);

                                    if (transaction.getStatus().trim().equals("4")) { //timeout
                                        processTimeOut();
                                    } else { // fail
                                        processTimeOut();
                                    }
                                }
                            }
                        } else {
                            SarawakMainActivity.updateFailTransactionWithResponse(response, SarawakMainActivity.this, transaction);
                            processDone();
                        }
                    } catch (Exception e) {
                        SarawakMainActivity.updateFailTransactionWithResponseException(response, SarawakMainActivity.this, transaction, e);
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

                    SarawakMainActivity.updateFailTransaction(SarawakMainActivity.this, transaction);
                    processDone();
                }
            });
        } catch (Exception e) {
            alertDialogCancelOrder.dismiss();

            if (transaction != null) {
                SarawakMainActivity.updateFailTransactionException(SarawakMainActivity.this, transaction, e);
                processDone();
            } else {
                SarawakMainActivity.showMessage(SarawakMainActivity.this, "Oops!", "Something went wrong. Please try again.");
            }
        }
    }

    private void inquirePayment(ISecurity iSecurity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SarawakMainActivity.this);
        LayoutInflater inflater = (LayoutInflater) SarawakMainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.pop_up_waiting, null);

        donutProgress = dialogView.findViewById(R.id.donut_progress_pop_up_waiting);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel_pop_up_waiting);

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);

        alertDialogWaiting = dialogBuilder.create();
        alertDialogWaiting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogWaiting.show();

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
                RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);
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
                RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);
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

    private void processDone() {
        RoomCaller dbCaller = new RoomCaller(SarawakMainActivity.this);
        transaction = dbCaller.getTransactionByInvoiceNoAndBatchNo(transaction.getInvoiceNo(), transaction.getBatchNo());
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(SarawakMainActivity.this, "Payment Success", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    private void processTimeOut() {
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(SarawakMainActivity.this, "Payment Timeout!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    private void processCancel() {
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(SarawakMainActivity.this, "Payment Canceled!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }

    private void processFail() {
        String status = transaction.getStatus();
        SarawakMainActivity.showMessage(SarawakMainActivity.this, "Payment Fail!", "Payment status: " + status);
        waiting = true;
        responded = true;
    }
}
