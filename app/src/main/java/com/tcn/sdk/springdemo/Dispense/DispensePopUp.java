//package com.tcn.sdk.springdemo.Dispense;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.serialport.SerialPort;
//import android.util.Log;
//import android.view.Window;
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
//import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
//import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
//import com.tcn.sdk.springdemo.DBUtils.configdata;
//import com.tcn.sdk.springdemo.MainActivity;
//import com.tcn.sdk.springdemo.Model.CartListModel;
//import com.tcn.sdk.springdemo.Model.CongifModel;
//import com.tcn.sdk.springdemo.Model.Pointsend;
//import com.tcn.sdk.springdemo.Model.TransactionModel;
//import com.tcn.sdk.springdemo.R;
//import com.tcn.sdk.springdemo.Utilities.HexDataHelper;
//import com.tcn.sdk.springdemo.Utilities.SharedPref;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class DispensePopUp {
//    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
//    private final String TAG = "DispensePopUp";
//    private final int mInterval = 3000;
//    Thread mThread;
//    SerialPort serialPort;
//    String devPath;
//    int baudrate;
//    int no = 0;
//    byte[] ackBytes = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x42, 0x00, 0x43};
//    Boolean isloggedin;
//    byte[] ackBytesTest = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x41, 0x00, 0x40};
//    Queue<byte[]> queue = new LinkedList<byte[]>();
//    int userid;
//    int userstatus;
//    int pid;
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };
//    double discountamt = 0.00;
//    double chargingprice;
//    double points;
//    double newpoints;
//    String fid;
//    String mid;
//    String productsids = "";
//    String mtd;
//    String vids = "";
//    String paytype = "";
//    String promname = "";
//    String payid;
//    String paystatus;
//    String remakrs;
//    PorductDBHandler porductDBHandler;
//    CartDBHandler db;
//    configdata dbconfig;
//    Boolean isuserpaying = false;
//    Boolean threadintrupt = false;
//    double rrp = 0.00;
//    String allstatuses = "";
//    private List<CartListModel> cartListModels;
//    private List<CongifModel> congifModels;
//    private SweetAlertDialog sdthankyou;
//    private CartListAdapter adapter;
//    private List<countObj> arr_count;
//    private ListView list;
//    private int count1 = 0;
//    private int queueNow = 0, posall = -1;
//    private String LDispense = "";
//    private Handler mHandler;
//    private boolean checkPosAll = false;
//    private Dialog customDialogDispense;
//    private Activity activity;
//    private List<CartListModel> cartListModelList;
//
//    public void DispensePopUp(Activity activity1, String mval1, boolean isloggedin1, String paytype1,
//                              double totalamt, String payid1, double points1, int userid1, int pid1,
//                              String exdate1, int ustatus1, String paystatus1, String remarks1,
//                              List<CartListModel> cartListModelList) {
//        RollingLogger.i(TAG, "DispesePopUp start");
//
//        mtd = mval1;
//        activity = activity1;
//        this.cartListModelList = cartListModelList;
//        isloggedin = isloggedin1;
//        paytype = paytype1;
//        chargingprice = totalamt;
//        payid = payid1;
//        points = points1;
//        userid = userid1;
//        pid = pid1;
//        paystatus = paystatus1;
//
//        if (isloggedin) {
//            points = points1;
//            userid = userid1;
//            userstatus = ustatus1;
//            pid = pid1;
//            newpoints = points1;
//        }
//
//        customDialogDispense = new Dialog(activity);
//        customDialogDispense.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        customDialogDispense.setContentView(R.layout.activity_dispense_popup);
//        customDialogDispense.setCancelable(true);
//        customDialogDispense.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        if (!activity.isFinishing())
//            customDialogDispense.show();
//
//        SharedPref.init(activity);
//        LDispense = SharedPref.read(SharedPref.Ldispense, "");
//
//        RollingLogger.i(TAG, "Start Dispensing Activity");
//
//        list = customDialogDispense.findViewById(R.id.mrecyclr);
//
//        try {
//            connectSerialPort();
//            db = new CartDBHandler(activity);
//            dbconfig = new configdata(activity);
//            congifModels = new ArrayList<CongifModel>();
//
//            congifModels = dbconfig.getAllItems();
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
//        } catch (Exception ex) {
//            RollingLogger.i(TAG, "Dispense oncreate error - " + ex);
//        }
//    }
//
//    private void setupfordispense() {
//        threadintrupt = true;
//        isuserpaying = true;
/// /        ConstraintLayout mylayout = customDialogDispense.findViewById(R.id.mylay);
//
//        adapter = new CartListAdapter(activity, cartListModels);
//        list.setAdapter(adapter);
////        mylayout.setVisibility(View.VISIBLE);
//
//        String text = "";
//        handler.post(new RunableEx(text) {
//            public void run() {
//                runQueue();
//            }
//        });
//    }
//
//    void startRepeatingTask() {
//        count1 = 0;
//        mStatusChecker.run();
//    }
//
//    private void runQueue() {
//        mHandler = new Handler();
//        checkPosAll = false;
//        startRepeatingTask();
//    }
//
//    void stopRepeatingTask() {
//        mHandler.removeCallbacks(mStatusChecker);
//    }    Runnable mStatusChecker = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                if (count1 < 2) {
//                    if (!checkPosAll) {
//                        posall = 0;
//                    } else {
//                        posall = arr_count.get(queueNow).getcount();
//                    }
//                    int hdhInt = Integer.parseInt(arr_count.get(queueNow).getproduct());
//                    System.out.println("loggings-check-sent-product-" + hdhInt);
//                    short[] hdhbyte = HexDataHelper.Int2Short16_2(hdhInt);
//                    if (hdhbyte.length == 1) {
//                        short temp = hdhbyte[0];
//                        hdhbyte = new short[2];
//                        hdhbyte[0] = 0;
//                        hdhbyte[1] = temp;
//                    }
//                    byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
//                    data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
//                    queue.add(data);
//                    count1++;
//                } else {
//                    stopRepeatingTask();
//                }
//            } finally {
//                mHandler.postDelayed(mStatusChecker, mInterval);
//            }
//        }
//    };
//
//    private void setvalues() {
//        TextView totl = customDialogDispense.findViewById(R.id.pricetext);
//        totl.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
//
//    }
//
//    private void connectSerialPort() {
//        if (null == serialPort) {
//            bindSerialPort();
//        }
//    }
//
//    public int getNextNo() {
//        no++;
//        if (no >= 255) {
//            no = 0;
//        }
//        return no;
//    }
//
//    private void bindSerialPort() {
//        devPath = "/dev/ttyS1";
//        baudrate = 57600;
//        mThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    File serialFile = new File(devPath);
//                    if (!serialFile.exists() || baudrate == -1) {
//                        return;
//                    }
//
//                    try {
//                        serialPort = new SerialPort(serialFile, baudrate, 0);
//                        if (LDispense.equalsIgnoreCase("true")) {
//                            //RollingLogger.i(TAG, "Serial port started");
//                        }
//                        readSerialPortData();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                } finally {
//                    if (null != serialPort) {
//                        try {
//                            if (LDispense.equalsIgnoreCase("true")) {
//                                //RollingLogger.i(TAG, "Serial port closed");
//                            }
//                            serialPort.close();
//                            serialPort = null;
//                        } catch (Exception e) {
//                            Log.d("test", e.toString());
//                        }
//                    }
//
//                }
//            }
//        });
//        mThread.start();
//    }
//
//    private void readSerialPortData() {
//        while (true) {
//            try {
//                if (null == serialPort) {
//                    Thread.sleep(1000);
//                    continue;
//                }
//                int available = serialPort.getInputStream().available();
//                if (0 == available) {
//                    Thread.sleep(10);
//                    continue;
//                }
//
//                byte[] data = readBytes(serialPort.getInputStream(), available);
//                mBuffer.write(data);
//                while (true) {
//                    byte[] bytes = mBuffer.toByteArray();
//                    int start = 0;
//                    int cmdCount = 0;
//                    boolean shuldBreak = false;
//                    for (; start <= bytes.length - 5; start++) {
//                        if ((short) (bytes[start] & 0xff) == 0xFA && (short) (bytes[start + 1] & 0xff) == 0xFB) {
//                            try {
//                                int len = bytes[start + 3];
//                                byte[] cmd = new byte[len + 5];
//                                System.arraycopy(bytes, start, cmd, 0, cmd.length);
//                                cmdCount++;
//                                proccessCmd(cmd);
//
//
//                                //计算还有多少剩余字节要解析，没有的跳出等待接收新的字节，有则继续处理
//                                int remain = bytes.length - start - cmd.length;
//                                if (0 == remain) {
//                                    shuldBreak = true;
//                                    mBuffer.reset();
//                                    break;
//                                }
//                                byte[] buffer2 = new byte[remain];
//                                System.arraycopy(bytes, start + cmd.length, buffer2, 0, buffer2.length);
//                                mBuffer.reset();
//                                mBuffer.write(buffer2);
//                            } catch (Exception e) {
//                                shuldBreak = true;
//                                //因数据包不全，导致越界异常，直接跳出即可
//                            }
//                            break;
//                        }
//                    }
//                    if (0 == cmdCount || shuldBreak) {
//                        break;
//                    }
//
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public byte[] readBytes(InputStream stream, int length) throws IOException {
//        byte[] buffer = new byte[length];
//
//        int total = 0;
//
//        while (total < length) {
//            int count = stream.read(buffer, total, length - total);
//            if (count == -1) {
//                break;
//            }
//            total += count;
//        }
//
//        if (total != length) {
//            throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", total, length));
//        }
//
//        return buffer;
//    }
//
//    public void writeCmd(byte[] cmd) {
//        if (LDispense.equalsIgnoreCase("true")) {
//            //RollingLogger.i(TAG, "loggings-check-sent-" + HexDataHelper.hex2String(cmd));
//        }
//        System.out.println("loggings-check-sent-" + HexDataHelper.hex2String(cmd));
//        try {
//            serialPort.getOutputStream().write(cmd);
//            serialPort.getOutputStream().flush();
//        } catch (Exception e) {
//            Log.d("test2", e.toString());
//        }
//    }
//
//    public void proccessCmd(byte[] cmd) {
//        if (LDispense.equalsIgnoreCase("true")) {
//            //RollingLogger.i(TAG, "loggings-check-response-" + HexDataHelper.hex2String(cmd));
//        }
//        System.out.println("loggings-check-response-" + HexDataHelper.hex2String(cmd));
//
//        if (0x41 == (short) (cmd[2] & 0xff)) {
//            //收到POLL包
//
//            if (queue.size() == 0) {
//                writeCmd(ackBytes);
//            } else {
//                writeCmd(queue.poll());
//            }
//        } else if (0x42 == (short) (cmd[2] & 0xff)) {
//            // 收到ACK
//            Log.d("TAG:", "ACK Received");
////            handler11.removeCallbacksAndMessages(null);
//
//            stopRepeatingTask();
//            //writeCmd(ackBytes);
//        } else if (0x04 == (short) (cmd[2] & 0xff)) {
//            stopRepeatingTask();
//            writeCmd(ackBytes);
//            //update status here
//            if (0x01 == (short) (cmd[5] & 0xff)) {
//                //dispensing
////                handler3.removeCallbacksAndMessages(null);
//
//                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
//                if (LDispense.equalsIgnoreCase("true")) {
//                    //RollingLogger.i(TAG, "loggings-check-dispensing-product-" + String.valueOf(product));
//                }
//
//            } else if (0x02 == (short) (cmd[5] & 0xff)) {
//                //dispensed
//                queueNow = queueNow + 1;
//
//                for (int i = 0; i < arr_count.size(); i++) {
//                    if (posall == arr_count.get(i).getcount()) {
//                        arr_count.get(i).setposition(1);
//                        break;
//                    }
//                }
//
//                if (queueNow == arr_count.size()) {
//                    //quit
//                } else {
//                    checkPosAll = true;
//                    //mHandler = new Handler();
//                    startRepeatingTask();
//                }
//
//                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
//                if (LDispense.equalsIgnoreCase("true")) {
//                    //RollingLogger.i(TAG, "loggings-check-dispensed-product-" + String.valueOf(product));
//                }
//                int mstatus = Integer.parseInt(String.format("%02X", cmd[5]), 16);
//                updateStatus(product, "1", mstatus);
//            } else {
//                //dispensed with no dropsensor
//                queueNow = queueNow + 1;
//
//                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
//                for (int i = 0; i < arr_count.size(); i++) {
//                    if (posall == arr_count.get(i).getcount()) {
//                        arr_count.get(i).setposition(1);
//                        break;
//                    }
//                }
//
//                if (queueNow == arr_count.size()) {
//                    //quit
//                } else {
//                    checkPosAll = true;
//                    startRepeatingTask();
//                }
//
//                int mstatus = Integer.parseInt(String.format("%02X", cmd[5]), 16);
//                if (LDispense.equalsIgnoreCase("true")) {
//                    //RollingLogger.i(TAG, "loggings-check-dispensed-product-" + String.valueOf(product));
//                }
//                updateStatus(product, "2", mstatus);
//            }
//        }
//        //ACK
//        else if (0x52 == (short) (cmd[2] & 0xff)) {
//            writeCmd(ackBytes);
//        } else {
//            writeCmd(ackBytes);
//        }
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
//                        serialPort.close();
//                        if (LDispense.equalsIgnoreCase("true")) {
//                            //RollingLogger.i(TAG, "Serial port closed");
//                        }
//                        serialPort = null;
//
//                        Handler handler13 = new Handler();
//                        handler13.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
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
//                        }, 60000);
//                        //if(LDispense.equalsIgnoreCase("true")) {
//                        RollingLogger.i(TAG, "End Dispensing Activity");
//                        //}
//
//                        RollingLogger.i(TAG, "api call uptqty");
//                        updateprodqty();
//                        if (isloggedin) {
//                            updatemobiletransactiondb();
//                            newpoints = points - chargingprice;
//                            updatepointsmdb();
//                        }
//                        updatetransactiondb(allstatuses);
//
////                        w30 = new wait30();
////                        w30.start();
//
//                        sdthankyou = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
//                                .setTitleText("Thank you.")
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
//                                RollingLogger.i(TAG, "jump main activity");
//                                freeMemory();
//                                Intent intent = new Intent(activity, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                activity.startActivity(intent);
//                                activity.finish();
//                                if (LDispense.equalsIgnoreCase("true")) {
//                                    RollingLogger.i(TAG, "Dispensing Activity Closed");
//                                }
//                            }
//                        });
//                        try {
//                            sdthankyou.show();
//                        } catch (Exception ex) {
//                            Log.d("test2", ex.toString());
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
//            RequestQueue requestQueue = Volley.newRequestQueue(activity);
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
//
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
//            RequestQueue requestQueue2 = Volley.newRequestQueue(activity);
//            JSONObject jsonParam = null;
//            String url = "https://vendingappapi.azurewebsites.net/api/Product/" + cartListModels.get(i).getProdid();
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
//            requestQueue2.add(stringRequest);
//
//        }
//
//    }
//
//    private void updatemobiletransactiondb() {
//
//        Date currentTime = Calendar.getInstance().getTime();
//
//        RequestQueue requestQueue1 = Volley.newRequestQueue(activity);
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
//        requestQueue1.add(stringRequest);
//
//
//    }
//
//    private void updatepointsmdb() {
//
//        // Date date = null;
//        Date date = Calendar.getInstance().getTime();
//
//        RequestQueue requestQueue2 = Volley.newRequestQueue(activity);
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
//        requestQueue2.add(stringRequest);
//
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
//
//
//
//}
