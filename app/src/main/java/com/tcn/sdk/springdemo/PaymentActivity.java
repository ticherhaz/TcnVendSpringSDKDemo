package com.tcn.sdk.springdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.dataspassmodel;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PaymentActivity extends AppCompatActivity {

    CartDBHandler db;
    double totalcost = 0;
    double amountdedd = 0;
    //values to send
    double chargingprice = 0;
    String paymentmtd = "";
    Button ctnshop;
    TextView namet;
    int UserID = 0;
    int pid = 0;
    double Points = 0.00;
    String ExpireDate = "";
    int UserStatus = 0;
    String FirstName = "";
    String LastName = "";
    Boolean isloggedin = false;
    SweetAlertDialog sweetAlertDialog;
    Boolean isuserpaying = false;
    Boolean threadintrupt = false;
    Boolean oncreate = false;
    wait30 w30;
    double promoamt = 0;
    String promname = " ";
    boolean ispromo = false;
    private ImageButton back;
    private TextView amt;
    private List<CartListModel> cartListModels;

    void setuserdet() {

        try {
            String fname = getIntent().getStringExtra("fname");
            String lname = getIntent().getStringExtra("lname");
            String exdate = getIntent().getStringExtra("expdate");
            int userstatus = getIntent().getIntExtra("ustatus", 0);
            int uid = getIntent().getIntExtra("uid", 0);
            int pid = getIntent().getIntExtra("pid", 0);
            double points = getIntent().getDoubleExtra("points", 0.00);

            this.UserID = uid;
            this.UserStatus = userstatus;
            this.ExpireDate = exdate;
            this.pid = pid;
            this.Points = points;
            this.FirstName = fname;
            this.LastName = lname;

            namet.setVisibility(View.VISIBLE);
            namet.setText("Signed  in as: " + FirstName + " " + LastName);

        } catch (Exception ex) {
        }
    }

    void showsweetalerttimeout(final CountDownTimer[] ct) {
        sweetAlertDialog = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        back = findViewById(R.id.backbtn);
        amt = findViewById(R.id.total);
        ctnshop = findViewById(R.id.ctshoping);

        namet = findViewById(R.id.name);
        namet.setVisibility(View.INVISIBLE);

        isloggedin = getIntent().getBooleanExtra("login", false);
        if (isloggedin) {

            setuserdet();

        }

        w30 = new wait30();
        w30.start();
        oncreate = true;
        cartListModels = new ArrayList<CartListModel>();
        db = new CartDBHandler(this);
        cartListModels = db.getAllItems();

        for (CartListModel cn : cartListModels) {
            String log = cn.getItemprice();
            totalcost = totalcost + Double.parseDouble(log);

        }
        chargingprice = totalcost;
        amt.setText(chargingprice + "0");
        paymentload();

        ctnshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TypeProfuctActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    void paymentload() {


        ImageButton walletpaybost = findViewById(R.id.imageView12);
        ImageButton walletpaymq = findViewById(R.id.imageView11);
        ImageButton walletpaywc = findViewById(R.id.imageView14);
        ImageButton walletpaytng = findViewById(R.id.imageView13);
        ImageButton paywave1 = findViewById(R.id.imageView23);
        ImageButton paywave2 = findViewById(R.id.imageView15);
        ImageButton paywave3 = findViewById(R.id.imageView21);
        ImageButton promo = findViewById(R.id.imageView10);
        final ImageButton point = findViewById(R.id.imageView6);
        ImageButton cash = findViewById(R.id.imageView20);


        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isloggedin) {
                    if (Points >= totalcost) {
                        if (ispromo) {
                            SweetAlertDialog sw = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setContentText("You cannot use promo with this payment method")
                                    .setTitleText("Warning").setCancelButton(
                                            "Discard", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                }
                                            }
                                    )
                                    .setConfirmButton("Continue without promo", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                                            //To pass:
                                            promname = "";
                                            promoamt = 0;
                                            it.putExtra("mval", new dataspassmodel("Member Points", totalcost, promoamt, promname, R.drawable.mobileqr));
                                            it.putExtra("isloggedin", isloggedin);
                                            it.putExtra("points", Points);
                                            it.putExtra("userid", UserID);
                                            it.putExtra("pid", pid);
                                            it.putExtra("exdate", ExpireDate);
                                            it.putExtra("ustatus", UserStatus);
                                            sweetAlertDialog.dismissWithAnimation();
                                            startActivity(it);
                                        }
                                    });
                            sw.show();
                        } else {
                            SweetAlertDialog sw = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setContentText("Your " + chargingprice + " points will deducted from your account")
                                    .setTitleText("Confirmation")
                                    .setConfirmButton("Confirm", new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                                            //To pass:
                                            it.putExtra("mval", new dataspassmodel("Member Points", chargingprice, promoamt, promname, R.drawable.mobileqr));
                                            it.putExtra("isloggedin", isloggedin);
                                            it.putExtra("points", Points);
                                            it.putExtra("userid", UserID);
                                            it.putExtra("pid", pid);
                                            it.putExtra("exdate", ExpireDate);
                                            it.putExtra("ustatus", UserStatus);
                                            sweetAlertDialog.dismissWithAnimation();
                                            startActivity(it);
                                        }
                                    });
                            sw.show();
                        }
                    } else {

                        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                .setContentText("Your points are insufficient for this transaction, Use another payment option and earn more points")
                                .setTitleText("Insufficient Points")
                                .setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                }).show();

                    }


                } else {
                    new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setContentText("This feature is only available")
                            .setTitleText("Please login")
                            .setConfirmButton("Login/Register", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }).show();
                }
            }
        });


        final SweetAlertDialog[] takepromo = {new SweetAlertDialog(PaymentActivity.this,
                SweetAlertDialog.NORMAL_TYPE)};


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        lp.setMargins(80, 80, 80, 80);
        lp.gravity = Gravity.CENTER;

        final SweetAlertDialog donepromo = new SweetAlertDialog(PaymentActivity.this,
                SweetAlertDialog.SUCCESS_TYPE);

        final LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Create EditText
        final EditText editText = new EditText(this);
        editText.setHint("PROMO");

        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        editText.setPadding(20, 20, 20, 20);
        linearLayout.addView(editText);


        donepromo.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                donepromo.dismissWithAnimation();
                chargingprice = chargingprice - amountdedd;
                if (chargingprice < 0.00) chargingprice = 0.00;
                promoamt = amountdedd;
                // textView_amount.setText((chargingprice)+"");
            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takepromo[0] = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.NORMAL_TYPE);
                takepromo[0].setTitleText("Please Input Promo Code..");
                takepromo[0].setCustomView(linearLayout);
                takepromo[0].setConfirmButton("Apply", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        try {
                            promname = editText.getText().toString().toUpperCase();
                            if (promname.equals("PROMO50")) {
                                promname = editText.getText().toString().toUpperCase();
                                amountdedd = chargingprice / 2;
                                takepromo[0].dismissWithAnimation();
                                donepromo.setTitleText("Thank you for using promo " + amountdedd + " is deducted from your final price.");
                                donepromo.show();
                                ispromo = true;
                            } else {
                                takepromo[0].dismissWithAnimation();
                                final SweetAlertDialog terrakepromo = new SweetAlertDialog(
                                        PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                                terrakepromo.setTitle("Invalid Promo Code");
                                terrakepromo.show();
                            }
                        } catch (Exception ex) {
                            takepromo[0].dismissWithAnimation();
                            final SweetAlertDialog terrakepromo = new SweetAlertDialog(
                                    PaymentActivity.this, SweetAlertDialog.ERROR_TYPE);
                            terrakepromo.setTitle("Invalid Promo Code");
                            terrakepromo.show();
                        }
                    }
                });
                takepromo[0].show();
                editText.requestFocus();
            }
        });


        View.OnClickListener oc = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Paywave",chargingprice,promoamt,promname,R.drawable.paywave));
                it.putExtra("isloggedin",isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);*/

                final SweetAlertDialog sd = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Under Maintenance")
                        .setContentText("Please use Waller or Cash, Sorry for inconvenience");
                sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                sd.show();
            }
        };

        walletpaybost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Boost", chargingprice, promoamt, promname, R.drawable.boost));
                it.putExtra("isloggedin", isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Cash", chargingprice, promoamt, promname, R.drawable.cash));
                it.putExtra("isloggedin", isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);

            }
        });

        walletpaymq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Maybank", chargingprice, promoamt, promname, R.drawable.maybank));
                it.putExtra("isloggedin", isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);
            }
        });

        walletpaywc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Wechat", chargingprice, promoamt, promname, R.drawable.wechatpay));
                it.putExtra("isloggedin", isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);
            }
        });

        walletpaytng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(PaymentActivity.this, SummaryActivity.class);
                //To pass:
                it.putExtra("mval", new dataspassmodel("Touch'n go", chargingprice, promoamt, promname, R.drawable.touchngo));
                it.putExtra("isloggedin", isloggedin);
                it.putExtra("points", Points);
                it.putExtra("userid", UserID);
                it.putExtra("pid", pid);
                it.putExtra("exdate", ExpireDate);
                it.putExtra("ustatus", UserStatus);

                startActivity(it);
            }
        });


        paywave1.setOnClickListener(oc);
        paywave2.setOnClickListener(oc);
        paywave3.setOnClickListener(oc);
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
        threadintrupt = false;
        isuserpaying = false;
        if (!oncreate) {
            new wait30().start();
        } else {

            oncreate = false;

        }


    }

    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt) {

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final CountDownTimer[] ct = new CountDownTimer[1];
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
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            ct[0].cancel();
                                        }
                                    }
                                }

                                public void onFinish() {

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

//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        showsweetalerttimeout(ct);
//
//                    }
//                });

            }
        }
    }

}
