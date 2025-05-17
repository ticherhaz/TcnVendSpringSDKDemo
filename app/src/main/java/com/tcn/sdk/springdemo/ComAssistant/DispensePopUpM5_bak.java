//package com.tcn.sdk.springdemo.ComAssistant;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.serialport.SerialPort;
//import android.util.Log;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.android.volley.AuthFailureError;
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
//import com.tcn.sdk.springdemo.R;
//import com.tcn.sdk.springdemo.TypeProfuctActivity;
//import com.tcn.sdk.springdemo.Utilities.SharedPref;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.security.InvalidParameterException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import java.util.Random;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class DispensePopUpM5_bak {
//    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
//    private final int count1 = 0;
//    private final int queueNow = 0;
//    private final int posall = -1;
//    private final String TAG = "DispensePopUp";
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
//    DispQueueThread DispQueue;
//    SerialControl ComA;
//    String allstatuses = "";
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
//    private TypeProfuctActivity activity;
//    private List<CartListModel> cartListModelList;
//    private boolean checkPopUp = false;
//    private RequestQueue requestQueue;
//    private int countItem = 0;
//    private int num = 1;
//
//    public void DispensePopUp(TypeProfuctActivity activity, UserObj userobj, String paystatus, String payid, RequestQueue requestQueue) {
//        RollingLogger.i(TAG, "DispesePopUp start");
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
//        DispQueue = new DispQueueThread();
//        DispQueue.start();
//        startOpenPort();
//
//        activity.getCountDownHandler().removeCallbacksAndMessages(null);
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
//        activity.clearCustomDialogDispense();
//        customDialogDispense = activity.getCustomDialogDispense("dispense");
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
/// /            connectSerialPort();
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
//            setvalues();
//            setupfordispense();
//            startDispense();
//        } catch (Exception ex) {
//            RollingLogger.i(TAG, "Dispense oncreate error - " + ex);
//        }
//    }
//
//    private void setupfordispense() {
//        RollingLogger.i(TAG, "setupfordispense");
//        threadintrupt = true;
//        isuserpaying = true;
//        adapter = new CartListAdapter(activity, cartListModels);
//        list.setAdapter(adapter);
//    }
//
//    private void setvalues() {
//        TextView totl = customDialogDispense.findViewById(R.id.pricetext);
//        totl.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
//    }
//
//    private void updateStatus(int product, String pos, int status) {
//        String productcode = String.valueOf(product);
//        boolean checkhere = false;
//
//        boolean check = false;
//        for (int j = 0; j < arr_count.size(); j++) {
//            System.out.println("loggings-count-" + arr_count.get(j).getposition() + "-product-" + arr_count.get(j).getproduct());
//            if (arr_count.get(j).getproduct().equalsIgnoreCase(productcode) && arr_count.get(j).getposition() != 1) {
//                check = true;
//            }
//        }
//
//        if (!check) {
//            for (int i = 0; i < cartListModels.size(); i++) {
//                System.out.println("loggings-countmodel-" + cartListModels.get(i).getSerial_port() + "-productcode-" + productcode + "-pos-" + cartListModels.get(i).getPosition());
//                if (cartListModels.get(i).getSerial_port().equalsIgnoreCase(productcode)) {
//                    cartListModels.get(i).setPostion(pos);
//                    checkhere = true;
//
//                    allstatuses += product + "x" + cartListModels.get(i).getItemqty() + ":" + status + ",";
//
//                }
//            }
//        }
//
//        String text = "";
//        if (checkhere) {
//            handler.post(new RunableEx(text) {
//                public void run() {
//                    adapter.update(cartListModels);
//
//                    boolean checkc = false;
//                    for (int i = 0; i < arr_count.size(); i++) {
//                        if (arr_count.get(i).getposition() == 0) {
//                            checkc = true;
//                            break;
//                        }
//                    }
//                    if (!checkc) {
//                        CloseComPort(ComA);
//
//                        Handler handler13 = new Handler();
//                        handler13.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
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
//                                    if (customDialogDispense != null) {
//                                        if (customDialogDispense.isShowing())
//                                            customDialogDispense.dismiss();
//                                    }
//
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
//                            }
//                        }, 15000);
//                        //if(LDispense.equalsIgnoreCase("true")) {
//                        RollingLogger.i(TAG, "End Dispensing Activity");
//                        //}
//
/// /                        Temporary Disable
/// /                        RollingLogger.i(TAG, "api call uptqty");
/// /                        updateprodqty();
/// /                        if(isloggedin) {
/// /                            updatemobiletransactiondb();
/// /                            newpoints  = points - chargingprice;
/// /                            updatepointsmdb();
/// /                        }
/// /                        updatetransactiondb(allstatuses);
//
//                        sdthankyou = activity.getThankYouDispense().setTitleText("Thank you.")
//                                .setContentText("Your order is dispensed please get ready to pickup.");
//                        sdthankyou.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                handler13.removeCallbacksAndMessages(null);
//                                handler.removeCallbacksAndMessages(null);
//                                if (sdthankyou != null) {
//                                    if (sdthankyou.isShowing()) {
//                                        sdthankyou.dismiss();
//                                        RollingLogger.i(TAG, "Thank you dialog dismiss");
//                                    }
//                                }
//                                if (customDialogDispense != null) {
//                                    if (customDialogDispense.isShowing()) {
//                                        customDialogDispense.dismiss();
//                                    }
//                                }
//                                if (!checkPopUp) {
//                                    RollingLogger.i(TAG, "jump main activity");
//                                    freeMemory();
//                                    Intent intent = new Intent(activity, MainActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    activity.startActivity(intent);
//                                    activity.finish();
//                                    if (LDispense.equalsIgnoreCase("true")) {
//                                        RollingLogger.i(TAG, "Dispensing Activity Closed");
//                                    }
//                                }
//                            }
//                        });
//                        try {
//                            if (!activity.isFinishing()) {
//                                sdthankyou.show();
//                            }
//                        } catch (Exception ex) {
//                        }
//                    }
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
//
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
//        sendPortData(ComA, sentCommand());
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
//        try {
//            ComPort.open();
//        } catch (SecurityException e) {
////            ShowMessage("打开串口失败:没有串口读/写权限!");
//        } catch (IOException e) {
////            ShowMessage("打开串口失败:未知错误!");
//        } catch (InvalidParameterException e) {
////            ShowMessage("打开串口失败:参数错误!");
//        }
//    }
//
//    private void sendPortData(SerialHelper ComPort, String sOut) {
//        if (ComPort != null && ComPort.isOpen()) {
//            ComPort.sendHex(sOut);
//        }
//    }
//
//    private String sentCommand() {
//        String itemno = cartListModelList.get(countItem).getItemnumber();
////        if(itemno.length()==3) {
////            itemno = itemno.substring(1, itemno.length());
////        }
//
////        String finalString = stringToHex(Integer.parseInt(itemno))+stringToHex2(Integer.parseInt(itemno));
////        Log.d("test","test1-"+itemno+"-"+finalString);
////        String full = "00FF"+finalString+"AA55";
//        String commandSend = returnCommand(Integer.valueOf(itemno));
//        return commandSend;
//    }
//
//    private String stringToHex(int intValue) {
//        String str = Integer.toHexString(intValue);
//        if (str.length() == 1) {
//            str = "0" + str;
//        }
//        str = str.toUpperCase();
//        return str;
//    }
//
//    private String stringToHex2(int intValue) {
//        String[] arr = {"F", "E", "D", "C", "B", "A"};
//        String[] arr2 = {"F", "E", "D", "C", "B", "A", "9", "8", "7", "6", "5",
//                "4", "3", "2", "1", "0"};
//        int getValue = 0;
//        if (intValue > 15) {
//            getValue = intValue / 16;
//        }
//        String getString = arr[getValue];
//        int reminder = 0;
//        boolean check = false;
//        do {
//            if (!check) {
//                reminder = intValue - 16;
//                check = true;
//            } else {
//                reminder = reminder - 16;
//            }
//        } while (reminder > 16);
//        String reminderStr;
//        if (intValue == 16 || intValue == 32 || intValue == 48) {
//            reminderStr = arr2[0];
//        } else {
//            if (reminder > 0) {
//                reminderStr = arr2[reminder];
//            } else {
//                reminderStr = arr2[intValue];
//            }
//        }
//        String finalString = getString + reminderStr;
//        return finalString;
//    }
//
//    private boolean isValidIndex(String[] array, int index) {
//        // Check if the index is within the valid range for the array
//        return index >= 0 && index < array.length;
//    }
//
//    private void DispRecData(ComBean ComRecData) {
//        String response = MyFunc.ByteArrToHex(ComRecData.bRec);
//
//        String responseReplace = response;
//        responseReplace = responseReplace.replace(" ", "");
//        if (responseReplace.equalsIgnoreCase("FEEF00000200810011000491000602383636393434363039376D40")) {
//            //command received and send back to machine
//            //do nothing
//            return;
//        }
//
//
//        String[] result = response.split(" ");
//        int indexToCheck = 14;
//        if (isValidIndex(result, indexToCheck)) {
//            countItem++;
//            if (countItem == cartListModelList.size()) {
//                //quit
//            } else {
//                sentCommand();
//            }
//
//            String index14 = result[14];
//            if (response.equalsIgnoreCase("02")) {
//                //have item drop
//                updateStatus(Integer.valueOf(cartListModelList.get(countItem - 1).getItemnumber()), "1", 3);
//            } else {
//                updateStatus(Integer.valueOf(cartListModelList.get(countItem - 1).getItemnumber()), "2", 3);
//            }
//        } else {
//            System.out.println("Index " + indexToCheck + " is out of bounds.");
//        }
//
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
//        byte[] fullcommand = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) 0x00, (byte) 0x00,
//                (byte) ((byte) num), (byte) 0x00, (byte) 0x81, (byte) 0x00, (byte) 0x11, (byte) 0x00,
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
//
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
//        String resultString = "FEEF0000" + numStr + "0081001100029100" + productIdStr
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
//            DispQueue.AddQueue(ComRecData);
//        }
//    }
//
//    private class DispQueueThread extends Thread {
//        private final Queue<ComBean> QueueList = new LinkedList<ComBean>();
//
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                final ComBean ComData;
//                while ((ComData = QueueList.poll()) != null) {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            DispRecData(ComData);
//                        }
//                    });
//                    try {
//                        Thread.sleep(100);//显示性能高的话，可以把此数值调小。
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//
//        public synchronized void AddQueue(ComBean ComData) {
//            QueueList.add(ComData);
//        }
//    }
//
//}
