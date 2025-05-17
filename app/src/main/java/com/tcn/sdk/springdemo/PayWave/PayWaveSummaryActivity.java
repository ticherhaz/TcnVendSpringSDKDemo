package com.tcn.sdk.springdemo.PayWave;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gkash.me51sdk.SerialComCallback;
import com.gkash.me51sdk.TransactionDetailCallBack;
import com.gkash.me51sdk.TransactionInfo;
import com.gkash.me51sdk.UsbConnection;
import com.gkash.me51sdk.UsbService;
import com.gkash.me51sdk.UserLoginCallBack;
import com.gkash.me51sdk.module.RequestMessage;
import com.gkash.me51sdk.module.ResponseMessage;
import com.gkash.me51sdk.module.TransactionDetail;
import com.tcn.sdk.springdemo.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PayWaveSummaryActivity extends AppCompatActivity {

    Button submit, ready, Auth, Cancel, txnDetail, Connect;
    String AuthToken = "";
    String log = "";
    String A = "";
    SweetAlertDialog pDialog, pDialogQr;
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private String totalprice = "", type = "", typechoose = "";
    private ProgressBar loading;
    private Button backbtn;
    private TextView total;
    private Boolean checkReady = false;
    private TextView tvpaywave;
    private String formatDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_pay_wave);

        EditText Type, cartid, email, currency, QRMethod, settlement, phoneNo, Amount, username, password, poremid;

        TextView txt = findViewById(R.id.textView);
        loading = findViewById(R.id.progressBar);
        Type = findViewById(R.id.TxnType);
        cartid = findViewById(R.id.CartId);
        email = findViewById(R.id.Email);
        currency = findViewById(R.id.Currency);
        QRMethod = findViewById(R.id.QRMethod);
        settlement = findViewById(R.id.SettlementIndex);
        phoneNo = findViewById(R.id.Phone);
        Amount = findViewById(R.id.Amount);
        tvpaywave = findViewById(R.id.tvpaywave);

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCall();
                finish();
            }
        });
        usbService = UsbService.getInstance();

        Intent intent = getIntent();
        totalprice = intent.getStringExtra("totalprice");
        type = intent.getStringExtra("type");
        typechoose = intent.getStringExtra("typechoose");
        TextView scantext = findViewById(R.id.scantext);
        if (typechoose.equalsIgnoreCase("paywave")) {
            tvpaywave.setText("payWave Scan");
            ImageView iv_scan = findViewById(R.id.iv_scan);
            iv_scan.setBackground(getResources().getDrawable(R.drawable.creditc));
            scantext.setText("Hold your payment card with security chip on top and tap on the payment device below.");
            try {
                pDialogQr = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

                pDialogQr.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
                pDialogQr.setTitleText("SHOW YOUR PAYWAVE SCAN TO PROCEED.");
                pDialogQr.show();//showing the dialog
            } catch (Exception ex) {
            }
        } else {
            tvpaywave.setText("Qr scan");
            scantext.setText("Select your e-Wallet and scan the QR code display on the payment reader below.");
            try {
                pDialogQr = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

                pDialogQr.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                //  pDialog.setTitleText("SHOW YOUR " + paymentmethod.toUpperCase() + " QR TO PROCEED.");
                pDialogQr.setTitleText("Scan the QR code display on the payment device below.");
                pDialogQr.show();//showing the dialog
            } catch (Exception ex) {
            }
        }

        loading.setVisibility(View.VISIBLE);
        TextView total = findViewById(R.id.total);
        total.setText("TOTAL : RM " + String.format("%.2f", Double.valueOf(totalprice)));
//        Connect = findViewById(R.id.Connectbtn);
//        Connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
        usbService.findDevice(getApplicationContext(), new UsbConnection() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                                txt.setText(responseMessage.getAll());
                        readyCall();
                    }
                });
            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt.setText(responseMessage.getAll());
                        loading.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void isConnected(boolean isconnect) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isconnect) {
                            Toast.makeText(getApplicationContext(), "USB connection is dropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//            }
//        });


        submit = findViewById(R.id.Submitbtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Amount.getText().toString().isEmpty()) {
                    BigDecimal amount = new BigDecimal(Amount.getText().toString());
                    RequestMessage requestMessage = new RequestMessage(Type.getText().toString(), cartid.getText().toString(), email.getText().toString(), currency.getText().toString(), amount, QRMethod.getText().toString(), settlement.getText().toString(), phoneNo.getText().toString());
                    usbService.send(requestMessage, new SerialComCallback() {
                        @Override
                        public void onSuccess(ResponseMessage responseMessage) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setText(responseMessage.getAll());
                                    //A += responseMessage.getResponseText();
                                }
                            });
                        }

                        @Override
                        public void onFail(ResponseMessage responseMessage) {
                            runOnUiThread(new Runnable() {
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

        ready = findViewById(R.id.Readybtn);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amountInput = "1.00";
                BigDecimal amount = new BigDecimal(amountInput);
                RequestMessage requestMessage = new RequestMessage("00", "test1231221", "junesung@gkash.com", "MYR", amount, "12", "", "");
                usbService.send(requestMessage, new SerialComCallback() {
                    @Override
                    public void onSuccess(ResponseMessage responseMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                                //log += responseMessage.getResponseText();
                            }
                        });

                    }

                    @Override
                    public void onFail(ResponseMessage responseMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }
                });
            }
        });

        Cancel = findViewById(R.id.cancelbtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amountInput = "1.00";
                BigDecimal amount = new BigDecimal(amountInput);
                RequestMessage requestMessage = new RequestMessage("99", "test1231221", "junesung@gkash.com", "MYR", amount, "12", "", "");
                usbService.send(requestMessage, new SerialComCallback() {
                    @Override
                    public void onSuccess(ResponseMessage responseMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }

                    @Override
                    public void onFail(ResponseMessage responseMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txt.setText(responseMessage.getAll());
                            }
                        });
                    }
                });
            }


        });

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Auth = findViewById(R.id.Authbtn);
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

        poremid = findViewById(R.id.PORemID);
        txnDetail = findViewById(R.id.Txnbtn);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.INVISIBLE);
                        if (pDialogQr != null)
                            pDialogQr.dismissWithAnimation();
                    }
                });
            }
        });
    }

    private void SubmitCall() {
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
            RequestMessage requestMessage = new RequestMessage(txntype, "GK" + formatDate, "", "MYR", amount, type1, "02", "");
            usbService.send(requestMessage, new SerialComCallback() {
                @Override
                public void onSuccess(ResponseMessage responseMessage) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            txt.setText(responseMessage.getAll());
                            //A += responseMessage.getResponseText();
                            if (pDialogQr != null)
                                pDialogQr.dismissWithAnimation();
                            loading.setVisibility(View.INVISIBLE);
                            String result = responseMessage.getResponseText();
                            result = result.trim();
                            if (result.equalsIgnoreCase("SUCCESS")) {
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
                                Double point = 0.00;
                                int pid = 0, ustatus = 0;

//                                DispensePopUp dispensePopUp = new DispensePopUp();
//                                dispensePopUp.DispensePopUp(PayWaveSummaryActivity.this, paytype, false, "Paywave",
//                                        Double.valueOf(totalprice), "", point, 0, pid,
//                                        "", ustatus, "Success", "");
//                                if(typechoose.equalsIgnoreCase("paywave")){
//                                    finish();
//                                }

                                //                                Intent it = new Intent(PayWaveSummaryActivity.this, DispenseActivityNew.class);
//                                it.putExtra("mval", paytype);
//                                it.putExtra("totalamt", Double.valueOf(totalprice));
//                                it.putExtra("paytype", "Paywave");
//                                it.putExtra("isloggedin", false);
//                                it.putExtra("points", point);
//                                it.putExtra("userid", 0);
//                                it.putExtra("pid", pid);
//                                it.putExtra("exdate", "");
//                                it.putExtra("ustatus", ustatus);
//                                startActivity(it);
//                                finish();
                            } else {
                                pDialog = new SweetAlertDialog(PayWaveSummaryActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            txt.setText(responseMessage.getAll());
                            if (pDialogQr != null)
                                pDialogQr.dismissWithAnimation();
                            loading.setVisibility(View.INVISIBLE);
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
        BigDecimal amount = new BigDecimal(totalprice);
        RequestMessage requestMessage = new RequestMessage("99", formatDate, "", "MYR", amount, "12", "", "");
        usbService.send(requestMessage, new SerialComCallback() {
            @Override
            public void onSuccess(ResponseMessage responseMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt.setText(responseMessage.getAll());
                    }
                });
            }

            @Override
            public void onFail(ResponseMessage responseMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //txt.setText(responseMessage.getAll());
                    }
                });
            }
        });
    }
}
