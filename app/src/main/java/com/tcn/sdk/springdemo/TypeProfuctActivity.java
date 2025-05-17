package com.tcn.sdk.springdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softspace.ssthirdpartyintegrationtestapp.FassPayClass;
import com.softspace.ssthirdpartyintegrationtestapp.MyBroadcastListener;
import com.softspace.ssthirdpartyintegrationtestapp.MyPaymentReceiver;
import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.Duitnow.DuitnowClass;
import com.tcn.sdk.springdemo.Ipay.IpaySummaryPopUp;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.PointsModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.Model.TempTrans;
import com.tcn.sdk.springdemo.Model.UserDetails;
import com.tcn.sdk.springdemo.Model.UserName;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.Note.CashNoteActivity;
import com.tcn.sdk.springdemo.PayWave.PaywaveChoosePopUp;
import com.tcn.sdk.springdemo.PayWave.PaywaveSummaryPopUp;
import com.tcn.sdk.springdemo.Recycler.CartRecycler;
import com.tcn.sdk.springdemo.Recycler.ProductRecycler;
import com.tcn.sdk.springdemo.SarawakPay.SarawakPayPayment;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.Utilities.Tools;
import com.tcn.sdk.springdemo.Utilities.UserInteractionAwareCallback;
import com.tcn.sdk.springdemo.Utilities.Uti;
import com.tcn.sdk.springdemo.duitnowkotlin.DuitNowKotlin;
import com.tcn.sdk.springdemo.tcnSpring.MainActDispenseM4;

import net.ticherhaz.vending_duitnow.DuitNow;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TypeProfuctActivity extends AppCompatActivity implements View.OnClickListener, MyBroadcastListener {
    private final String TAG = "TypeProfuctActivity";
    private final String remarks = "N/A";
    private final String payid = "";
    private final Boolean valid = false;
    private final int qrid = 0;
    private final long delay = 1000;
    private final Handler handler = new Handler();
    private final boolean isloggedin = false;
    private final double promoamt = 0;
    private final double Points = 0.00;
    private final String promname = " ";
    private final String ExpireDate = "";
    private final int UserID = 0;
    private final int pid = 0;
    private final int UserStatus = 0;
    private final int REQUEST_CODE = 100;
    public boolean paymentInProgress = false;
    FassPayClass fassPayClass;
    String quantity;
    private Button pay, memberpay, cancel;
    private double totalcost = 0;
    private SweetAlertDialog pd, sweetAlertDialog, pDialog, pDialogPaywave;
    private TextView amount;
    private List<CartListModel> cartListModels;
    private List<ProductModel> productapiModelList;
    private List<CongifModel> congifModels;
    private RecyclerView recyclerView, recyclerViewcart;
    private ProductRecycler productRecycler;
    private CartRecycler cartRecycler;
    private PorductDBHandler porductDBHandler;
    private CartDBHandler db;
    private configdata dbconfig;
    private String fid;
    private String mid;
    private String productsids = "";
    private String barcodetext = "";
    private Boolean isuserpaying = false;
    private Boolean threadintrupt = false;
    private Boolean oncreate = false;
    private TextView datee, monthtxt;
    private CountDownTimer[] ct;
    private RequestQueue requestQueue;
    private PointsModel mypoints;
    private UserName myuser;
    private UserDetails mydetl;
    private long last_text_edit = 0;
    private EditText et;
    private final Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                try {
                    Log.d("test", "test-" + et.getText().toString());
                    String qridStr = et.getText().toString();//Integer.parseInt(et.getText().toString().replaceAll("[^a-zA-Z0-9\\.\\-]", "").replace("?", ""));
                    //new JsonTask2().execute("https://memberappapi.azurewebsites.net/Api/Login/"+qridStr);
                    RollingLogger.i(TAG, "member pay qr code - " + qridStr);
                    callGetMember("https://memberappapi.azurewebsites.net/Api/Login/" + qridStr);

                    System.out.println("the code written : " + et.getText());
                    pDialog.setTitle("Processing..");

                } catch (Exception ex) {
                    RollingLogger.i(TAG, "api call error - " + ex);


                }
            }
        }
    };
    private Button backw;
    private boolean isCountDownCont = false;
    private boolean enableaddproduct = true;
    private double chargingprice = 0;
    private double currenttotal = 0.0;
    private LinearLayout iwallet, gkash;
    private Dialog customDialog;
    private SarawakPayPayment sarawakPayClass;
    private Handler countDownHandler;
    private CountDownTimer cdt;
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        String jsonString = data.getStringExtra("obj");
                        Gson gson = new Gson();
                        UserObj userObj = gson.fromJson(jsonString, UserObj.class);

//                        TempTrans(1, userObj, "");

//                        DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//                        dispensePopUpM5.DispensePopUp(TypeProfuctActivity.this, userObj, "success", "", requestQueue);

                        if (cdt != null) {
                            cdt.cancel();
                        }
                        isCountDownCont = true;
                        countDownHandler.removeCallbacksAndMessages(null);
                        Intent intent = new Intent(TypeProfuctActivity.this, MainActDispenseM4.class);
                        intent.putExtra("userObjStr", jsonString);

                        startActivity(intent);

                    } else if (result.getResultCode() == -2) {
                        Intent data = result.getData();

                        String jsonString = data.getStringExtra("obj");
                        Gson gson = new Gson();
                        UserObj userObj = gson.fromJson(jsonString, UserObj.class);

                        TempTrans(0, userObj, "");

                        customDialog = getCustomDialogProduct();
                        if (!isFinishing())
                            customDialog.show();
                    } else {
                        customDialog = getCustomDialogProduct();
                        if (!isFinishing())
                            customDialog.show();
                    }
                }
            });
    private Dialog customDialogDispense, customDialogPaywaveChoose, customDialogDispenseFass;
    private SweetAlertDialog pDialogIpay, globalDialogIpay, pDialogThankYouDispense, pDialogSarawak, pDialogUnableAddProduct, pDialogF, pDialogError;
    private String paymentTypeLoad = "";
    private boolean checkCash = false;
    private boolean fasspayment = false;
    private MyPaymentReceiver myReceiver;
    private int countFasspay = 0;
    private TextView tvscantext;
    private DuitNow duitNow;
    private DuitnowClass duitnowClass;

    public static void copyObject(Object src, Object dest)
            throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException {
        for (Field field : src.getClass().getFields()) {
            dest.getClass().getField(field.getName()).set(dest, field.get(src));
        }
    }

    private void CountDownTimer() {

        countDownHandler = new Handler();
        countDownHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //testing 5 second first
                if (!isCountDownCont) {
                    showsweetalerttimeout();
                    cdt = new CountDownTimer(10000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            if (millisUntilFinished > 0) {
                                sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
                            } else {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                            }
                        }

                        public void onFinish() {
                            if (countDownHandler != null) {
                                countDownHandler.removeCallbacksAndMessages(null);
                            }
                            //cleartypeproduct
                            clearTypeProductDialog();

                            //ipay
                            if (paymentTypeLoad.equalsIgnoreCase("ipay")) {
                                clearIpayDialog();
                            } else if (paymentTypeLoad.equalsIgnoreCase("sarawak")) {
                                clearSarawakDialog();
                                if (sarawakPayClass != null) {
                                    sarawakPayClass.alertDialogWaiting();
                                }
                            } else if (paymentTypeLoad.equalsIgnoreCase("paywave")) {
                                clearPaywaveDialog();
                            }
                            //dispense
                            clearDispenseDialog();

                            //fasspay
                            if (fasspayment) {
                                clearFassPayDialog();
                            }
                            Uti.freeMemory();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }.start();
                }
                countDownHandler.removeCallbacksAndMessages(null);
                CountDownTimer();
            }
        }, 80000);
    }

    private void showsweetalerttimeout() {
        RollingLogger.i(TAG, "sweet alert time out show");
        sweetAlertDialog = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
        sweetAlertDialog.setContentText("This session will end in 10");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                cdt.cancel();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                RollingLogger.i(TAG, "sweet alert time out close");
                threadintrupt = true;
                cdt.cancel();
                sweetAlertDialog.dismissWithAnimation();
                Uti.freeMemory();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RollingLogger.i(TAG, "sweet alert time out dismiss");

                sweetAlertDialog.dismissWithAnimation();
            }
        });
        if (!isFinishing()) {
            if (sweetAlertDialog != null && !sweetAlertDialog.isShowing()) {
                sweetAlertDialog.show();
            }

        }
    }

    public List<CartListModel> getCartListModels() {
        return cartListModels;
    }

    public void showprice() {
        for (CartListModel cn : cartListModels) {

            String log = cn.getItemprice();
            totalcost = totalcost + Double.parseDouble(log);
            RollingLogger.i(TAG, "show price total cost -" + totalcost);
        }

        currenttotal = Double.parseDouble(String.format("%.2f", totalcost));
        amount.setText("Total : RM " + String.format("%.2f", totalcost));
        totalcost = 0;

        if (cartListModels.size() > 0) {

            cartRecycler = new CartRecycler(cartListModels, this, productRecycler, productapiModelList);
            recyclerViewcart.setLayoutManager(new GridLayoutManager(this, 4));
            recyclerViewcart.setAdapter(cartRecycler);
            recyclerViewcart.setHasFixedSize(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (porductDBHandler != null) {
            porductDBHandler.close();
        }
        if (cdt != null) {
            cdt.cancel();
        }
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages(null);
        }
        if (duitnowClass != null) {
            duitnowClass.cleanup();
        }

        if (duitNow != null) {
            duitNow.dismissDialogDuitNow();
        }

        RollingLogger.i(TAG, "ondestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_profuct);

        paymentInProgress = false;
        CountDownTimer();
        RollingLogger.i(TAG, "Start TypeProfuct Activity");
        pay = findViewById(R.id.button2);
        memberpay = findViewById(R.id.button3);
        cancel = findViewById(R.id.button);
        datee = findViewById(R.id.date);
        monthtxt = findViewById(R.id.month);
        amount = findViewById(R.id.textView36);
        recyclerView = findViewById(R.id.itemrecycler);
        recyclerViewcart = findViewById(R.id.cartrecylcer);

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        String monthname = (String) android.text.format.DateFormat.format("MMM", new Date());
        String dayLongName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int year = c.get(Calendar.YEAR);
        datee.setText("" + day);
        monthtxt.setText(monthname + " " + year + " " + dayLongName);

        ImageView imageView = findViewById(R.id.imageView);
        SharedPref.init(this);
        TextView textView21 = findViewById(R.id.textView21);
        String LcartEnable = SharedPref.read(SharedPref.LcartEnable, "true");
        if (LcartEnable.equalsIgnoreCase("true")) {
            textView21.setText("YOUR CART  - Up to 4 items per transaction.");
        } else {
            textView21.setText("YOUR CART  - Up to 1 items per transaction.");
        }
        String logopath = SharedPref.read(SharedPref.logopath, "");
        if (logopath.length() > 0) {
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(Uri.parse(logopath));
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RollingLogger.i(TAG, "on create cancel clicked");
                try {
                    for (int i = 0; i < cartListModels.size(); i++) {
                        cartListModels.remove(i);
                    }

                    cancel.setEnabled(false);
                    Uti.freeMemory();
                    Intent intent = new Intent(TypeProfuctActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    RollingLogger.i(TAG, "Cancel clicked - " + ex);
                }
            }
        });

        productapiModelList = new ArrayList<ProductModel>();
        porductDBHandler = new PorductDBHandler(this);

        congifModels = new ArrayList<CongifModel>();
        dbconfig = new configdata(this);
        congifModels = dbconfig.getAllItems();
        for (CongifModel cn : congifModels) {
            fid = cn.getFid();
            mid = cn.getMid();

        }

        cartListModels = new ArrayList<CartListModel>();
        oncreate = true;

        try {
            RollingLogger.i(TAG, "oncreate getproduct api call");
            getproducts();
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Get product - " + ex);
        }

        memberpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RollingLogger.i(TAG, "memberpay clicked");
                try {
                    if (!cartListModels.isEmpty() || valid) {
                        if (cartListModels.isEmpty()) {
                            if (Integer.parseInt(quantity) >= 1) {

                                if (!Uti.chkinternet(TypeProfuctActivity.this)) {
                                    new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("No internet")
                                            .setContentText("Please contact careline")
                                            .show();
                                } else {
                                    RollingLogger.i(TAG, "memberpay read qr");
                                    readqr();
                                    showylogindialog();
                                }
                            } else {

                                final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Unavailable")
                                        .setContentText("selected item is unavailable");
                                sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                                if (!isFinishing()) {
                                    sd.show();
                                }
                            }
                        } else {
                            if (!Uti.chkinternet(TypeProfuctActivity.this)) {
                                new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("No internet")
                                        .setContentText("Please contact careline")
                                        .show();
                            } else {
                                SharedPref.init(TypeProfuctActivity.this);
                                String dispensetest = SharedPref.read(SharedPref.dispensetest, "");
                                if (dispensetest.equalsIgnoreCase("")) {
                                    RollingLogger.i(TAG, "memberpay readqr 2");
                                    readqr();
                                    showylogindialog();
                                } else {
                                    String TestDispensePassword = SharedPref.read(SharedPref.TestDispensePassword, "");
                                    String dispensetest1 = SharedPref.read(SharedPref.dispensetest, "");
//                                    hasper temporary disable to do testing
                                    if (!TestDispensePassword.equalsIgnoreCase("") && !dispensetest1.equalsIgnoreCase("")) {
                                        Intent intent = new Intent(TypeProfuctActivity.this, PasswordUnlock.class);
                                        intent.putExtra("type", "2");
                                        startActivityForResult(intent, REQUEST_CODE);
                                    }
//                                    double priceDb = 0;
//                                    for (int i = 0; i < cartListModels.size(); i++) {
//                                        priceDb = priceDb + Double.valueOf(cartListModels.get(i).getItemprice());
//                                    }
//                                    for (CartListModel cn : cartListModels) {
//                                        String log = cn.getItemprice();
//                                        totalcost = totalcost + Double.parseDouble(log);
//                                        int qty;
//                                        qty = Integer.parseInt(cn.getItemqty());
//                                        for (int x = 0; x < qty; x++) {
//
//                                            productsids += cn.getProdid() + ",";
//                                        }
//
//                                    }
//                                    chargingprice = totalcost;
//
//                                    cartListModels = cartProQuantityMinus();
//                                    UserObj userObj = new UserObj();
//                                    userObj.setMtd("Machine Test");
//                                    userObj.setIsloggedin(isloggedin);
//                                    userObj.setIpaytype("paywave");
//                                    userObj.setChargingprice(chargingprice);
//                                    userObj.setPoints(0);
//                                    userObj.setUserid(0);
//                                    userObj.setPid(pid);
//                                    userObj.setExpiredate(ExpireDate);
//                                    userObj.setUserstatus(0);
//                                    userObj.setCartModel(cartListModels);
//                                    userObj.setConfigModel(congifModels);
//
//                                    Gson gson = new Gson();
//                                    String jsonString = gson.toJson(userObj);
//                                    if (cdt != null) {
//                                        cdt.cancel();
//                                    }
//                                    isCountDownCont = true;
//                                    countDownHandler.removeCallbacksAndMessages(null);
//                                    Intent intent = new Intent(TypeProfuctActivity.this, MainActDispenseM4.class);
//                                    intent.putExtra("userObjStr", jsonString);
//
//                                    startActivity(intent);
                                }
                            }
                        }
                    } else {
                        final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Empty Cart")
                                .setContentText("Kindly choose your product");
                        sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                        if (!isFinishing()) {
                            sd.show();
                        }
                    }
                } catch (Exception ex) {
                    RollingLogger.i(TAG, "Member Pay clicked - " + ex);
                }
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    RollingLogger.i(TAG, "pay button clicked");
                    if (!cartListModels.isEmpty() || valid) {
                        if (cartListModels.isEmpty()) {
                            if (Integer.parseInt(quantity) >= 1) {
                                pay.setEnabled(false);
                                RollingLogger.i(TAG, "Select Payment Activity New Class called");
                                Intent pay = new Intent(TypeProfuctActivity.this, SelectPaymentActivityNew.class);
                                pay.putExtra("login", false);
                                startActivity(pay);
                            } else {

                                final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Unavailable")
                                        .setContentText("selected item is unavailable");
                                sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                                if (!isFinishing()) {
                                    sd.show();
                                }


                            }
                        } else {
                            if (countDownHandler != null) {
                                countDownHandler.removeCallbacksAndMessages(null);
                            }
                            isPaymentinProgress(true);
                            pay.setEnabled(false);
                            String duitnowonlynewStr = SharedPref.read(SharedPref.DuitNowOnlyNew, "");
                            if (duitnowonlynewStr.equalsIgnoreCase("true")) {
                                productsids = "";
                                totalcost = 0;
                                for (CartListModel cn : cartListModels) {
                                    String log = cn.getItemprice();
                                    totalcost = totalcost + Double.parseDouble(log);
                                    int qty;
                                    qty = Integer.parseInt(cn.getItemqty());
                                    for (int x = 0; x < qty; x++) {

                                        productsids += cn.getProdid() + ",";
                                    }

                                }
                                chargingprice = totalcost;

                                cartListModels = cartProQuantityMinus();
                                final UserObj obj = new UserObj();
                                obj.setConfigModel(congifModels);
                                obj.setCartModel(cartListModels);
                                obj.setIpaytype("duitnow");
                                obj.setIsloggedin(isloggedin);
                                obj.setUserid(UserID);
                                obj.setPoints(Points);
                                obj.setPid(pid);
                                obj.setExpiredate(ExpireDate);
                                obj.setUserstatus(UserStatus);
                                obj.setImage(0);
                                obj.setMtd("Duitnow payment");
                                obj.setChargingprice(chargingprice);

                                if (customDialog != null) {
                                    if (customDialog.isShowing()) {
                                        customDialog.dismiss();
                                    }
                                }

                                if (cdt != null) {
                                    cdt.cancel();
                                    cdt.cancel();
                                }
                                isCountDownCont = true;

                                initDuitNow(obj);
                            } else {
                                popoutdialog();
                            }

                        }
                    } else {
                        final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Empty Cart")
                                .setContentText("Kindly choose your product");
                        sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                        if (!isFinishing()) {
                            sd.show();
                        }
                    }
                } catch (Exception ex) {
                    RollingLogger.i(TAG, "Pay clicked - " + ex);
                }
            }
        });
    }

    private void initDuitNow(final UserObj obj) {


        /*new DuitnowClass(
                TypeProfuctActivity.this,
                requestQueue,
                chargingprice,
                obj,
                customDialogDispense,
                cartListModels);*/


        try {
            // Remove the countdown once the DuitNow dialog shows.
            countDownHandler.removeCallbacksAndMessages(null);

            // Get userObjClone
            net.ticherhaz.vending_duitnow.model.UserObj userObjClone = new net.ticherhaz.vending_duitnow.model.UserObj();
            copyObject(obj, userObjClone);

            // Get productIds
            final String productIds = CartListModel.getProductIds(cartListModels);

            // Get configModelClone
            net.ticherhaz.vending_duitnow.model.CongifModel congifModelClone = new
                    net.ticherhaz.vending_duitnow.model.CongifModel();
            copyObject(dbconfig.getAllItems().get(0), congifModelClone);

            final String title = getString(R.string.proceed_with_duitnow_pay);
            final String description = getString(R.string.duitnow_pay_description);
            final String totalMessage = getString(R.string.total_uppercase);

            final String titleTransactionCancel = getString(R.string.cancel_transaction);
            final String descriptionTransactionCancel = getString(R.string.cancel_transaction_description);

            final String titleTransactionFailed = getString(R.string.transaction_failed);
            final String descriptionTransactionFailed = getString(R.string.transaction_failed_description);


            duitNow = new DuitNow(
                    TypeProfuctActivity.this,
                    chargingprice,
                    userObjClone,
                    productIds,
                    congifModelClone,
                    title,
                    description,
                    totalMessage,
                    titleTransactionCancel,
                    descriptionTransactionCancel,
                    titleTransactionFailed,
                    descriptionTransactionFailed,
                    new DuitNow.DuitNowCallback() {
                        @Override
                        public void onLoggingEverything(@NonNull String s) {
                            RollingLogger.d("DUITNOW: " + s);
                            Tools.INSTANCE.logSimple("DUITNOW: " + s);
                        }

                        @Override
                        public void enableAllUiAtTypeProductActivity() {

                            paymentInProgress = false;
                            getpaybuttonenable().setEnabled(true);
                            clearCustomDialogDispense();
                            setEnableaddproduct(true);

                            Uti.freeMemory();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onPrepareStartDispensePopup(@NonNull String transactionNo) {
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(obj);
                            Intent intent = new Intent(TypeProfuctActivity.this, MainActDispenseM4.class);
                            intent.putExtra("userObjStr", jsonString);
                            intent.putExtra("transactionNo", transactionNo);
                            startActivity(intent);
                        }
                    });

        } catch (Exception e) {
            runOnUiThread(() -> {
                final SweetAlertDialog sd = new SweetAlertDialog(
                        TypeProfuctActivity.this,
                        SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DuitNow Error")
                        .setContentText(e.getLocalizedMessage());
                sd.setCancelButton(getString(R.string.txt_cancel), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        CountDownTimer();
                    }
                });
                sd.show();
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String resultValue = data.getStringExtra("keyName");

                if (resultValue.equalsIgnoreCase("done")) {
                    RollingLogger.i(TAG, "member pay testing run");
                    //for testing
                    double test = 0.0;
                    for (CartListModel cn : cartListModels) {
                        String log = cn.getItemprice();
                        test = test + Double.parseDouble(log);
                    }
                    cartListModels = cartProQuantityMinus();
                    UserObj userObj = new UserObj();
                    userObj.setMtd("Machine Test");
                    userObj.setIsloggedin(isloggedin);
                    userObj.setIpaytype("paywave");
                    userObj.setChargingprice(test);
                    userObj.setPoints(0);
                    userObj.setUserid(0);
                    userObj.setPid(pid);
                    userObj.setExpiredate(ExpireDate);
                    userObj.setUserstatus(0);
                    userObj.setCartModel(cartListModels);
                    userObj.setConfigModel(congifModels);
//                    DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//                    dispensePopUpM5.DispensePopUp(TypeProfuctActivity.this, userObj, "Success", payid, requestQueue);

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(userObj);
                    if (cdt != null) {
                        cdt.cancel();
                    }
                    isCountDownCont = true;
                    countDownHandler.removeCallbacksAndMessages(null);
                    Intent intent = new Intent(TypeProfuctActivity.this, MainActDispenseM4.class);
                    intent.putExtra("userObjStr", jsonString);

                    startActivity(intent);
                }
            }
        }
    }

    private void getproducts() {
        if (pd == null) {
            pd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pd.setCanceledOnTouchOutside(false);
            pd.setContentText("Processing");
            pd.show();
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        String uri;
        uri = "https://vendingappapi.azurewebsites.net/api/Product?franID=" + fid + "&mid=" + mid;

        JsonArrayRequest myReq = new JsonArrayRequest(Request.Method.GET,
                uri, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (pd != null) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
                Log.i("VOLLEY", response.toString());
                AsyncTask.execute(() -> {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrayList<ProductModel>>() {
                        }.getType();
                        productapiModelList = gson.fromJson(String.valueOf(response), type);

                        //porductDBHandler.deleteallitems();
                    } catch (Exception ex) {
                        RollingLogger.i(TAG, "Get Product response - " + ex);
                    }
                    RollingLogger.i(TAG, "get product api success");
                    //porductDBHandler.addItemNew(productapiModelList);

                    try {

                        if (productapiModelList.size() > 0) {

                            Collections.sort(productapiModelList, new Comparator<ProductModel>() {
                                public int compare(ProductModel o1, ProductModel o2) {
                                    if (o1.Item_Number == o2.Item_Number)
                                        return 0;
                                    return o1.Item_Number < o2.Item_Number ? -1 : 1;
                                }
                            });


                            TypeProfuctActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    for (int i = 0; i < productapiModelList.size(); i++) {
                                        productapiModelList.get(i).setPosition(0);
                                    }
                                    TypeProfuctActivity.this.productRecycler = new ProductRecycler(productapiModelList, TypeProfuctActivity.this);
                                    TypeProfuctActivity.this.recyclerView.setLayoutManager(new GridLayoutManager(TypeProfuctActivity.this, 4));
                                    TypeProfuctActivity.this.recyclerView.setAdapter(productRecycler);
                                    TypeProfuctActivity.this.recyclerView.setHasFixedSize(true);
                                    TypeProfuctActivity.this.pd.dismissWithAnimation();
                                }
                            });


                        } else {
                            TypeProfuctActivity.this.runOnUiThread(new Runnable() {
                                public void run() {

                                    TypeProfuctActivity.this.pd.dismissWithAnimation();
                                    if (!isFinishing()) {
                                        new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.ERROR_TYPE).show();
                                    }
                                    finish();
                                }
                            });

                        }
                    } catch (Exception ex) {
                        RollingLogger.i(TAG, "Get product response filter - " + ex);
                    }
                });

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                if (pd.isShowing()) {
                    pd.dismiss();
                }

                SweetAlertDialog pdError = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.ERROR_TYPE);
                pdError.setCanceledOnTouchOutside(false);
                pdError.setContentText("Connection To Server Error. Please Contact Careline.");
                if (!isFinishing()) {
                    pdError.show();
                }

                RollingLogger.i(TAG, "Get product error - " + error);
            }
        });


        requestQueue.add(myReq);
    }

    private void readqr() {

        try {
            et = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(80, 80, 80, 80);
            lp.gravity = Gravity.CENTER;
            //et.setVisibility(View.INVISIBLE);
            et.setTextColor(getResources().getColor(android.R.color.transparent));

            et.setInputType(InputType.TYPE_NULL);
            et.setFocusable(false);
            et.setFocusableInTouchMode(false);
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
                        handler.postDelayed(input_finish_checker, delay);

                    } else {

                    }

                }
            });

        } catch (Exception ex) {
            RollingLogger.i(TAG, "Read Qr error - " + ex);
        }

    }

    void showylogindialog() {

        try {
            final LinearLayout linearLayout = new LinearLayout(this);

            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // Create EditText
            final ImageView editText = new ImageView(this);
            editText.setImageDrawable(getDrawable(R.drawable.mobileqr));
            editText.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
            editText.setPadding(20, 20, 20, 20);
            linearLayout.addView(et);

            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//            pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//
//                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                        barcodetext += (char) event.getUnicodeChar();
//                        System.out.println("kesa  :" + barcodetext);
//                        et.setText(barcodetext);
//                    }
//
//                    return false;
//                }
//            });
            pDialog.setConfirmButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            });
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
            pDialog.setTitleText("SHOW YOUR LOGIN QR TO PROCEED.");
            barcodetext = "";
            pDialog.setCustomView(linearLayout);
            if (!isFinishing()) {
                pDialog.show();
            }
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Show login dialog error - " + ex);
        }

    }

    private void callGetMember(String url) {
        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            JSONObject jsonParam = null;
            RollingLogger.i(TAG, "call get member api");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responseStr) {
                            try {
                                RollingLogger.i(TAG, "call get member api success");
                                JSONObject response = new JSONObject(responseStr);
                                String userIdStr = "https://memberappapi.azurewebsites.net/Api/Points?userID=" + response.getString("UserID");
                                String FirstName = response.getString("FirstName");
                                String LastName = response.getString("LastName");
                                callGetUserId(userIdStr, FirstName, LastName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    RollingLogger.i(TAG, "call get member api error");
                    final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText("Invalid QR Code.");
                    sd.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    sd.show();
                }
            });
            requestQueue.add(stringRequest);

        } catch (Exception ex) {

        }
    }

    private void callGetUserId(String url, String FirstName, String LastName) {
        try {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            JSONObject jsonParam = null;
            RollingLogger.i(TAG, "call get user id api");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String responseStr) {
                            RollingLogger.i(TAG, "call get user id api success");
                            System.out.println(responseStr);
                            Intent it = new Intent(TypeProfuctActivity.this, SelectPaymentActivity.class);

                            String Points = "", UserID = "", ID = "", UserStatus = "", ExpireDate = "";
                            try {
                                JSONObject response = new JSONObject(responseStr);
                                Points = response.getString("Points");
                                UserID = response.getString("UserID");
                                ID = response.getString("ID");
                                UserStatus = response.getString("UserStatus");
                                ExpireDate = response.getString("ExpireDate");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            cartListModels = cartProQuantityMinus();

                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(cartListModels);
                            String jsonStrConfig = gson.toJson(congifModels);

                            //To pass:
                            it.putExtra("fname", FirstName);
                            it.putExtra("lname", LastName);
                            it.putExtra("points", Points);
                            it.putExtra("uid", UserID);
                            it.putExtra("pid", ID);
                            it.putExtra("ustatus", UserStatus);
                            it.putExtra("expdate", ExpireDate);
                            it.putExtra("login", true);
                            it.putExtra("cartListModels", jsonStr);
                            it.putExtra("configModels", jsonStrConfig);

                            pDialog.dismissWithAnimation();
                            pd.dismissWithAnimation();
                            startActivity(it);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    RollingLogger.i(TAG, "call get user id api error");
                    final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error!")
                            .setContentText("");
                    sd.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    sd.show();
                }
            });
            requestQueue.add(stringRequest);

        } catch (Exception ex) {

        }
    }

    public void showerrdialog() {

        pDialog.dismissWithAnimation();
        pd.dismissWithAnimation();
        final SweetAlertDialog sd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No Record Found!")
                .setContentText("Invalid QR Code.");
        sd.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sd.show();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
//        if(fasspayment){
//            fassPayClass.onPause();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pay.setEnabled(true);
        totalcost = 0;
        threadintrupt = false;
        isuserpaying = false;
        if (!oncreate) {
//            new wait30().start();
        } else {
            oncreate = false;
        }
//        if(fasspayment){
//            fassPayClass.onResumeFilterUsb();
//            fassPayClass.onResumeStartUsbService();
//        }
    }

    private void popoutdialog() {
        try {
            customDialog = getCustomDialogProduct();
            if (!isFinishing())
                customDialog.show();

            productsids = "";
            totalcost = 0;
            for (CartListModel cn : cartListModels) {
                String log = cn.getItemprice();
                totalcost = totalcost + Double.parseDouble(log);
                int qty;
                qty = Integer.parseInt(cn.getItemqty());
                for (int x = 0; x < qty; x++) {

                    productsids += cn.getProdid() + ",";
                }

            }
            chargingprice = totalcost;

            backw = customDialog.findViewById(R.id.backbtn3);
            backw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPaymentinProgress(false);
                    pay.setEnabled(true);
                    totalcost = 0;
                    customDialog.dismiss();
                    enableaddproduct = true;
                }
            });
            dialog_wallet(customDialog);
        } catch (Exception ex) {
            RollingLogger.i(TAG, "pop out dialog error - " + ex);
        }
    }

    public void enableSelectionProduct() {
        isPaymentinProgress(false);
        pay.setEnabled(true);
        totalcost = 0;
        enableaddproduct = true;
    }

    private void dialog_wallet(Dialog dialog) {
        try {
            LinearLayout ll_paywave = dialog.findViewById(R.id.ll_paywave);
            LinearLayout ll_ewallet = dialog.findViewById(R.id.ll_ewallet);
            ImageButton walletpaybost = dialog.findViewById(R.id.boost);
            ImageButton walletpaymq = dialog.findViewById(R.id.maybank);
            ImageButton walletpaywc = dialog.findViewById(R.id.wechatpay);
            ImageButton walletpaytng = dialog.findViewById(R.id.touchngopay);
            ImageButton walletpaygrab = dialog.findViewById(R.id.grabpay);
            ImageButton walletpayali = dialog.findViewById(R.id.alipay);
            ImageButton walletpayshopee = dialog.findViewById(R.id.shopee);
            ImageButton paywave = dialog.findViewById(R.id.paywave);
            ImageButton wallet = dialog.findViewById(R.id.wallet);
            ImageButton sarawakpay = dialog.findViewById(R.id.sarawakpay);
            iwallet = dialog.findViewById(R.id.iwallet);
            gkash = dialog.findViewById(R.id.gkash);

            LinearLayout ll_cash = dialog.findViewById(R.id.ll_cash);

            String tokenStr = SharedPref.read(SharedPref.Token, "");
            LinearLayout ll_token = dialog.findViewById(R.id.ll_token);
            if (tokenStr.equalsIgnoreCase("true")) {
                ll_token.setVisibility(View.VISIBLE);
            }
            ll_token.setOnClickListener(view -> {
                cartListModels = cartProQuantityMinus();
                UserObj obj = new UserObj();
                obj.setConfigModel(congifModels);
                obj.setCartModel(cartListModels);
                obj.setIpaytype("cash");
                obj.setIsloggedin(isloggedin);
                obj.setUserid(UserID);
                obj.setPoints(Points);
                obj.setPid(pid);
                obj.setExpiredate(ExpireDate);
                obj.setUserstatus(UserStatus);
                obj.setImage(0);
                obj.setMtd("Cash");
                obj.setChargingprice(chargingprice);
                paymentTypeLoad = "cash";
                Dialog dialog1 = getCustomDialogProduct();
                if (dialog1 != null) {
                    if (dialog1.isShowing()) {
                        dialog1.dismiss();
                    }
                }
                if (cdt != null) {
                    cdt.cancel();
                }
                isCountDownCont = true;
                checkCash = true;
                countDownHandler.removeCallbacksAndMessages(null);
                String formattedString = String.format("%.2f", chargingprice);

//                Intent intent = new Intent(TypeProfuctActivity.this, TokenActivity.class);
//                intent.putExtra("price", formattedString);
//
//                Gson gson = new Gson();
//                String json = gson.toJson(obj);
//                intent.putExtra("obj", json);
//                startActivity(intent);
            });

            String cashCoinOnly = SharedPref.read(SharedPref.CashCoinPay, "");
            LinearLayout ll_cash_new = dialog.findViewById(R.id.ll_cash_new);
            if (cashCoinOnly.equalsIgnoreCase("true")) {
                ll_cash_new.setVisibility(View.VISIBLE);
            }
            ll_cash_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartListModels = cartProQuantityMinus();
                    UserObj obj = new UserObj();
                    obj.setConfigModel(congifModels);
                    obj.setCartModel(cartListModels);
                    obj.setIpaytype("cash");
                    obj.setIsloggedin(isloggedin);
                    obj.setUserid(UserID);
                    obj.setPoints(Points);
                    obj.setPid(pid);
                    obj.setExpiredate(ExpireDate);
                    obj.setUserstatus(UserStatus);
                    obj.setImage(0);
                    obj.setMtd("Cash");
                    obj.setChargingprice(chargingprice);
                    paymentTypeLoad = "cash";
                    Dialog dialog = getCustomDialogProduct();
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    if (cdt != null) {
                        cdt.cancel();
                    }
                    isCountDownCont = true;
                    checkCash = true;
                    countDownHandler.removeCallbacksAndMessages(null);
                    String formattedString = String.format("%.2f", chargingprice);

//                    Intent intent = new Intent(TypeProfuctActivity.this, CashNoteNewActivity.class);
//                    intent.putExtra("price", formattedString);

                    Gson gson = new Gson();
                    String json = gson.toJson(obj);
//                    intent.putExtra("obj", json);
//                    startForResult.launch(intent);
                }
            });

            String fasspayonly = SharedPref.read(SharedPref.FasspayOnly, "");
            if (fasspayonly.equalsIgnoreCase("true")) {
                LinearLayout ll_fasspay = dialog.findViewById(R.id.ll_fasspay);
                ll_fasspay.setVisibility(View.VISIBLE);
                ll_fasspay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isCountDownCont = true;
                        countFasspay = 0;
                        fasspayment = true;
                        if (customDialog != null) {
                            if (customDialog.isShowing()) {
                                customDialog.dismiss();
                            }
                        }
                        fassPayClass = new FassPayClass();

                        myReceiver = new MyPaymentReceiver(TypeProfuctActivity.this);
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("com.ratnorsoft.myapp.ACTION_MY_BROADCAST");
                        registerReceiver(myReceiver, intentFilter);

                        fassPayClass.onCreate(TypeProfuctActivity.this);
                        fassPayClass.onCreateGetMsgSerialPort();

                        customDialogDispenseFass = getCustomDialogDispense("fasspay");
                        TextView tvmtd = customDialogDispenseFass.findViewById(R.id.mtd);
                        tvmtd.setText("Card Payment");
                        tvscantext = customDialogDispenseFass.findViewById(R.id.scantext);
                        tvscantext.setText("Place your card near the machine's \ncontactless payment area to proceed.");

                        double price = 0.0;
                        for (CartListModel cn : cartListModels) {
                            String log = cn.getItemprice();
                            price = price + Double.parseDouble(log);
                        }
                        TextView pricetext = customDialogDispenseFass.findViewById(R.id.pricetext);
                        pricetext.setText("TOTAL : RM " + String.format("%.2f", price));

                        fassPayClass.onResumeFilterUsb();
                        fassPayClass.onResumeStartUsbService();

                        String pricecStr = String.format("%.2f", price);
                        pricecStr = pricecStr.replace(".", "");
                        int priceInt = Integer.parseInt(pricecStr);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.removeCallbacksAndMessages(null);
                                fassPayClass.doSalesWithAmount(String.valueOf(priceInt));
//                                tvscantext.setText("Place your card near the machine's contactless payment area to proceed.");
                            }
                        }, 1500);

                        pDialogF = getDialogIpay();
                        pDialogF.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                        pDialogF.setTitleText(" ");
                        pDialogF.setCanceledOnTouchOutside(false);
                        pDialogF.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                clearFassPayDialog();
                                isPaymentinProgress(false);
                                pay.setEnabled(true);
                                totalcost = 0;
                                enableaddproduct = true;
                                fassPayClass.doCancel();
                                unregisterReceiver(myReceiver);
                                isCountDownCont = false;
                            }
                        });
                        if (!isFinishing()) {
                            customDialogDispenseFass.show();
                            pDialogF.show();
                        }

                        Button backbtn = customDialogDispenseFass.findViewById(R.id.backbtn);
                        backbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clearFassPayDialog();
                                fassPayClass.doCancel();
                                unregisterReceiver(myReceiver);
                                isPaymentinProgress(false);
                                pay.setEnabled(true);
                                totalcost = 0;
                                enableaddproduct = true;
                                isCountDownCont = false;
                            }
                        });

                    }
                });
            }
            String cashonly = SharedPref.read(SharedPref.CashOnly, "");
            ImageView cash = dialog.findViewById(R.id.iv_cash);
            if (cashonly.equalsIgnoreCase("true")) {
                ll_cash.setVisibility(View.VISIBLE);
            }
            cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    CashNotePopUp cashNotePopUp = new CashNotePopUp();
                    cartListModels = cartProQuantityMinus();
                    UserObj obj = new UserObj();
                    obj.setConfigModel(congifModels);
                    obj.setCartModel(cartListModels);
                    obj.setIpaytype("cash");
                    obj.setIsloggedin(isloggedin);
                    obj.setUserid(UserID);
                    obj.setPoints(Points);
                    obj.setPid(pid);
                    obj.setExpiredate(ExpireDate);
                    obj.setUserstatus(UserStatus);
                    obj.setImage(0);
                    obj.setMtd("Cash payment");
                    obj.setChargingprice(chargingprice);
                    paymentTypeLoad = "cash";
                    Dialog dialog = getCustomDialogProduct();
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    if (cdt != null) {
                        cdt.cancel();
                    }
                    isCountDownCont = true;
                    checkCash = true;
                    countDownHandler.removeCallbacksAndMessages(null);
                    String formattedString = String.format("%.2f", chargingprice);

                    Intent intent = new Intent(TypeProfuctActivity.this, CashNoteActivity.class);
                    intent.putExtra("price", formattedString);

                    Gson gson = new Gson();
                    String json = gson.toJson(obj);
                    intent.putExtra("obj", json);
                    startActivity(intent);
//                    cashNotePopUp.cashPopUp(obj, TypeProfuctActivity.this, customDialogDispense, requestQueue);
                }
            });

            SharedPref.init(this);
            String type = SharedPref.read(SharedPref.type, "");
            String gtypepaywave = SharedPref.read(SharedPref.gtypepaywave, "");
            String gtypewallet = SharedPref.read(SharedPref.gtypewallet, "");
            String sarawakPayOnly = SharedPref.read(SharedPref.SarawakPayOnly, "");
            String sarawakStr = SharedPref.read(SharedPref.SarawakPay, "");
            String DuitNowOnlyStr = SharedPref.read(SharedPref.DuitNowOnly, "");
            LinearLayout ll_sarawakpay = dialog.findViewById(R.id.ll_sarawakpay);

            if (DuitNowOnlyStr.equalsIgnoreCase("true")) {

                final ImageButton llduitnow = dialog.findViewById(R.id.duitnow);
                llduitnow.setVisibility(View.VISIBLE);
                llduitnow.setOnClickListener(view -> {
                    cartListModels = cartProQuantityMinus();
                    final UserObj obj = new UserObj();
                    obj.setConfigModel(congifModels);
                    obj.setCartModel(cartListModels);
                    obj.setIpaytype("duitnow");
                    obj.setIsloggedin(isloggedin);
                    obj.setUserid(UserID);
                    obj.setPoints(Points);
                    obj.setPid(pid);
                    obj.setExpiredate(ExpireDate);
                    obj.setUserstatus(UserStatus);
                    obj.setImage(0);
                    obj.setMtd("Duitnow payment");
                    obj.setChargingprice(chargingprice);

                    if (customDialog != null) {
                        if (customDialog.isShowing()) {
                            customDialog.dismiss();
                        }
                    }

                    initDuitNow(obj);

                    /*new DuitnowClass(
                            TypeProfuctActivity.this,
                            requestQueue,
                            chargingprice,
                            obj,
                            customDialogDispense,
                            cartListModels);*/

//                    new DuitNowKotlin(
//                            TypeProfuctActivity.this,
//                            chargingprice,
//                            obj,
//                            cartListModels);

                });
//                paymentipay ipay88ewallet = new paymentipay();
//                JSONObject result = ipay88ewallet.Merchant_Scan_Duitnow(chargingprice, "", 0,
//                        TypeProfuctActivity.this, "M41288", "m4d6TpUkUp", "49");
//                JSONObject jsonObject = result;
//                try {
//                    String QRCode = jsonObject.getString("QRCode");
//                    String QRValue = jsonObject.getString("QRValue");
//                    ImageView ivQrcode = customDialog.findViewById(R.id.ivQrcode);
//                    Picasso.get().load(QRCode).resize(500,500).into(ivQrcode);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
            if (sarawakPayOnly.equalsIgnoreCase("true")) {
                ll_sarawakpay.setVisibility(View.VISIBLE);
                iwallet.setVisibility(View.GONE);
                gkash.setVisibility(View.GONE);
                ll_paywave.setVisibility(View.GONE);
                ll_ewallet.setVisibility(View.GONE);
            } else {
                if (type.equalsIgnoreCase("") && gtypepaywave.equalsIgnoreCase("")
                        && gtypewallet.equalsIgnoreCase("")) {
                    iwallet.setVisibility(View.VISIBLE);
                    gkash.setVisibility(View.GONE);
                } else if (type.equalsIgnoreCase("iwallet") && gtypepaywave.equalsIgnoreCase("")
                        && gtypewallet.equalsIgnoreCase("")) {
                    iwallet.setVisibility(View.VISIBLE);
                    gkash.setVisibility(View.GONE);
                } else if (type.equalsIgnoreCase("") && gtypepaywave.equalsIgnoreCase("gpaywave")
                        && gtypewallet.equalsIgnoreCase("")) {
                    iwallet.setVisibility(View.GONE);
                    gkash.setVisibility(View.VISIBLE);
                    ll_paywave.setVisibility(View.VISIBLE);
                    ll_ewallet.setVisibility(View.VISIBLE);
                } else if (type.equalsIgnoreCase("") && gtypewallet.equalsIgnoreCase("gwallet")
                        && gtypepaywave.equalsIgnoreCase("")) {
                    iwallet.setVisibility(View.VISIBLE);
                    gkash.setVisibility(View.VISIBLE);
                    ll_paywave.setVisibility(View.VISIBLE);
                    ll_ewallet.setVisibility(View.GONE);
                } else {
                    if (type.equalsIgnoreCase("")) {
                        iwallet.setVisibility(View.GONE);
                    } else {
                        iwallet.setVisibility(View.VISIBLE);
                    }
                    gkash.setVisibility(View.GONE);
                }
            }

            if (sarawakStr.equalsIgnoreCase("true")) {
                ll_sarawakpay.setVisibility(View.VISIBLE);
            }
            sarawakpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RollingLogger.i(TAG, "sarawak pay clicked");
                    paymentTypeLoad = "sarawak";
                    cartListModels = cartProQuantityMinus();
                    sarawakPayClass = new SarawakPayPayment();
                    sarawakPayClass.SarawakPayPayment(TypeProfuctActivity.this, cartListModels,
                            fid, mid, productsids, congifModels, requestQueue);
                }
            });

            wallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chargingprice > 0.09) {
                        RollingLogger.i(TAG, "wallet clicked");
                        cartListModels = cartProQuantityMinus();
                        paymentTypeLoad = "paywave";
                        PaywaveChoosePopUp paywavepopup = new PaywaveChoosePopUp();
                        UserObj obj = new UserObj();
                        obj.setConfigModel(congifModels);
                        obj.setCartModel(cartListModels);
                        obj.setChargingprice(chargingprice);
                        paywavepopup.paywaveChoosePopUp(TypeProfuctActivity.this, obj, requestQueue);
                    } else {
                        pDialog = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                        pDialog.setTitleText("Transaction must not less than RM 0.10");
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

            paywave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chargingprice > 0.09) {
                        RollingLogger.i(TAG, "paywave click");
                        cartListModels = cartProQuantityMinus();
                        UserObj obj = new UserObj();
                        obj.setChargingprice(chargingprice);
                        obj.setCartModel(cartListModels);
                        obj.setConfigModel(congifModels);
                        paymentTypeLoad = "paywave";
                        PaywaveSummaryPopUp paywavepopup = new PaywaveSummaryPopUp();
                        paywavepopup.summaryPopUp(TypeProfuctActivity.this, String.valueOf(chargingprice), "", "paywave", obj, null, requestQueue);
                    } else {
                        pDialog = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.WARNING_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                        pDialog.setTitleText("Transaction must not less than RM 0.10");
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

            walletpaybost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("boost", R.drawable.boost, "Boost");
                }
            });
            walletpayshopee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("shopee", R.drawable.shopeepaylogo, "Shopee");

                }
            });

            walletpaymq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("maybank", R.drawable.maybank, "Maybank");
                }
            });

            walletpaywc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("wechat", R.drawable.wechatpay, "Wechat");
                }
            });

            walletpaytng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("touchngo", R.drawable.touchngo, "Touch'n go");
                }
            });
            walletpayali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("alipay", R.drawable.alipayicon, "Alipay");
                }
            });
            walletpaygrab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPassSummaryParam("grab", R.drawable.grabpayicon, "Grabpay");
                }
            });
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Dialog wallet error - " + ex);
        }
    }

    public void totalcost(double totalcostpay) {
        totalcost = totalcostpay;
    }

    private List<CartListModel> cartProQuantityMinus() {
        for (int i = 0; i < productapiModelList.size(); i++) {
            for (int j = 0; j < cartListModels.size(); j++) {
                int totalqty = 0;

                if (String.valueOf(productapiModelList.get(i).getItem_Number()).equals(cartListModels.get(j).getItemnumber())) {
                    totalqty = Integer.valueOf(productapiModelList.get(i).getQuantity()) - Integer.valueOf(cartListModels.get(j).getItemqty());
                    cartListModels.get(j).setQuantityMinus(totalqty);
                }
            }
        }
        return cartListModels;
    }

    private void setPassSummaryParam(String type, int image, String mtd) {
        cartListModels = cartProQuantityMinus();
        IpaySummaryPopUp ipaySummaryPopUp = new IpaySummaryPopUp();
        UserObj obj = new UserObj();
        obj.setConfigModel(congifModels);
        obj.setCartModel(cartListModels);
        obj.setIpaytype(type);
        obj.setIsloggedin(isloggedin);
        obj.setUserid(UserID);
        obj.setPoints(Points);
        obj.setPid(pid);
        obj.setExpiredate(ExpireDate);
        obj.setUserstatus(UserStatus);
        obj.setImage(image);
        obj.setMtd(mtd);
        obj.setChargingprice(chargingprice);
        paymentTypeLoad = "ipay";
        Dialog dialog = getCustomDialogProduct();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        ipaySummaryPopUp.IpaySummaryPopUp(obj, this, customDialogDispense, requestQueue);
    }

    //IpaySummaryPopUp Use in this class
    public SweetAlertDialog getDialogIpay() {
        if (pDialogIpay != null) {
            if (pDialogIpay.isShowing()) {
                pDialogIpay.dismiss();
                pDialogIpay = null;
            }
        }
        pDialogIpay = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        return pDialogIpay;
    }

    //SarawakPayPayment Use in this class
    public SweetAlertDialog getDialogSarawak(String type) {
        if (pDialogSarawak != null) {
            if (pDialogSarawak.isShowing()) {
                pDialogSarawak.dismiss();
                pDialogSarawak = null;
            }
        }
        switch (type) {
            case "warning":
                pDialogSarawak = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                break;
            case "error":
                pDialogSarawak = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                break;
            case "progress":
                pDialogSarawak = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                break;
        }

        return pDialogSarawak;
    }

    //IpaySummaryPopUp Use in this class
    public SweetAlertDialog getDialogIpayGlobal(String type) {
        if (globalDialogIpay != null) {
            if (globalDialogIpay.isShowing()) {
                globalDialogIpay.dismiss();
                globalDialogIpay = null;
            }
        }
        switch (type) {
            case "warning":
                globalDialogIpay = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                break;
            case "error":
                globalDialogIpay = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                break;
            case "progress":
                globalDialogIpay = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                break;

        }
        return globalDialogIpay;
    }

    //PaywaveSummaryPopUp Use in this class
    public SweetAlertDialog getDialogPaywave(String type) {
        if (pDialogPaywave != null) {
            if (pDialogPaywave.isShowing()) {
                pDialogPaywave.dismiss();
                pDialogPaywave = null;
            }
        }
        switch (type) {
            case "warning":
                pDialogPaywave = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                break;
            case "error":
                pDialogPaywave = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                break;
            case "progress":
                pDialogPaywave = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                break;
        }

        return pDialogPaywave;
    }

    private void clearTypeProductDialog() {
        if (customDialogDispense != null) {
            if (customDialogDispense.isShowing()) {
                customDialogDispense.dismiss();
            }
        }
        if (customDialog != null) {
            if (customDialog.isShowing()) {
                customDialog.dismiss();
            }
        }
        if (sweetAlertDialog != null) {
            if (sweetAlertDialog.isShowing()) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }
        if (pDialogUnableAddProduct != null) {
            if (pDialogUnableAddProduct.isShowing()) {
                pDialogUnableAddProduct.dismiss();
            }
        }
        if (pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
        if (pd != null) {
            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }

    private void clearIpayDialog() {
        if (pDialogIpay != null) {
            if (pDialogIpay.isShowing()) {
                pDialogIpay.dismiss();
            }
        }
        if (globalDialogIpay != null) {
            if (globalDialogIpay.isShowing()) {
                globalDialogIpay.dismiss();
            }
        }
    }

    private void clearSarawakDialog() {
        if (pDialogSarawak != null) {
            if (pDialogSarawak.isShowing()) {
                pDialogSarawak.dismiss();
            }
        }
    }

    private void clearDispenseDialog() {
        if (pDialogThankYouDispense != null) {
            if (pDialogThankYouDispense.isShowing()) {
                pDialogThankYouDispense.dismiss();
            }
        }
    }

    private void clearPaywaveDialog() {
        if (pDialogPaywave != null) {
            if (pDialogPaywave.isShowing()) {
                pDialogPaywave.dismiss();
            }
        }
    }

    public void clearCustomDialogDispense() {
        customDialogDispense = null;
    }

    public Dialog getCustomDialogDispense(String type) {
        if (customDialogDispense == null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            customDialogDispense = new Dialog(this);
            switch (type) {
                case "summary":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_summary);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    customDialogDispense.setCanceledOnTouchOutside(false);
                    break;
                case "fasspay":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_fasspay);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    customDialogDispense.setCanceledOnTouchOutside(false);
                    break;
                case "cash":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_cash);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    customDialogDispense.setCanceledOnTouchOutside(false);
                    break;
                case "dispense":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_dispense_popup);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.setCanceledOnTouchOutside(false);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    break;
                case "dispense2025":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_dispense_popup_2025);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.setCanceledOnTouchOutside(false);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    break;
                case "sarawakpay":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_summary_sarawak);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    customDialogDispense.setCanceledOnTouchOutside(false);

                    break;
                case "paywave":
                    customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    customDialogDispense.setContentView(R.layout.activity_summary_pay_wave);
                    customDialogDispense.setCancelable(true);
                    customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    customDialogDispense.setCanceledOnTouchOutside(false);

                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.gravity = Gravity.CENTER;

                    customDialogDispense.getWindow().setAttributes(lp);
                    break;
            }
        }
        return customDialogDispense;
    }

    public SweetAlertDialog getThankYouDispense() {
        if (pDialogThankYouDispense == null) {
            pDialogThankYouDispense = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        }
        return pDialogThankYouDispense;
    }

    public Dialog getCustomDialogPaywaveChoose() {
        if (customDialogPaywaveChoose == null) {
            customDialogPaywaveChoose = new Dialog(this);
            customDialogPaywaveChoose.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialogPaywaveChoose.setContentView(R.layout.activity_choose_pay_wave);
            customDialogPaywaveChoose.setCancelable(true);
            customDialogPaywaveChoose.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            customDialogPaywaveChoose.setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            customDialogPaywaveChoose.getWindow().setAttributes(lp);
        }
        return customDialogPaywaveChoose;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (countDownHandler != null) {
            countDownHandler.removeCallbacksAndMessages(null);
            if (!checkCash)
                CountDownTimer();
        }
    }

    public Dialog getCustomDialogProduct() {
        enableaddproduct = false;
        if (customDialog == null) {
            customDialog = new Dialog(this);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setContentView(R.layout.popupdialog_selectpayment);
            customDialog.setCancelable(true);
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if (customDialog.getWindow() != null) {
                customDialog.getWindow().setCallback(new UserInteractionAwareCallback(customDialog.getWindow().getCallback(), this));
            }
            TextView tv = customDialog.findViewById(R.id.pricetext);
            View contentView = customDialog.findViewById(android.R.id.content);
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.requestFocus();
        }
        return customDialog;
    }

    public boolean getEnableDisableAddProduct() {
        return enableaddproduct;
    }

    public void setEnableaddproduct(boolean setflag) {
        enableaddproduct = setflag;
    }

    public Button getpaybuttonenable() {
        return pay;
    }

    public boolean isPaymentinProgress(boolean flag) {
        paymentInProgress = flag;
        return paymentInProgress;
    }

    public void displayDialogUnableAddProduct() {
        pDialogUnableAddProduct = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialogUnableAddProduct.setTitle("Payment in Progress, Unable to add Product");
        pDialogUnableAddProduct.show();
    }

    public Handler getCountDownHandler() {
        return countDownHandler;
    }

    @Override
    public void updateValue(String s) {
        if (countFasspay > 0) {
//            tvscantext.setText("Place your card near the machine's contactless payment area to proceed.");
            if (s.equalsIgnoreCase("100")) {
                clearFassPayDialog();
                double price = 0.0;
                for (CartListModel cn : cartListModels) {
                    String log = cn.getItemprice();
                    price = price + Double.parseDouble(log);
                }
                if (cdt != null) {
                    cdt.cancel();
                }

                String versionName = "";
                try {
                    versionName = getPackageManager()
                            .getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                //countDownHandler.removeCallbacksAndMessages(null);
                unregisterReceiver(myReceiver);

                String cardTypeCode = fassPayClass.getCardType();
                String cardType = "";
                switch (cardTypeCode) {
                    case "00":
                        cardType = "Visa";
                        break;
                    case "01":
                        cardType = "MasterCard";
                        break;
                    case "03":
                        cardType = "JCB";
                        break;
                    case "07":
                        cardType = "UnionPay";
                        break;
                    case "08":
                        cardType = "MyDebit";
                        break;
                    case "10":
                        cardType = "Diners";
                        break;
                    case "23":
                        cardType = "Discover";
                        break;
                }

                cartListModels = cartProQuantityMinus();
                UserObj userObj = new UserObj();
                userObj.setMtd("FP (" + fassPayClass.getTransId() + ") " + fassPayClass.getApplicationLabel());
                userObj.setIsloggedin(isloggedin);
                userObj.setIpaytype("Fasspay");
                userObj.setChargingprice(price);
                userObj.setPoints(0);
                userObj.setUserid(0);
                userObj.setPid(pid);
                userObj.setExpiredate(ExpireDate);
                userObj.setUserstatus(0);
                userObj.setCartModel(cartListModels);
                userObj.setConfigModel(congifModels);

                TempTrans(1, userObj, "");

//                DispensePopUpM5 dispensePopUpM5 = new DispensePopUpM5();
//                dispensePopUpM5.DispensePopUp(TypeProfuctActivity.this, userObj, "Success", payid, requestQueue);

                Gson gson = new Gson();
                String jsonString = gson.toJson(userObj);
                if (cdt != null) {
                    cdt.cancel();
                }
                isCountDownCont = true;
                countDownHandler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(TypeProfuctActivity.this, MainActDispenseM4.class);
                intent.putExtra("userObjStr", jsonString);

                startActivity(intent);
            } else if (s.equalsIgnoreCase("304")) {
                pDialogF.getButton(SweetAlertDialog.BUTTON_CANCEL).setVisibility(View.INVISIBLE);
            } else if (s.equalsIgnoreCase("300") || s.equalsIgnoreCase("301") ||
                    s.equalsIgnoreCase("302") || s.equalsIgnoreCase("303") || s.equalsIgnoreCase("304") ||
                    s.equalsIgnoreCase("307") || s.equalsIgnoreCase("308")
            ) {
//                Do nothing
            } else {
//            } else if (s.equalsIgnoreCase("251") || s.equalsIgnoreCase("000") || s.equalsIgnoreCase("299")
//                    || s.equalsIgnoreCase("400") || s.equalsIgnoreCase("401") || s.equalsIgnoreCase("904") ||
//                    s.equalsIgnoreCase("213")) {

                double price = 0.0;
                for (CartListModel cn : cartListModels) {
                    String log = cn.getItemprice();
                    price = price + Double.parseDouble(log);
                }

                cartListModels = cartProQuantityMinus();
                UserObj userObj = new UserObj();
                userObj.setMtd("FP (" + fassPayClass.getTransId() + ") " + fassPayClass.getApplicationLabel());
                userObj.setIsloggedin(isloggedin);
                userObj.setIpaytype("Fasspay");
                userObj.setChargingprice(price);
                userObj.setPoints(0);
                userObj.setUserid(0);
                userObj.setPid(pid);
                userObj.setExpiredate(ExpireDate);
                userObj.setUserstatus(0);
                userObj.setCartModel(cartListModels);
                userObj.setConfigModel(congifModels);

                String paymentFailStr = "";
                switch (s) {
                    case "400":
                        paymentFailStr = "Processing error";
                        break;
                    case "251":
                        paymentFailStr = "Insufficient balance";
                        break;
                    case "000":
                        paymentFailStr = "Transaction Cancelled";
                        break;
                    case "299":
                        paymentFailStr = "Transction Declined";
                        break;
                    case "401":
                        paymentFailStr = "Host timeout";
                        break;
                    case "904":
                        paymentFailStr = "Service not available";
                        break;
                    case "213":
                        paymentFailStr = "Insufficient fund";
                        break;
                    default:
                        paymentFailStr = "Unknown Error-" + s;
                }

                final Handler handlerTimeoutF = new Handler(Looper.getMainLooper());
                handlerTimeoutF.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handlerTimeoutF.removeCallbacksAndMessages(null);
                        clearFassPayDialog();
                        unregisterReceiver(myReceiver);
                        if (cdt != null)
                            cdt.cancel();
                        Uti.freeMemory();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, 15000);

                String strerror = s + "-" + paymentFailStr;

                TempTrans(0, userObj, strerror);
                pDialogError = getDialogIpay();
                pDialogError.getProgressHelper().setBarColor(Color.parseColor("#FFC107"));
                pDialogError.setTitleText("Error-" + s);
                pDialogError.setContentText(paymentFailStr + "\n" + "Please try again");
                pDialogError.setCanceledOnTouchOutside(false);
                pDialogError.setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (handlerTimeoutF != null)
                            handlerTimeoutF.removeCallbacksAndMessages(null);
                        clearFassPayDialog();
                        unregisterReceiver(myReceiver);
                        if (cdt != null)
                            cdt.cancel();
                        sweetAlertDialog.dismissWithAnimation();
                        Uti.freeMemory();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                if (!TypeProfuctActivity.this.isFinishing())
                    pDialogError.show();
            }
        } else {
            countFasspay++;
        }
    }

    private void clearFassPayDialog() {
        if (!isFinishing()) {
            if (customDialogDispenseFass != null) {
                if (customDialogDispenseFass.isShowing()) {
                    customDialogDispenseFass.dismiss();
                }
            }
            if (pDialogF != null) {
                if (pDialogF.isShowing()) {
                    pDialogF.dismiss();
                }
            }
            if (pDialogError != null) {
                if (pDialogError.isShowing()) {
                    pDialogError.dismiss();
                }
            }
        }
    }

    private void TempTrans(int Status, UserObj userObj, String refCode) {

        try {
            RollingLogger.i(TAG, "temp api call start");

            Date currentTime = Calendar.getInstance().getTime();

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = "https://vendingappapi.azurewebsites.net/Api/TempTrans";
            TempTrans transactionModel = new TempTrans();
            transactionModel.setAmount(userObj.getChargingprice());
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(userObj.getUserid());
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType(userObj.getMtd());
            transactionModel.setPaymentMethod(userObj.getIpaytype());
            transactionModel.setPaymentStatus(Status);
            transactionModel.setFreePoints("");
            transactionModel.setPromocode(userObj.getPromname());
            transactionModel.setPromoAmt(userObj.getPromoamt() + "");
            transactionModel.setVouchers("");
            transactionModel.setPaymentStatusDes(refCode);


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
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());
                    RollingLogger.i(TAG, "temp api call error-" + error);
                }
            });
            // pDialog.dismissWithAnimation();

            requestQueue.add(myReq);
        } catch (Exception ex) {
        }
    }

    private class JsonTask2 extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pd.setContentText("Processing");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                    System.out.println("Response  :" + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

            System.out.println("result for get userdetails  :" + result);

            if (result != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<UserName>() {
                }.getType();

                UserName details = gson.fromJson(result, type);

                myuser = details;
                new JsonTask().execute("https://memberappapi.azurewebsites.net/Api/Points?userID=" + myuser.getUserID());
            } else
                showerrdialog();

        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new SweetAlertDialog(TypeProfuctActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pd.setContentText("Processing");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {


                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                    System.out.println("Response  :" + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            System.out.println("result for get points  :" + result);

            if (result != null) {
                System.out.println(result);
                System.out.println("id " + qrid);
                Gson gson = new Gson();
                Type type = new TypeToken<List<PointsModel>>() {
                }.getType();
                ArrayList<PointsModel> pointsModels = gson.fromJson(result, type);
                // new JsonTask2().execute("https://memberappapi.azurewebsites.net/Api/Login/"+qrid);
                mypoints = pointsModels.get(0);
                mydetl = new UserDetails(mypoints, myuser);

                // String username ="Welcome " +mydetl.getFirstName()+" "+mydetl.getLastName()+". You have "+mydetl.getPoints();

                Intent it = new Intent(TypeProfuctActivity.this, SelectPaymentActivity.class);
                //To pass:
                it.putExtra("fname", mydetl.getFirstName());
                it.putExtra("lname", mydetl.getLastName());
                it.putExtra("points", mydetl.getPoints());
                it.putExtra("uid", mydetl.getUserID());
                it.putExtra("pid", mydetl.getId());
                it.putExtra("ustatus", mydetl.getUserStatus());
                it.putExtra("expdate", mydetl.getExpireDate());
                it.putExtra("login", true);

                pDialog.dismissWithAnimation();
                pd.dismissWithAnimation();
                startActivity(it);

            } else
                showerrdialog();
        }
    }
}

