//package com.tcn.sdk.springdemo.ComAssistant;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.serialport.SerialPort;
//import android.util.Log;
//import android.view.Window;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.HttpHeaderParser;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.tcn.sdk.springdemo.Dispense.AppLogger;
//import com.tcn.sdk.springdemo.Dispense.CartListAdapter;
//import com.tcn.sdk.springdemo.Dispense.countObj;
//import com.tcn.sdk.springdemo.MainActivity;
//import com.tcn.sdk.springdemo.Model.CartListModel;
//import com.tcn.sdk.springdemo.Model.CongifModel;
//import com.tcn.sdk.springdemo.Model.Pointsend;
//import com.tcn.sdk.springdemo.Model.TransactionModel;
//import com.tcn.sdk.springdemo.Model.UserObj;
//import com.tcn.sdk.springdemo.Note.CashNoteActivity;
//import com.tcn.sdk.springdemo.R;
//import com.tcn.sdk.springdemo.Utilities.SharedPref;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.lang.ref.WeakReference;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import java.util.Random;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class DispensePopUpCashM5 {
//    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
//    private final int count1 = 0;
//    private final int queueNow = 0;
//    private final int posall = -1;
//    private final String TAG = "DispensePopUpCashM5";
//    private final int mInterval = 3000;
//    private final boolean checkPosAll = false;
//    Thread mThread;
//    SerialPort serialPort;
//    String devPath;
//    int baudrate;
//    int no = 0;
//    byte[] ackBytes = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x42, 0x00, 0x43};
//    byte[] ackBytesTest = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x41, 0x00, 0x40};
//    Queue<byte[]> queue = new LinkedList<byte[]>();
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };
//    double discountamt = 0.00;
//    Boolean isloggedin = false;
//    Boolean isuserpaying = false;
//    Boolean threadintrupt = false;
//    int userid = 0;
//    int userstatus = 0;
//    String productsids = "";
//    int pid = 0;
//    String vids = "";
//    String paytype = "";
//    String promname = "";
//    double rrp = 0.00;
//    double chargingprice = 0.00;
//    double points = 0.00;
//    double newpoints = 0.00;
//    String fid = "";
//    String mid = "";
//    String mtd = "";
//    String payid = "";
//    String paystatus = "";
//    String remakrs = "";
//    SerialControl ComA;
//    String allstatuses = "";
//    private DataDisplayThread dataDisplayThread;
//    private List<CartListModel> cartListModels;
//    private List<CongifModel> congifModels;
//    private SweetAlertDialog sdthankyou;
//    private SweetAlertDialog sweetAlertDialog;
//    private List<countObj> arr_count;
//    private CartListAdapter adapter;
//    private ListView list;
//    private Handler mHandler;
//    private String LDispense = "";
//    private Dialog customDialogDispense;
//    private CashNoteActivity activity;
//    private List<CartListModel> cartListModelList;
//    private List<CartListModel> cartListModelNewList;
//    private boolean checkPopUp = false;
//    private RequestQueue requestQueue;
//    private int countItem = 0;
//    private int countDispense = 0;
//    private String commandStr = "";
//    private int num = 0;
//
//    private static String convertToHexadecimalWithLeadingZero(int decimalValue) {
//        // Convert decimal to hexadecimal string and add a leading zero if needed
//        String hexString = Integer.toHexString(decimalValue).toUpperCase();
/// /        if(hexString.length()==2){
/// /            hexString="00"+hexString;
/// /        }
//        return (hexString.length() == 1) ? "0" + hexString : hexString;
//    }
//
//    public void DispensePopUp(CashNoteActivity activity, UserObj userobj, String paystatus, String payid, RequestQueue requestQueue) {
//        RollingLogger.i(TAG, "DispensePopUpCashM5 start");
//
//        this.requestQueue = requestQueue;
//        mtd = userobj.getMtd();
//        this.activity = activity;
//        this.cartListModelList = userobj.getCartModel();
//        isloggedin = userobj.getIsloggedin();
//        paytype = userobj.getIpaytype();
//        chargingprice = userobj.getChargingprice();
//        this.payid = payid;
//        points = userobj.getPoints();
//        userid = userobj.getUserid();
//        pid = userobj.getPid();
//        this.paystatus = paystatus;
//
//        ComA = new SerialControl();
//        dataDisplayThread = new DataDisplayThread(activity);
//        dataDisplayThread.start();
//        startOpenPort();
//
/// /        activity.getCountDownHandler().removeCallbacksAndMessages(null);
//        Log.d("Thread Active", "Thread Active-" + Thread.activeCount());
//
//        if (isloggedin) {
//            points = userobj.getPoints();
//            userid = userobj.getUserid();
//            userstatus = userobj.getUserstatus();
//            pid = userobj.getPid();
//            newpoints = userobj.getPoints();
//        }
//
//        customDialogDispense = new Dialog(activity);
//        customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        customDialogDispense.setContentView(R.layout.activity_dispense_popup);
//        customDialogDispense.setCancelable(true);
//        customDialogDispense.setCanceledOnTouchOutside(false);
//
//        if (customDialogDispense.getWindow() != null)
//            customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        if (!activity.isFinishing()) {
//            customDialogDispense.show();
//        }
//
//        SharedPref.init(activity);
//        LDispense = SharedPref.read(SharedPref.Ldispense, "");
//
//        RollingLogger.i(TAG, "Start Dispensing Activity");
//
//        list = customDialogDispense.findViewById(R.id.mrecyclr);
//
//        try {
////            connectSerialPort();
//            congifModels = userobj.configModel;
//
//            for (CongifModel cn : congifModels) {
//                fid = cn.getFid();
//                mid = cn.getMid();
//            }
//
//            cartListModels = cartListModelList;
//            for (CartListModel cart : cartListModels) {
//                int qty;
//                qty = Integer.parseInt(cart.getItemqty());
//                for (int x = 0; x < qty; x++) {
//                    productsids += cart.getProdid() + ",";
//                }
//            }
//            arr_count = new ArrayList<>();
//            int countTotal = 0;
//            //set all position to default 0
//            for (int i = 0; i < cartListModels.size(); i++) {
//                cartListModels.get(i).setPostion("0");
//                RollingLogger.i(TAG, "cart item number-" + cartListModels.get(i).getItemnumber());
//                RollingLogger.i(TAG, "cart item qty-" + cartListModels.get(i).getItemqty());
//                RollingLogger.i(TAG, "cart item name-" + cartListModels.get(i).getItemname());
//                RollingLogger.i(TAG, "cart item price-" + cartListModels.get(i).getItemprice());
//
//                int size = Integer.parseInt(cartListModels.get(i).getItemqty());
//                for (int j = 0; j < size; j++) {
//                    countObj obj = new countObj();
//                    obj.setproduct(cartListModels.get(i).getSerial_port());
//                    obj.setcount(countTotal);
//                    obj.setposition(0);
//                    arr_count.add(obj);
//                    countTotal = countTotal + 1;
//                }
//
//            }
//
//            setTextViewTotal();
//            setupForDispense();
//            startDispense();
//        } catch (Exception ex) {
//            RollingLogger.i(TAG, "Dispense oncreate error - " + ex);
//        }
//    }
//
//    private void setupForDispense() {
//        RollingLogger.i(TAG, "setupForDispense");
//        threadintrupt = true;
//        isuserpaying = true;
//        adapter = new CartListAdapter(activity, cartListModels);
//        //adapter.handleDefaultStatusCallback(() -> {
//        //  initCustomDialogDispenseDismiss();
//        //});
//        list.setAdapter(adapter);
//    }
//
//    private void setTextViewTotal() {
//        final TextView tvPriceTotal = customDialogDispense.findViewById(R.id.pricetext);
//        tvPriceTotal.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
//    }
//
//    private void updateStatus(int product, String pos, int status) {
//        String productcode = String.valueOf(product);
//        boolean checkhere = false;
//        //Log.d("print", "print--"+productcode+"/"+pos+"/"+String.valueOf(status));
//
//        int check = 0;
//        for (int j = 0; j < arr_count.size(); j++) {
//            System.out.println("loggings-count-" + arr_count.get(j).getposition() + "-product-" + arr_count.get(j).getproduct());
//            if (arr_count.get(j).getproduct().equalsIgnoreCase(productcode) && arr_count.get(j).getposition() == 0) {
//                check++;
//            }
//        }
//
//        if (check == 0) {
//            for (int i = 0; i < cartListModels.size(); i++) {
//                System.out.println("loggings-countmodel-" + cartListModels.get(i).getSerial_port() + "-productcode-" + productcode + "-pos-" + cartListModels.get(i).getPosition());
//                if (cartListModels.get(i).getSerial_port().equalsIgnoreCase(productcode)) {
//                    cartListModels.get(i).setPostion(pos);
//                    checkhere = true;
//
//                    allstatuses += product + "x" + cartListModels.get(i).getItemqty() + ":" + status + ",";
//                }
//            }
//        }
//
//        String text = "";
//        if (checkhere) {
//            handler.post(new RunableEx(text) {
//                public void run() {
//
//
//                    int checkc = 0;
//                    for (int i = 0; i < arr_count.size(); i++) {
//                        if (arr_count.get(i).getposition() == 0) {
//                            checkc++;
//                        }
//                    }
//
//                    if (checkc == 0) {
//                        final Handler handler = new Handler(Looper.getMainLooper());
//                        handler.postDelayed(() -> {
//                            adapter.update(cartListModels);
//                            handler.removeCallbacksAndMessages(null);
//
//                            CloseComPort(ComA);
//
//                            Handler handler13 = new Handler();
//                            handler13.postDelayed(() -> {
//                                checkPopUp = true;
//                                try {
//                                    if (sdthankyou != null) {
//                                        if (sdthankyou.isShowing()) {
//                                            sdthankyou.dismiss();
//                                        }
//                                    }
//                                } catch (Exception ex) {
//                                }
//                                try {
//                                    initCustomDialogDispenseDismiss();
//                                } catch (Exception ex) {
//                                }
//                                handler.removeCallbacksAndMessages(null);
//                                handler13.removeCallbacksAndMessages(null);
//                                freeMemory();
//                                Intent intent = new Intent(activity, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                activity.startActivity(intent);
//                                activity.finish();
//                                //if(LDispense.equalsIgnoreCase("true")) {
//                                RollingLogger.i(TAG, "Dispensing Activity Closed");
//                                //}
//                            }, 15000);
//                            //if(LDispense.equalsIgnoreCase("true")) {
//                            RollingLogger.i(TAG, "End Dispensing Activity");
//                            //}
//
////                        Temporary Disable
//                            RollingLogger.i(TAG, "api call uptqty");
////                                updateprodqty();
//                            final Handler handlerprd = new Handler();
//                            handlerprd.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    handlerprd.removeCallbacksAndMessages(null);
//                                    updateprodqty();
//                                }
//                            }, 1000);
//
//                            if (isloggedin) {
//                                updatemobiletransactiondb();
//                                newpoints = points - chargingprice;
//                                updatepointsmdb();
//                            }
//                            updatetransactiondb(allstatuses);
//
//                            sdthankyou = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE);
//                            sdthankyou.setTitleText("Thank you.")
//                                    .setContentText("Your order is dispensed please get ready to pickup.");
//                            sdthankyou.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                @Override
//                                public void onDismiss(DialogInterface dialog) {
//                                    handler13.removeCallbacksAndMessages(null);
//                                    handler.removeCallbacksAndMessages(null);
//                                    if (sdthankyou != null) {
//                                        if (sdthankyou.isShowing()) {
//                                            sdthankyou.dismiss();
//                                            RollingLogger.i(TAG, "Thank you dialog dismiss");
//                                        }
//                                    }
//                                    initCustomDialogDispenseDismiss();
//                                    if (!checkPopUp) {
//                                        RollingLogger.i(TAG, "jump main activity");
//                                        freeMemory();
//                                        Intent intent = new Intent(activity, MainActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        activity.startActivity(intent);
//                                        activity.finish();
//                                        if (LDispense.equalsIgnoreCase("true")) {
//                                            RollingLogger.i(TAG, "Dispensing Activity Closed");
//                                        }
//                                    }
//                                }
//                            });
//                            try {
//                                if (!activity.isFinishing()) {
//                                    sdthankyou.show();
//                                }
//                            } catch (Exception ex) {
//                            }
//                        }, 3000);
//                    }
//
//                }
//            });
//        }
//    }
//
//    public void freeMemory() {
//        System.runFinalization();
//        Runtime.getRuntime().gc();
//        System.gc();
//    }
//
//    public void updatetransactiondb(String status) {
//        RollingLogger.i(TAG, "updatetransactiondb api call");
//        RollingLogger.i(TAG, "updatetransactiondb api call-amount-" + chargingprice);
//        RollingLogger.i(TAG, "updatetransactiondb api call-fid-" + fid);
//        RollingLogger.i(TAG, "updatetransactiondb api call-mid-" + mid);
//        RollingLogger.i(TAG, "updatetransactiondb api call-productsids-" + productsids);
//        RollingLogger.i(TAG, "updatetransactiondb api call-paytype-" + paytype);
//        try {
//            Date currentTime = Calendar.getInstance().getTime();
//
//            if (requestQueue == null) {
//                requestQueue = Volley.newRequestQueue(activity);
//            }
//            String tag_json_obj = "json_obj_req";
//            JSONObject jsonParam = null;
//            String url = "https://vendingappapi.azurewebsites.net/Api/Transaction";
//
//            TransactionModel transactionModel = new TransactionModel();
//            transactionModel.setAmount(chargingprice);
//            transactionModel.setmDate(currentTime);
//            transactionModel.setUserID(userid);
//            transactionModel.setFranchiseID(fid);
//            transactionModel.setMachineID(mid);
//            transactionModel.setProductsIdes(productsids);
//            transactionModel.setPaymentType(mtd);
//            transactionModel.setPaymentMethod(paytype);
//            transactionModel.setFreePoints(String.valueOf(rrp));
//            transactionModel.setPromocode(status);
//            transactionModel.setPromoamount(discountamt + "");
//            transactionModel.setPaymentStatus(paystatus);
//            transactionModel.setPaymentID(payid);
//            transactionModel.setRemarks(remakrs);
//
//            System.out.println("Trans Voucher id =" + vids);
//
//            try {
//                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            final String requestBody = jsonParam.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//
//                public void onResponse(String response) {
//                    //updateprodqty();
//                    System.out.println("Trans =" + response);
//
//                    RollingLogger.i(TAG, "updatetransactiondb api call response-" + response);
//                    Log.i("VOLLEY", response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY", error.toString());
//                    RollingLogger.i(TAG, "updatetransactiondb api call error-" + error);
//
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        Log.d("InputStream", String.valueOf(response));
//                        //  responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//            stringRequest.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
//            requestQueue.add(stringRequest);
//        } catch (Exception ex) {
//            RollingLogger.i(TAG, "Dispense update transaction error - " + ex);
//        }
//
//
//    }
//
//    private void updateprodqty() {
//
//        int totalqty = 0;
//        for (int i = 0; i < cartListModels.size(); i++) {
//            totalqty = 0;
//
//            System.out.println("total qty= " + totalqty + " prod id= " + cartListModels.get(i).getProdid());
//            if (requestQueue == null) {
//                requestQueue = Volley.newRequestQueue(activity);
//            }
//            JSONObject jsonParam = null;
//            String url = "https://vendingappapi.azurewebsites.net/api/Product/" + cartListModels.get(i).getFprodid();
//
//            final String requestBody = String.valueOf(cartListModels.get(i).getQuantityMinus());
//
//            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
//
//                public void onResponse(String response) {
//                    Log.i("VOLLEY", response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e("VOLLEY", error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        Log.d("InputStream", String.valueOf(response));
//                        //  responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            requestQueue.add(stringRequest);
//
//        }
//
//    }
//
//    private void updatemobiletransactiondb() {
//
//        Date currentTime = Calendar.getInstance().getTime();
//
//        if (requestQueue == null) {
//            requestQueue = Volley.newRequestQueue(activity);
//        }
//        JSONObject jsonParam = null;
//        String url = "https://memberappapi.azurewebsites.net/Api/Transaction";
//
//        TransactionModel transactionModel = new TransactionModel();
//        transactionModel.setAmount(chargingprice);
//        transactionModel.setmDate(currentTime);
//        transactionModel.setUserID(userid);
//        transactionModel.setFranchiseID(fid);
//        transactionModel.setMachineID(mid);
//        transactionModel.setProductsIdes(productsids);
//        transactionModel.setPaymentType(mtd);
//        transactionModel.setPaymentMethod(paytype);
//        transactionModel.setFreePoints(String.valueOf(rrp));
//        transactionModel.setPromocode(promname);
//        transactionModel.setPromoamount(discountamt + "");
//        transactionModel.setVouchers(vids);
//
//        System.out.println("Trans Voucher id =" + vids);
//
//
//        try {
//            jsonParam = new JSONObject(new Gson().toJson(transactionModel));
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.d("test2", e.toString());
//        }
//
//
//        final String requestBody = jsonParam.toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//
//            public void onResponse(String response) {
//                Log.i("VOLLEY", response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("VOLLEY", error.toString());
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                String responseString = "";
//                if (response != null) {
//                    Log.d("InputStream", String.valueOf(response));
//                    //  responseString = String.valueOf(response.statusCode);
//                    // can get more details such as response.headers
//                }
//                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//            }
//        };
//
//        requestQueue.add(stringRequest);
//
//
//    }
//
//    private void updatepointsmdb() {
//
//        // Date date = null;
//        Date date = Calendar.getInstance().getTime();
//
//        if (requestQueue == null) {
//            requestQueue = Volley.newRequestQueue(activity);
//        }
//        JSONObject jsonParam = null;
//        String url = "https://memberappapi.azurewebsites.net/Api/Points";
//
//        Pointsend pointModel = new Pointsend();
//        pointModel.setID(pid);
//        pointModel.setUserID(userid);
//        pointModel.setPoints(newpoints);
//        pointModel.setExpireDate(date);
//        pointModel.setUserStatus(userstatus);
//
//        try {
//            jsonParam = new JSONObject(new Gson().toJson(pointModel));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        final String requestBody = jsonParam.toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
//
//            public void onResponse(String response) {
//                Log.i("VOLLEY", response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("VOLLEY", error.toString());
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                String responseString = "";
//                if (response != null) {
//                    Log.d("InputStream", String.valueOf(response));
//                    //  responseString = String.valueOf(response.statusCode);
//                    // can get more details such as response.headers
//                }
//                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//            }
//        };
//
//        requestQueue.add(stringRequest);
//
//    }
//
//    private void startOpenPort() {
//        ComA.setPort("/dev/ttyS1");
//        ComA.setBaudRate("19200");
//        OpenComPort(ComA);
//    }
//
//    private void startDispense() {
//        new Thread(() -> {
//            try {
//                sendPortData(ComA, sentCommand());
//            } catch (Exception e) {
//                Log.e("DispensePopUpCashM5", "startDispense Error: " + e.getLocalizedMessage());
//                activity.runOnUiThread(() -> {
//                    Toast.makeText(activity, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                    initCustomDialogDispenseDismiss();
//                });
//            }
//        }).start();
//    }
//
//    private void initCustomDialogDispenseDismiss() {
//        if (customDialogDispense != null && customDialogDispense.isShowing()) {
//            customDialogDispense.dismiss();
//        }
//    }
//
//    private void CloseComPort(SerialHelper ComPort) {
//        if (ComPort != null) {
//            ComPort.stopSend();
//            ComPort.close();
//        }
//    }
//
//    //----------------------------------------------------打开串口
//    private void OpenComPort(SerialHelper ComPort) {
//        new Thread(() -> {
//            try {
//                ComPort.open();
//            } catch (Exception e) {
//                Log.e("DispensePopUpCashM5", "OpenComPort Error: " + e.getLocalizedMessage());
//                activity.runOnUiThread(() ->
//                        Toast.makeText(activity, "Error Open Com Port: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
//            }
//        }).start();
//    }
//
//    private void sendPortData(SerialHelper ComPort, String sOut) {
//        if (ComPort != null && ComPort.isOpen()) {
//            ComPort.sendHex(sOut);
//        }
//    }
//
//    private String sentCommand() {
//        final String itemNumber = arr_count.get(countItem).getproduct();
//        countDispense = 0;
//        return returnCommand(Integer.parseInt(itemNumber));
//    }
//
//    private String returnCommand(int productid) {
//        int random1 = randomNumber();
//        int random2 = randomNumber();
//        int random3 = randomNumber();
//        int random4 = randomNumber();
//        int random5 = randomNumber();
//        int random6 = randomNumber();
//        int random7 = randomNumber();
//        int random8 = randomNumber();
//        int random9 = randomNumber();
//        int random10 = randomNumber();
//        num++;
//
//        int total = 0;
//        String productIdStr1 = String.valueOf(productid);
//        if (productIdStr1.length() > 1 && productid > 15) {
//            productIdStr1 = productIdStr1.substring(0, 1);
//            if (Integer.valueOf(productIdStr1) > 1) {
//                total = (productid * Integer.valueOf(productIdStr1) - 1) + 6;
//            } else {
//                total = productid + 6;
//            }
//        } else {
//            total = productid;
//        }
//
//        int mainslave = 0;
//        String productIdStr2 = String.valueOf(productid);
//        if (productIdStr2.length() > 2) {
//            mainslave = 1;
//        }
//
//        int hexint = 0;
//        if (num > 9) {
//            if (num > 9 && num < 20) {
//                hexint = num + 6;
//            } else if (num > 19 && num < 30) {
//                hexint = num + 12;
//            } else if (num > 29 && num < 40) {
//                hexint = num + 18;
//            }
//        } else {
//            hexint = num;
//        }
//
//        byte[] fullcommand = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) mainslave), (byte) 0x00,
//                (byte) ((byte) hexint), (byte) 0x00, (byte) 0x81, (byte) 0x00, (byte) 0x11, (byte) 0x00,
//                (byte) 0x02,
//                (byte) 0x91, (byte) 0x00, (byte) ((byte) productid), (byte) 0x03,
//                (byte) ((byte) random1 + 18), (byte) ((byte) random2 + 18), (byte) ((byte) random3 + 18), (byte) ((byte) random4 + 18),
//                (byte) ((byte) random5 + 18), (byte) ((byte) random6 + 18), (byte) ((byte) random7 + 18), (byte) ((byte) random8 + 18),
//                (byte) ((byte) random9 + 18), (byte) ((byte) random10 + 18)};
//
//        int test = crc16_CCITT(fullcommand, fullcommand.length);
////        String hex = Integer.toString(test, 16);
//        String hex = intToHexStringWithLeadingZero(test);
//        String subtotal = hex.substring(2) + hex.substring(0, 2);
//        Log.d("test", "test-" + subtotal.toUpperCase());
//        commandStr = subtotal.toUpperCase();
//        String numStr = "", productIdStr = "";
//        if (num < 10) {
//            numStr = "0" + num;
//        } else {
//            numStr = String.valueOf(num);
//        }
//        if (productid < 10) {
//            productIdStr = "0" + productid;
//        } else {
//            productIdStr = String.valueOf(productid);
//        }
//        //34 32 34 35 37 39 37 34 39 30
//        //FEEF0000020081001100029100 0203343933313832383836373A54
//
//        String lengthnum = "";
//        if (productIdStr.length() > 2) {
//            lengthnum = "01";
//        } else {
//            lengthnum = "00";
//        }
//        String resultString = "FEEF0" + mainslave + "00" + numStr + "0081001100029100" + convertToHexadecimalWithLeadingZero(productid)
//                + "03" + random1 + random2 + random3
//                + random4 + random5 + random6
//                + random7 + random8 + random9
//                + random10;
//
//        return resultString + subtotal.toUpperCase();
//    }
//
//    private String intToHexStringWithLeadingZero(int value) {
//        // Convert int to hexadecimal string and add a leading zero if needed
//        String hexString = Integer.toString(value, 16);
//        if (hexString.length() == 2) {
//            hexString = "00" + hexString;
//        }
//        return (hexString.length() % 2 == 0) ? hexString : "0" + hexString;
//    }
//
//    private int randomNumber() {
//        Random random = new Random();
//        int randomNumber = random.nextInt(10) + 30;
//        return randomNumber;
//    }
//
//    private int crc16_CCITT(byte[] ptr, int len) {
//        int i = 0;
//        int crc = 0xFFFF;
//        int polyn = 0x1021;
//        int ptr_c = 0;
//
//        for (; len > 0; len--) {
//            int temp = ptr[ptr_c];
//            crc = crc ^ (temp << 8);
//            ptr_c++;
//            for (i = 0; i < 8; i++) {
//                if ((crc & 0x8000) == 0x8000) {
//                    crc = (crc << 1) ^ polyn;
//                } else {
//                    crc <<= 1;
//                }
//            }
//            crc &= 0xFFFF;
//        }
//        return (crc);
//    }
//
//    private void DispRecData(ComBean ComRecData) {
//        String response = MyFunc.ByteArrToHex(ComRecData.bRec);
//
//        //String productId = arr_count.get(countItem).getproduct();
//
//        String responseReplace = response;
//        responseReplace = responseReplace.replace(" ", "");
//        if (responseReplace.equalsIgnoreCase("FEEF0000020081800600010291001B1C") || countDispense > 0
//                || responseReplace.equalsIgnoreCase("FEEF0000030081800600010291005E73")) {
//            //command received and send back to machine
//            //do nothing
//            return;
//        }
//        String[] result = response.split(" ");
//        int indexToCheck = 25;
//        if (isValidIndex(result, indexToCheck)) {
//            for (int i = 0; i < arr_count.size(); i++) {
//                if (countItem == arr_count.get(arr_count.size() - 1).getcount()) {
//                    arr_count.get(i).setposition(1);
//                    break;
//                }
//            }
//
//            if (countItem == arr_count.get(arr_count.size() - 1).getcount()) {
//                //quit
//            } else {
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.removeCallbacksAndMessages(null);
//                        sendPortData(ComA, sentCommand());
//                    }
//                }, 4000);
//            }
//
//            arr_count.get(countItem).setposition(1);
//            countItem++;
//            countDispense++;
//            Log.d("print", "print-" + response);
//            String index14 = result[14];
//
//            if (index14.equalsIgnoreCase("02")) {
//                //have item drop
//                updateStatus(Integer.valueOf(arr_count.get(countItem - 1).getproduct()), "1", Integer.valueOf(index14));
//            } else {
//                updateStatus(Integer.valueOf(arr_count.get(countItem - 1).getproduct()), "2", Integer.valueOf(index14));
//            }
//        } else {
//            System.out.println("Index " + indexToCheck + " is out of bounds.");
//        }
//    }
//
//    private boolean isValidIndex(String[] array, int index) {
//        // Check if the index is within the valid range for the array
//        return index >= 0 && index < array.length;
//    }
//
//    abstract class RunableEx implements Runnable {
//        String data;
//
//        public RunableEx(String data) {
//            this.data = data;
//        }
//    }
//
//    private class SerialControl extends SerialHelper {
//        public SerialControl() {
//        }
//
//        @Override
//        protected void onDataReceived(final ComBean ComRecData) {
//            dataDisplayThread.addData(ComRecData);
//        }
//    }
//
//    private class DataDisplayThread extends Thread {
//        private final BlockingQueue<ComBean> displayQueue = new LinkedBlockingQueue<>();
//        private final WeakReference<Activity> activityRef;
//
//        public DataDisplayThread(Activity activity) {
//            this.activityRef = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                try {
//                    ComBean data = displayQueue.take(); // Blocks until data is available
//                    Activity activity = activityRef.get();
//                    if (activity != null) {
//                        activity.runOnUiThread(() -> displayData(data));
//                    }
//                } catch (InterruptedException e) {
//                    // Thread interrupted, exit gracefully
//                    break;
//                } catch (Exception e) {
//                    Log.e("DataDisplayThread", "Error processing data", e);
//                }
//            }
//        }
//
//        public void addData(ComBean data) {
//            try {
//                displayQueue.put(data);
//            } catch (InterruptedException e) {
//                // Handle interruption
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        private void displayData(ComBean data) {
//            DispRecData(data);
//        }
//    }
//}