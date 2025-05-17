package com.tcn.sdk.springdemo.PayWave;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.gkash.me51sdk.SerialComCallback;
import com.gkash.me51sdk.TransactionDetailCallBack;
import com.gkash.me51sdk.TransactionInfo;
import com.gkash.me51sdk.UsbConnection;
import com.gkash.me51sdk.UsbService;
import com.gkash.me51sdk.UserLoginCallBack;
import com.gkash.me51sdk.module.RequestMessage;
import com.gkash.me51sdk.module.ResponseMessage;
import com.gkash.me51sdk.module.TransactionDetail;
import com.google.gson.Gson;
import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.tcnSpring.MainActDispenseM4;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PaywaveSummaryPopUp {
    public static UsbService usbService;
    public static String formatDate = "";
    private final String TAG = "PaywaveSummaryPopUp";
    Button submit, ready, Auth, Cancel, txnDetail, Connect;
    String AuthToken = "";
    String log = "";
    String A = "";
    SweetAlertDialog pDialog;
    private TextView display;
    private EditText editText;
    private ProgressBar loading;
    private Button backbtn;
    private TextView total;
    private String totalprice = "", type = "", typechoose = "";
    private TextView tvpaywave;
    private TypeProfuctActivity activity;
    private Dialog customDialogDispense, customDialogPaywaveChoose;
    private Boolean checkReady = false;
    private List<CartListModel> cartListModelList;
    private Handler handler;
    private List<CongifModel> congifModels = new ArrayList<>();
    private RequestQueue requestQueue;
    private boolean checkdisablecancel = false;

    public void summaryPopUp(TypeProfuctActivity activity1, String totalprice1, String type1, String typechoose1, UserObj userObj,
                             Dialog customDialogPaywaveChoose, RequestQueue requestQueue) {
        RollingLogger.i(TAG, "paywavesummarypopup start");
        checkdisablecancel = false;
        this.requestQueue = requestQueue;
        activity = activity1;
        this.cartListModelList = userObj.getCartModel();
        this.customDialogPaywaveChoose = customDialogPaywaveChoose;
        this.congifModels = userObj.getConfigModel();
        activity.clearCustomDialogDispense();
        customDialogDispense = activity1.getCustomDialogDispense("paywave");
        if (!activity.isFinishing()) {
            customDialogDispense.show();
        }

        EditText Type, cartid, email, currency, QRMethod, settlement, phoneNo, Amount, username, password, poremid;

        TextView txt = customDialogDispense.findViewById(R.id.textView);
        loading = customDialogDispense.findViewById(R.id.progressBar);
        Type = customDialogDispense.findViewById(R.id.TxnType);
        cartid = customDialogDispense.findViewById(R.id.CartId);
        email = customDialogDispense.findViewById(R.id.Email);
        currency = customDialogDispense.findViewById(R.id.Currency);
        QRMethod = customDialogDispense.findViewById(R.id.QRMethod);
        settlement = customDialogDispense.findViewById(R.id.SettlementIndex);
        phoneNo = customDialogDispense.findViewById(R.id.Phone);
        Amount = customDialogDispense.findViewById(R.id.Amount);
        tvpaywave = customDialogDispense.findViewById(R.id.tvpaywave);

        backbtn = customDialogDispense.findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCall();
                RollingLogger.i(TAG, "back button clicked");
                customDialogDispense.dismiss();
                activity.clearCustomDialogDispense();
//                if(pDialogQr!=null){
//                    pDialogQr.dismiss();
//                }
            }
        });
        usbService = UsbService.getInstance();

        totalprice = totalprice1;
        type = type1;
        typechoose = typechoose1;
        TextView scantext = customDialogDispense.findViewById(R.id.scantext);
        if (typechoose.equalsIgnoreCase("paywave")) {
            RollingLogger.i(TAG, "type choose paywave");

            tvpaywave.setText("payWave Scan");
            ImageView iv_scan = customDialogDispense.findViewById(R.id.iv_scan);
            iv_scan.setBackground(activity.getResources().getDrawable(R.drawable.creditc));
            scantext.setText("Hold your payment card with security chip on top and tap on the payment device below.");
            try {
                pDialog = activity.getDialogPaywave("progress");
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
                pDialog.setTitleText("SHOW YOUR PAYWAVE SCAN TO PROCEED.");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (!checkdisablecancel) {
                            pDialog.dismissWithAnimation();
                            backbtn.setEnabled(true);

                            if (customDialogDispense != null) {
                                if (customDialogDispense.isShowing()) {
                                    customDialogDispense.dismiss();
                                }
                            }

                            if (customDialogPaywaveChoose != null) {
                                if (customDialogPaywaveChoose.isShowing()) {
                                    customDialogPaywaveChoose.dismiss();
                                }
                            }
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                });
                if (!activity.isFinishing())
                    pDialog.show();//showing the dialog
            } catch (Exception ex) {
            }
        } else {
            RollingLogger.i(TAG, "type choose qr scan");

            tvpaywave.setText("QR scan");
            scantext.setText("Open your e-Wallet and scan the QR code display on the payment reader below.");

            try {
                pDialog = activity.getDialogPaywave("progress");
                //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
                pDialog.setTitleText("Scan the QR code display on the payment device below.");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismissWithAnimation();
                        backbtn.setEnabled(true);
                    }
                });
                if (!activity.isFinishing())
                    pDialog.show();//showing the dialog
            } catch (Exception ex) {
            }
        }
        RollingLogger.i(TAG, "total price" + String.format("%.2f", Double.valueOf(totalprice)));
        loading.setVisibility(View.VISIBLE);
        TextView total = customDialogDispense.findViewById(R.id.total);
        total.setText("TOTAL : RM " + String.format("%.2f", Double.valueOf(totalprice)));

        usbService.findDevice(activity, new UsbConnection() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                txt.setText(responseMessage.getAll());

//                        handler.removeCallbacksAndMessages(null);
                        checkdisablecancel = true;
                        RollingLogger.i(TAG, "ready Call");
                        activity.isPaymentinProgress(true);
                        readyCall();
                        backbtn.setEnabled(false);
                    }
                });
            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        backbtn.setEnabled(true);
                        txt.setText(responseMessage.getAll());
                        loading.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void isConnected(boolean isconnect) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isconnect) {
                            Toast.makeText(activity, "USB connection is dropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//            }
//        });


        submit = customDialogDispense.findViewById(R.id.Submitbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "submit clicked");
                if (!Amount.getText().toString().isEmpty()) {
                    BigDecimal amount = new BigDecimal(Amount.getText().toString());
                    RequestMessage requestMessage = new RequestMessage(Type.getText().toString(), cartid.getText().toString(), email.getText().toString(), currency.getText().toString(), amount, QRMethod.getText().toString(), settlement.getText().toString(), phoneNo.getText().toString());
                    usbService.send(requestMessage, new SerialComCallback() {
                        @Override
                        public void onSuccess(ResponseMessage responseMessage) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setText(responseMessage.getAll());
                                    //A += responseMessage.getResponseText();
                                }
                            });
                        }

                        @Override
                        public void onFail(ResponseMessage responseMessage) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setText(responseMessage.getAll());
                                }
                            });
                        }
                    });
                }
            }
        });

        ready = customDialogDispense.findViewById(R.id.Readybtn);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "ready clicked");

                String amountInput = "1.00";
                BigDecimal amount = new BigDecimal(amountInput);
                RequestMessage requestMessage = new RequestMessage("00", "test1231221", "junesung@gkash.com", "MYR", amount, "12", "", "");
                usbService.send(requestMessage, new SerialComCallback() {
                    @Override
                    public void onSuccess(ResponseMessage responseMessage) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                                //log += responseMessage.getResponseText();
                            }
                        });

                    }

                    @Override
                    public void onFail(ResponseMessage responseMessage) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }
                });
            }
        });

        Cancel = customDialogDispense.findViewById(R.id.cancelbtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "cancel clicked");

                String amountInput = "1.00";
                BigDecimal amount = new BigDecimal(amountInput);
                RequestMessage requestMessage = new RequestMessage("99", "test1231221", "junesung@gkash.com", "MYR", amount, "12", "", "");
                usbService.send(requestMessage, new SerialComCallback() {
                    @Override
                    public void onSuccess(ResponseMessage responseMessage) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }

                    @Override
                    public void onFail(ResponseMessage responseMessage) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }
                });
            }


        });

        username = customDialogDispense.findViewById(R.id.username);
        password = customDialogDispense.findViewById(R.id.password);
        Auth = customDialogDispense.findViewById(R.id.Authbtn);
        Auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionInfo transaction = new TransactionInfo();
                transaction.GetAuthToken(username.getText().toString(), password.getText().toString(), new UserLoginCallBack() {
                    @Override
                    public void onSuccess(String responseMessage) {
                        txt.setText(responseMessage);
                        AuthToken = responseMessage;
                    }

                    @Override
                    public void onFail(String responseMessage) {
                        txt.setText(responseMessage);
                    }

                });

            }
        });

        poremid = customDialogDispense.findViewById(R.id.PORemID);
        txnDetail = customDialogDispense.findViewById(R.id.Txnbtn);
        txnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactionInfo transaction = new TransactionInfo();
                transaction.GetTransactionDetails(AuthToken, poremid.getText().toString(), new TransactionDetailCallBack() {
                    @Override
                    public void onSuccess(TransactionDetail detail) {
                        //txt.setText(detail.getTransaction().getCardNo() + "  " + detail.getTransaction().getCardtype() + "  " + detail.getTransaction().getRRemID());
                        String sample = detail.GetHTMLReceipt();
                        txt.setText(detail.GetHTMLReceipt());
                    }

                    @Override
                    public void onFail(String responseMessage) {
                        txt.setText(responseMessage);
                    }

                });
            }
        });

    }

    private void readyCall() {
        String amountInput = totalprice;
        BigDecimal amount = new BigDecimal(amountInput);
        Date now = new Date();
        formatDate = new SimpleDateFormat("yyMMddHHmmssS", Locale.ENGLISH).format(now);
//        String type1 = returnType();
        RequestMessage requestMessage = new RequestMessage("00", formatDate, "", "MYR", amount, "12", "", "");
        usbService.send(requestMessage, new SerialComCallback() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RollingLogger.i(TAG, "ready Call api success");

                        log += responseMessage.getResponseText();
//                        if(!checkReady) {
                        checkReady = true;
                        SubmitCall();
//                        }
                    }
                });

            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.isPaymentinProgress(false);
                        backbtn.setEnabled(true);
                        loading.setVisibility(View.INVISIBLE);
                        if (pDialog != null)
                            pDialog.dismissWithAnimation();
                    }
                });
            }
        });
    }

    private void SubmitCall() {
        RollingLogger.i(TAG, "submit Call");

        if (!totalprice.equals("")) {
            BigDecimal amount = new BigDecimal(totalprice);
            Date now = new Date();
            String type1 = "";
            if (typechoose.equalsIgnoreCase("wallet")) {
                type1 = returnType();
            }
            //String formatDate = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH).format(now);
            String txntype = "";
            if (typechoose.equalsIgnoreCase("paywave")) {
                txntype = "01";
            } else {
                txntype = "11";
            }
            RollingLogger.i(TAG, "submit Call-" + typechoose);

            RequestMessage requestMessage = new RequestMessage(txntype, "GK" + formatDate, "", "MYR", amount, type1, "02", "");
            usbService.send(requestMessage, new SerialComCallback() {
                @Override
                public void onSuccess(ResponseMessage responseMessage) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            txt.setText(responseMessage.getAll());
                            //A += responseMessage.getResponseText();
                            RollingLogger.i(TAG, "submit Call success");
                            if (pDialog != null)
                                pDialog.dismissWithAnimation();
                            loading.setVisibility(View.INVISIBLE);
                            String result = responseMessage.getResponseText();
                            result = result.trim();
                            if (result.equalsIgnoreCase("SUCCESS")) {

                                if (typechoose.equalsIgnoreCase("paywave")) {
                                    //customDialog.dismiss();
                                }

                                String paytype = "";
                                if (typechoose.equalsIgnoreCase("paywave")) {
                                    paytype = "PW." + responseMessage.getCardType().trim() + " (GK" + formatDate + ")";
                                } else {
                                    switch (type) {
                                        case "touch":
                                            paytype = "GK-tngo " + "(GK" + formatDate + ")";
                                            break;
                                        case "boost":
                                            paytype = "GK-boost " + "(GK" + formatDate + ")";
                                            break;
                                        case "ali":
                                            paytype = "GK-ali " + "(GK" + formatDate + ")";
                                            break;
                                        case "maybank":
                                            paytype = "GK-maybank " + "(GK" + formatDate + ")";
                                            break;
                                        case "wechat":
                                            paytype = "GK-wechat " + "(GK" + formatDate + ")";
                                            break;
                                        case "grab":
                                            paytype = "GK-grab " + "(GK" + formatDate + ")";
                                            break;
                                    }
                                }
                                RollingLogger.i(TAG, "submit Call success-paytype-" + paytype);
                                Double point = 0.00;
                                int pid = 0, ustatus = 0;

                                if (typechoose.equalsIgnoreCase("paywave")) {
                                    customDialogDispense.dismiss();
                                }
                                if (activity.getCustomDialogProduct() != null) {
                                    if (activity.getCustomDialogProduct().isShowing()) {
                                        activity.getCustomDialogProduct().dismiss();
                                    }
                                }

                                if (customDialogPaywaveChoose != null) {
                                    if (customDialogPaywaveChoose.isShowing()) {
                                        customDialogPaywaveChoose.dismiss();
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
                                obj.setIpaytype(paytype);
                                obj.setIsloggedin(false);
                                obj.setMtd("Paywave (T" + traceNo + ")" + versionName);
                                obj.setChargingprice(Double.valueOf(totalprice));
                                obj.setPoints(point);
                                obj.setUserid(0);
                                obj.setPid(pid);
                                obj.setExpiredate("");
                                obj.setUserstatus(ustatus);
                                obj.setCartModel(cartListModelList);
                                obj.setConfigModel(congifModels);

                                activity.setEnableaddproduct(false);

                                Gson gson = new Gson();
                                String jsonString = gson.toJson(obj);
                                Intent intent = new Intent(activity, MainActDispenseM4.class);
                                intent.putExtra("userObjStr", jsonString);

                                activity.startActivity(intent);
//                                DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//                                dispensePopUpM5.DispensePopUp(activity, obj, "Success", "", requestQueue);
                            } else {
                                checkdisablecancel = false;
                                backbtn.setEnabled(true);
                                activity.isPaymentinProgress(false);
                                pDialog = activity.getDialogPaywave("warning");
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                                pDialog.setTitleText(result);
                                pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        pDialog.dismissWithAnimation();
                                    }
                                });
                                pDialog.show();
                            }
                        }
                    });
                }

                @Override
                public void onFail(ResponseMessage responseMessage) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            txt.setText(responseMessage.getAll());
//                            if(pDialogQr!=null)
//                                pDialogQr.dismissWithAnimation();
                            loading.setVisibility(View.INVISIBLE);
                            backbtn.setEnabled(true);
                            activity.isPaymentinProgress(false);
                        }
                    });
                }
            });
        }
    }

    private String returnType() {
        String num = "";
        switch (type) {
            case "boost":
                num = "2";
                break;
            case "wechat":
                num = "4";
                break;
            case "grab":
                num = "6";
                break;
            case "maybank":
                num = "9";
                break;
            case "ali":
                num = "10";
                break;
            case "touch":
                num = "14";
                break;
        }
        return num;
    }

    private void cancelCall() {
//        String amountInput = "1.00";
        RollingLogger.i(TAG, "cancel api Call");

        BigDecimal amount = new BigDecimal(totalprice);
        RequestMessage requestMessage = new RequestMessage("99", formatDate, "", "MYR", amount, "12", "", "");
        usbService.send(requestMessage, new SerialComCallback() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt.setText(responseMessage.getAll());
                        RollingLogger.i(TAG, "cancel api Call success");
                        Log.d("test", "tests-" + responseMessage.toString());
                    }
                });
            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test", "testf-" + responseMessage.toString());
                        //txt.setText(responseMessage.getAll());
                    }
                });
            }
        });
    }

    private void usbRetry() {
        usbService.findDevice(activity, new UsbConnection() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                txt.setText(responseMessage.getAll());
                        readyCall();
                    }
                });
            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        SweetAlertDialog pDialogRetry = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
//                        pDialogRetry.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
//                        pDialogRetry.setTitleText("Retry");
//                        pDialogRetry.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                pDialogRetry.dismissWithAnimation();
//                            }
//                        });
//                        pDialogRetry.show();
                    }
                });
            }

            @Override
            public void isConnected(boolean isconnect) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isconnect) {
                            Toast.makeText(activity, "USB connection is dropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
