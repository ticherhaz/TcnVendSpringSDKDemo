package com.tcn.sdk.springdemo.tcnSpring;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcn.sdk.springdemo.Dispense.CartListAdapter2025;
import com.tcn.sdk.springdemo.Dispense.countObj;
import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.Pointsend;
import com.tcn.sdk.springdemo.Model.TransactionModel;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.ys.springboard.control.TcnVendEventResultID;
import com.ys.springboard.control.TcnVendIF;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActDispenseM4 extends AppCompatActivity {

    private static final String TAG = "MainAct";
    private final int userstatus = 0;
    private OutDialog m_OutDialog = null;
    private int userid = 0;
    private int pid = 0;
    private int countItem = 0;
    private int productId = 0;
    private int countProduct = 1;
    private String productsids = "", paytype = "", fid = "", mid = "", mtd = "", payid = "", paystatus = "", allstatuses = "";
    private double chargingprice = 0.00, points = 0.00, newpoints = 0.00;
    private Boolean isloggedin = false, checkPopUp = false;
    private List<CongifModel> congifModels;
    private ArrayList<CartListModel> cartListModels;
    private List<countObj> arr_count;
    private CartListAdapter2025 adapter;
    private RecyclerView recyclerViewCart;
    private SweetAlertDialog sdthankyou, sdError;
    private RequestQueue requestQueue;
    private Handler checkErrorHandler, handlerQuit;
    private String transactionNo;

    private View dialogView;
    private TextView messageTextView;
    private boolean isDialogInitialized = false;

    private TcnVendIF.VendEventListener listener = event -> {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "eventId : " + event.m_iEventID + " param1: " + event.m_lParam1 +
                " param2: " + event.m_lParam2 + " param3: " + event.m_lParam3 + " param4: " + event.m_lParam5);

        if (event.m_lParam3 == TcnVendEventResultID.SHIP_SHIPING) {
            checkErrorHandler = new Handler(Looper.getMainLooper());
            checkErrorHandler.postDelayed(() -> {
                // Start to update data to database
                handleCallApiControlBoardError();

                checkErrorHandler.removeCallbacksAndMessages(null);
                sdError = new SweetAlertDialog(MainActDispenseM4.this, SweetAlertDialog.ERROR_TYPE);
                sdError.setTitleText("Error.");
                sdError.setContentText("Control Board Error. Please contact careline");
                sdError.setCanceledOnTouchOutside(false);
                sdError.setOnDismissListener(dialog -> {
                    if (sdError != null && sdError.isShowing()) {
                        sdError.dismiss();
                    }
                    if (handlerQuit != null) {
                        handlerQuit.removeCallbacksAndMessages(null);
                    }
                    navigateToMainActivity();
                });
                if (sdError != null && !sdError.isShowing()) {
                    sdError.show();
                }

                handlerQuit = new Handler(Looper.getMainLooper());
                handlerQuit.postDelayed(() -> {
                    handlerQuit.removeCallbacksAndMessages(null);
                    navigateToMainActivity();
                }, 15000);
            }, 12000);

        } else if (event.m_lParam3 == TcnVendEventResultID.SHIP_SUCCESS) {
            handleShipSuccess();
        } else if (event.m_lParam3 == TcnVendEventResultID.SHIP_FAIL) {
            handleShipFailure();
        }
    };

    public static <T> ArrayList<T> listToArrayList(List<T> list) {
        return list != null ? new ArrayList<>(list) : null;
    }

    private void handleShipSuccess() {
        if (checkErrorHandler != null) {
            checkErrorHandler.removeCallbacksAndMessages(null);
        }
        arr_count.get(countItem).setCheckStatus(true);
        for (int i = 0; i < cartListModels.size(); i++) {
            if (arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())) {
                if (productId != Integer.parseInt(arr_count.get(countItem).getproduct())) {
                    countProduct = 1;
                } else {
                    countProduct++;
                }
                productId = Integer.parseInt(arr_count.get(countItem).getproduct());
                ArrayList<String> itemStatus = cartListModels.get(i).getItemStatus();
                if (itemStatus.isEmpty()) {
                    itemStatus = new ArrayList<>();
                    itemStatus.add(countProduct + ".Success");
                    cartListModels.get(i).setItemStatus(itemStatus);
                } else {
                    itemStatus.add(countProduct + ".Success");
                }
                break;
            }
        }
        runOnUiThread(() -> adapter.update(cartListModels));
        checkResponse(true);
    }

    private void handleShipFailure() {
        if (checkErrorHandler != null) {
            checkErrorHandler.removeCallbacksAndMessages(null);
        }
        arr_count.get(countItem).setCheckStatus(false);
        for (int i = 0; i < cartListModels.size(); i++) {
            if (arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())) {
                if (productId != Integer.parseInt(arr_count.get(countItem).getproduct())) {
                    countProduct = 1;
                } else {
                    countProduct++;
                }
                productId = Integer.parseInt(arr_count.get(countItem).getproduct());
                ArrayList<String> itemStatus = cartListModels.get(i).getItemStatus();
                if (itemStatus.isEmpty()) {
                    itemStatus = new ArrayList<>();
                    itemStatus.add(countProduct + ".Failed");
                    cartListModels.get(i).setItemStatus(itemStatus);
                } else {
                    itemStatus.add(countProduct + ".Failed");
                }
                break;
            }
        }
        runOnUiThread(() -> adapter.update(cartListModels));
        checkResponse(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispense_popup_2025);
        Log.i(TAG, "MainAct onCreate()");
        initializeDialogComponents(); // Initialize dialog components once
        initView();
    }

    private void initializeDialogComponents() {
        LayoutInflater inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.dialog_message, null);
        messageTextView = dialogView.findViewById(R.id.dialogMessage);
        isDialogInitialized = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TcnVendIF.getInstance().registerListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TcnVendIF.getInstance().unregisterListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupResources();
    }

    private void cleanupResources() {
        m_OutDialog = null;
        listener = null;

        if (checkErrorHandler != null) {
            checkErrorHandler.removeCallbacksAndMessages(null);
        }
        if (handlerQuit != null) {
            handlerQuit.removeCallbacksAndMessages(null);
        }
        if (sdthankyou != null && sdthankyou.isShowing()) {
            sdthankyou.dismiss();
        }
        if (sdError != null && sdError.isShowing()) {
            sdError.dismiss();
        }
        sdthankyou = null;
        sdError = null;
        dialogView = null;
        messageTextView = null;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(MainActDispenseM4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initView() {
        Intent intent = getIntent();
        String userObjStr = intent.getStringExtra("userObjStr");
        transactionNo = intent.getStringExtra("transactionNo");

        Gson gson = new Gson();
        UserObj userobj = gson.fromJson(userObjStr, new TypeToken<UserObj>() {
        }.getType());
        mtd = userobj.getMtd();
        isloggedin = userobj.getIsloggedin();
        paytype = userobj.getIpaytype();
        chargingprice = userobj.getChargingprice();
        this.payid = payid;
        points = userobj.getPoints();
        userid = userobj.getUserid();
        pid = userobj.getPid();
        this.paystatus = paystatus;
        congifModels = userobj.configModel;
        ArrayList<CartListModel> cartListModelList = listToArrayList(userobj.getCartModel());

        for (CongifModel cn : congifModels) {
            fid = cn.getFid();
            mid = cn.getMid();
        }

        cartListModels = cartListModelList;
        StringBuilder productsBuilder = new StringBuilder();
        for (CartListModel cart : cartListModels) {
            int qty = Integer.parseInt(cart.getItemqty());
            for (int x = 0; x < qty; x++) {
                productsBuilder.append(cart.getProdid()).append(",");
            }
        }
        productsids = productsBuilder.toString();

        arr_count = new ArrayList<>();
        int countTotal = 0;
        //set all position to default 0
        for (int i = 0; i < cartListModels.size(); i++) {
            cartListModels.get(i).setPostion("0");

            int size = Integer.parseInt(cartListModels.get(i).getItemqty());
            for (int j = 0; j < size; j++) {
                countObj obj = new countObj();
                obj.setproduct(cartListModels.get(i).getSerial_port());
                obj.setcount(countTotal);
                obj.setposition(0);
                obj.setqty(cartListModelList.get(i).getItemqty());
                arr_count.add(obj);
                countTotal++;
            }
        }
        setTextViewTotalPrice();
        setupForDispense();
        startDispense();
    }

    private void setupForDispense() {
        recyclerViewCart = findViewById(R.id.mrecyclr);
        adapter = new CartListAdapter2025(cartListModels);
        recyclerViewCart.setAdapter(adapter);
    }

    private void setTextViewTotalPrice() {
        final TextView tvTotalPrice = findViewById(R.id.pricetext);
        tvTotalPrice.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
    }

    private void startDispense() {
        String itemno = arr_count.get(countItem).getproduct();
        int slotNo = Integer.parseInt(itemno);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            handler.removeCallbacksAndMessages(null);
            TcnVendIF.getInstance().reqShipTest(slotNo);
        }, 1000);
    }

    private void checkResponse(boolean check) {
        for (int i = 0; i < arr_count.size(); i++) {
            if (countItem == arr_count.get(arr_count.size() - 1).getcount()) {
                arr_count.get(i).setposition(1);
                break;
            }
        }

        if (countItem != arr_count.get(arr_count.size() - 1).getcount()) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                handler.removeCallbacksAndMessages(null);
                startDispense();
            }, 3000);
        }

        arr_count.get(countItem).setposition(1);
        countItem++;

        updateStatus(
                Integer.parseInt(arr_count.get(countItem - 1).getproduct()),
                check ? "1" : "2",
                check ? 2 : 3
        );
    }

    private void handleCallApiControlBoardError() {
        allstatuses = "";
        if (arr_count != null && !arr_count.isEmpty()) {
            StringBuilder statusBuilder = new StringBuilder();
            for (countObj obj : arr_count) {
                int statusCheck = 7;
                statusBuilder.append(obj.getproduct())
                        .append("x")
                        .append(obj.getqty())
                        .append(":")
                        .append(statusCheck)
                        .append(",");
            }
            allstatuses = statusBuilder.toString();
            updatetransactiondb(allstatuses, "Control Board Error");
        }
    }

    private void updateStatus(int product, String pos, int status) {
        String productcode = String.valueOf(product);
        boolean checkhere = false;

        int check = 0;
        for (countObj obj : arr_count) {
            if (obj.getproduct().equalsIgnoreCase(productcode) && obj.getposition() == 0) {
                check++;
            }
        }

        if (check == 0) {
            for (int i = 0; i < cartListModels.size(); i++) {
                if (cartListModels.get(i).getSerial_port().equalsIgnoreCase(productcode)) {
                    cartListModels.get(i).setPostion(pos);
                    checkhere = true;

                    allstatuses += product + "x" + cartListModels.get(i).getItemqty() + ":" + status + ",";

                    int finalI = i;
                    runOnUiThread(() -> adapter.updateItemChange(finalI));
                    break;
                }
            }
        }

        if (checkhere) {
            int checkc = 0;
            for (countObj obj : arr_count) {
                if (obj.getposition() == 0) {
                    checkc++;
                }
            }

            if (checkc == 0) {
                allstatuses = "";
                for (countObj obj : arr_count) {
                    int statusCheck = obj.getCheckStatus() ? 2 : 3;
                    allstatuses += obj.getproduct() + "x" + obj.getqty() + ":" + statusCheck + ",";
                }

                final Handler handlerDis = new Handler(Looper.getMainLooper());
                handlerDis.postDelayed(() -> {
                    handlerDis.removeCallbacksAndMessages(null);

                    Handler handler13 = new Handler();
                    handler13.postDelayed(() -> {
                        checkPopUp = true;
                        runOnUiThread(() -> {
                            if (sdthankyou != null && sdthankyou.isShowing()) {
                                sdthankyou.dismiss();
                            }
                        });
                        handler13.removeCallbacksAndMessages(null);
                        navigateToMainActivity();
                    }, 15000);

                    final Handler handlerprd = new Handler();
                    handlerprd.postDelayed(() -> {
                        handlerprd.removeCallbacksAndMessages(null);
                        updateprodqty();
                    }, 1000);

                    if (isloggedin) {
                        updatemobiletransactiondb();
                        newpoints = points - chargingprice;
                        updatepointsmdb();
                    }
                    updatetransactiondb(allstatuses, "");

                    runOnUiThread(() -> {
                        int successInt = 0, failInt = 0;
                        for (countObj obj : arr_count) {
                            if (obj.getCheckStatus()) successInt++;
                            else failInt++;
                        }

                        // REUSE DIALOG INSTANCE
                        if (sdthankyou == null) {
                            sdthankyou = new SweetAlertDialog(MainActDispenseM4.this, SweetAlertDialog.SUCCESS_TYPE);
                            sdthankyou.setTitleText("Thank you.");

                            // Use pre-inflated view
                            if (isDialogInitialized) {
                                sdthankyou.setCustomView(dialogView);
                            }

                            sdthankyou.setCanceledOnTouchOutside(false);
                            sdthankyou.setOnDismissListener(dialog -> {
                                handler13.removeCallbacksAndMessages(null);
                                if (sdthankyou != null && sdthankyou.isShowing()) {
                                    sdthankyou.dismiss();
                                }
                                if (!checkPopUp) {
                                    navigateToMainActivity();
                                }
                            });
                        }

                        // UPDATE EXISTING VIEW
                        if (isDialogInitialized) {
                            messageTextView.setText("Your order is dispensed.\n\nSuccess: " + successInt + "\nFail: " + failInt);
                        }

                        try {
                            if (!isFinishing() && !sdthankyou.isShowing()) {
                                sdthankyou.show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }, 3000);
            }
        }
    }

    public void updatetransactiondb(String status, final String remarks) {
        RollingLogger.i(TAG, "updatetransactiondb api call");
        try {
            Date currentTime = Calendar.getInstance().getTime();

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            String url = "https://vendingappapi.azurewebsites.net/Api/Transaction";

            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setAmount(chargingprice);
            transactionModel.setmDate(currentTime);
            transactionModel.setUserID(userid);
            transactionModel.setFranchiseID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductsIdes(productsids);

            String versionName = "";
            try {
                versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            final String mtdUpdated = mtd + " (" + transactionNo + ") M4 " + versionName;
            transactionModel.setPaymentType(mtdUpdated);
            transactionModel.setPaymentMethod(paytype);
            transactionModel.setPromocode(status);
            transactionModel.setPaymentStatus(paystatus);
            transactionModel.setPaymentID(payid);
            transactionModel.setRemarks(remarks);

            JSONObject jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            final String requestBody = jsonParam.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> RollingLogger.i(TAG, "updatetransactiondb api call response-" + response),
                    error -> RollingLogger.i(TAG, "updatetransactiondb api call error-" + error)) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Dispense update transaction error - " + ex);
        }
    }

    private void updateprodqty() {
        for (CartListModel cart : cartListModels) {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            String url = "https://vendingappapi.azurewebsites.net/api/Product/" + cart.getFprodid();
            final String requestBody = String.valueOf(cart.getQuantityMinus());

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    response -> Log.i("VOLLEY", response != null ? response : "empty response"),
                    error -> Log.e("VOLLEY", error != null ? error.toString() : "unknown error")
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    // Return a safe string instead of null to avoid NPEs
                    String responseString = response != null ? String.valueOf(response.statusCode) : "no response";
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        }
    }


    private void updatemobiletransactiondb() {
        Date currentTime = Calendar.getInstance().getTime();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        String url = "https://memberappapi.azurewebsites.net/Api/Transaction";

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(chargingprice);
        transactionModel.setmDate(currentTime);
        transactionModel.setUserID(userid);
        transactionModel.setFranchiseID(fid);
        transactionModel.setMachineID(mid);
        transactionModel.setProductsIdes(productsids);

        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        final String mtdUpdated = mtd + " M4 " + versionName;
        transactionModel.setPaymentType(mtdUpdated);
        transactionModel.setPaymentMethod(paytype);

        try {
            JSONObject jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            final String requestBody = jsonParam.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> Log.i("VOLLEY", response),
                    error -> Log.e("VOLLEY", error.toString())
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            Log.d("test2", e.toString());
        }
    }

    private void updatepointsmdb() {
        Date date = Calendar.getInstance().getTime();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        String url = "https://memberappapi.azurewebsites.net/Api/Points";

        Pointsend pointModel = new Pointsend();
        pointModel.setID(pid);
        pointModel.setUserID(userid);
        pointModel.setPoints(newpoints);
        pointModel.setExpireDate(date);
        pointModel.setUserStatus(userstatus);

        try {
            JSONObject jsonParam = new JSONObject(new Gson().toJson(pointModel));
            final String requestBody = jsonParam.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    response -> Log.i("VOLLEY", response),
                    error -> Log.e("VOLLEY", error.toString())
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    return requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}