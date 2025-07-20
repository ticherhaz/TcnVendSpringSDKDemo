//package com.tcn.sdk.springdemo.Dispense;
//
//import android.app.Activity;
//import android.os.Handler;
//import android.os.Message;
//import android.serialport.SerialPort;
//import android.util.Log;
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
//import com.tcn.sdk.springdemo.Model.TransactionModel;
//import com.tcn.sdk.springdemo.Utilities.HexDataHelper;
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
//public class DispenseDirect {
//    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
//    private final String TAG = "DispensePopUp";
//    private final String LDispense = "";
//    private final int mInterval = 3000;
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
//    String allstatuses = "";
//    private List<countObj> arr_count;
//    private int count1 = 0;
//    private int queueNow = 0, posall = -1;
//    private String num1 = "";
//    private String Fid1 = "";
//    private String CreatedBy = "";
//    private String Pid = "";
//    private int temp = 0;
//    private int Dispid = 0;
//    private Handler mHandler;
//    private boolean checkPosAll = false;
//    private Activity activity;
//    private Double price;
//
//    public void DispenseDirect(Activity activity1, String num, String Fid, Double price1, String CreatedBy1, int temp1, String Pid1,
//                               int Dispid1) {
//        activity = activity1;
//        Fid1 = Fid;
//        price = price1;
//        CreatedBy = CreatedBy1;
//        temp = temp1;
//        Pid = Pid1;
//        Dispid = Dispid1;
//        try {
//            connectSerialPort();
//            arr_count = new ArrayList<>();
//
//            if (temp == 2) {
//                if (num.length() == 1) {
//                    num = "10" + num;
//                } else if (num.length() == 2) {
//                    num = "1" + num;
//                }
//            } else {
//                if (num.length() == 1) {
//                    num = "00" + num;
//                } else if (num.length() == 2) {
//                    num = "0" + num;
//                }
//            }
//            num1 = num;
//            countObj obj = new countObj();
//            obj.setproduct(num);
//            obj.setcount(0);
//            obj.setposition(0);
//            arr_count.add(obj);
//
//            setupfordispense();
//        } catch (Exception ex) {
//            RollingLogger.i(TAG, "Dispense oncreate error - " + ex);
//        }
//    }
//
//    private void setupfordispense() {
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
/// /            handler11.removeCallbacksAndMessages(null);
//
//            stopRepeatingTask();
//            //writeCmd(ackBytes);
//        } else if (0x04 == (short) (cmd[2] & 0xff)) {
//            stopRepeatingTask();
//            writeCmd(ackBytes);
//            //update status here
//            if (0x01 == (short) (cmd[5] & 0xff)) {
//                //dispensing
/// /                handler3.removeCallbacksAndMessages(null);
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
//        boolean checkhere = false;
//
//        String text = "";
//        if (!checkhere) {
//            handler.post(new RunableEx(text) {
//                public void run() {
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
//                        updateprodqty();
//                        //updatetransactiondb(allstatuses);
//                    }
//                }
//            });
//        }
//    }
//
//    private void updateprodqty() {
//        RequestQueue requestQueue2 = Volley.newRequestQueue(activity);
//        JSONObject jsonParam = null;
//        String url = "https://vendingappapi.azurewebsites.net/Api/Transaction?Rtransid=" + Dispid;
//
//        final String requestBody = String.valueOf(1);
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
//    }
//
//    public void updatetransactiondb(String status) {
//        try {
//            Date currentTime = Calendar.getInstance().getTime();
//
//
//            RequestQueue requestQueue = Volley.newRequestQueue(activity);
//            String tag_json_obj = "json_obj_req";
//            JSONObject jsonParam = null;
//            String url = "https://vendingappapi.azurewebsites.net/Api/Transaction";
//
//            TransactionModel transactionModel = new TransactionModel();
//            transactionModel.setAmount(price);
//            transactionModel.setmDate(currentTime);
//            transactionModel.setUserID(0);
//            transactionModel.setFranchiseID(Fid1);
//            transactionModel.setMachineID(Fid1);
//            transactionModel.setProductsIdes(Pid);
//            transactionModel.setPaymentType("remotely by " + CreatedBy);
//            transactionModel.setPaymentMethod("remotely by " + CreatedBy);
//            transactionModel.setFreePoints("");
//            transactionModel.setPromocode(status);
//            transactionModel.setPromoamount("");
//            transactionModel.setPaymentStatus("Success");
//            transactionModel.setPaymentID("");
//            transactionModel.setRemarks("");
//
/// /            System.out.println("Trans Voucher id =" + vids);
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
