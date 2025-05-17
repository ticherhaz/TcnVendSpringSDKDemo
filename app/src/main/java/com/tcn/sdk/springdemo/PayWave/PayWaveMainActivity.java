package com.tcn.sdk.springdemo.PayWave;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class PayWaveMainActivity extends AppCompatActivity {

    Button submit, ready, Auth, Cancel, txnDetail, Connect;
    String AuthToken = "";
    String log = "";
    String A = "";
    private UsbService usbService;
    private TextView display;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pay_wave);

        EditText Type, cartid, email, currency, QRMethod, settlement, phoneNo, Amount, username, password, poremid;

        TextView txt = findViewById(R.id.textView);

        Type = findViewById(R.id.TxnType);
        cartid = findViewById(R.id.CartId);
        email = findViewById(R.id.Email);
        currency = findViewById(R.id.Currency);
        QRMethod = findViewById(R.id.QRMethod);
        settlement = findViewById(R.id.SettlementIndex);
        phoneNo = findViewById(R.id.Phone);
        Amount = findViewById(R.id.Amount);
        UsbService usbService = UsbService.getInstance();

        Connect = findViewById(R.id.Connectbtn);
        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usbService.findDevice(getApplicationContext(), new UsbConnection() {
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
            }
        });


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
}