//package com.tcn.sdk.springdemo;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.android.volley.RequestQueue;
//import com.ftdi.j2xx.D2xxManager;
//import com.ftdi.j2xx.FT_Device;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.tcn.sdk.springdemo.ComAssistant.MyFunc;
//import com.tcn.sdk.springdemo.ComAssistant.SerialHelper;
//import com.tcn.sdk.springdemo.Dispense.AppLogger;
//import com.tcn.sdk.springdemo.Model.CartListModel;
//import com.tcn.sdk.springdemo.Model.CongifModel;
//import com.tcn.sdk.springdemo.Model.UserObj;
//import com.tcn.sdk.springdemo.Note.ITLDeviceComPopUp;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import java.io.IOException;
//import java.security.InvalidParameterException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//import device.itl.sspcoms.SSPDevice;
//import device.itl.sspcoms.SSPUpdate;
//
//public class TokenActivity extends AppCompatActivity {
//
//
//    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
//    //static NumberPicker numPayAmount;
//    private static final D2xxManager ftD2xx = null;
//    private static final FT_Device ftDev = null;
//    private static final SSPDevice sspDevice = null;
//    private static final TokenActivity instance = null;
//    private static final String m_DeviceCountry = null;
//    private static final int count = 0;
//    private static final int total = 0;
//    private static final int stackint = 0;
//    private static final int storeint = 0;
//    private static final int refundint = 0;
//    private static final int dispensedint = 0;
//    private static final CountDownTimer cTimer = null;
//    private static final String TAG = "CashNoteActivity";
//    private static final String fid = "";
//    private static final String mid = "";
//    private static final boolean refundboo = false;
//    static FloatingActionButton fab;
//    static LinearLayout bvDisplay;
//    static TokenActivity mainActivity;
//    static ListView listChannels;
//    static ListView listEvents;
//    static Button bttnAccept;
//    static Button bttnReject;
//    static Switch swEscrow;
//    static Button bttnPay;
//    static Button bttnEmpty;
//    static TextView txtFirmware;
//    static TextView txtDevice;
//    static TextView txtDataset;
//    static TextView txtSerial;
//    static LinearLayout lPayoutControl;
//    static TextView txtPayoutStatus;
//    static TextView txtConnect;
//    static ProgressBar prgConnect;
//    static Button bttnPayNext;
//    static Button bttnStackNext;
//    static ArrayList<HashMap<String, String>> list;
//    static String[] pickerValues;
//    static ProgressDialog progress;
//    static List<String> channelValues;
//    static String[] eventValues;
//    static ArrayAdapter<String> adapterChannels;
//    static ArrayAdapter<String> adapterEvents;
//    static String productsids = "", noteone = "", notefive = "", noteten = "", notetwenty = "", notefifty = "", notehundred = "";
//    private static MenuItem downloadMenuItem;
//    private static MenuItem storedBillMenuItem;
//    private static TextView scantext, pricetext, balancetext, amounttext, outputtext; //added outputtext
//    private static UserObj userObj;
//    private static TokenActivity activity;
//    private static Handler handler;
//    private static SweetAlertDialog sweetAlertDialog, sweetAlertDialogOK, sweetAlertDialogBack, sweetAlertDialogCancel;
//    private static ITLDeviceComPopUp deviceCom;
//    private static String price;
//    private static RequestQueue requestQueue;
//    private static List<CongifModel> congifModels;
//    private static List<CartListModel> cartListModelList;
//    private final SSPUpdate sspUpdate = null;
//    private final int num = 0;
//    private final int isMain = 0;
//    SerialControl ComA;
//    DispQueueThread DispQueue;
//    private Intent intent;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_token);
//
//        requestQueue = null;
//        congifModels = new ArrayList<>();
//        cartListModelList = new ArrayList<>();
//        productsids = "";
//        RollingLogger.i(TAG, "TokenActivity start");
//        activity = this;
//        Button backbtn = findViewById(R.id.backbtn);
//        backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RollingLogger.i(TAG, "back button clicked");
//                if (sweetAlertDialog != null) {
//                    if (sweetAlertDialog.isShowing()) {
//                        sweetAlertDialog.dismiss();
//                    }
//                }
//                if (sweetAlertDialogBack != null) {
//                    if (sweetAlertDialogBack.isShowing()) {
//                        sweetAlertDialogBack.dismiss();
//                    }
//                }
//                finish();
//            }
//        });
//        ComA = new SerialControl();
//        DispQueue = new DispQueueThread();
//        DispQueue.start();
//        startOpenPort();
//    }
//
//    private void startOpenPort() {
//        ComA.setPort("/dev/ttyS1");
//        ComA.setBaudRate("19200");
//        OpenComPort(ComA);
//    }
//
//    public SerialControl returnPort() {
//        return ComA;
//    }
//
//    public void CloseComPort(SerialHelper ComPort) {
//        if (ComPort != null) {
//            ComPort.stopSend();
//            ComPort.close();
//        }
//    }
//
//    private void startCommand(String commandSend) {
//        sendPortData(ComA, sentCommand(commandSend));
//    }
//
//    private String sentCommand(String commandSend) {
//        commandSend = commandSend.replace(" ", "");
//        return commandSend;
//    }
//
//    private void sendPortData(SerialHelper ComPort, String sOut) {
//        if (ComPort != null && ComPort.isOpen()) {
//            ComPort.sendHex(sOut);
//        }
//    }
//
//    private void OpenComPort(SerialHelper ComPort) {
//        try {
//            ComPort.open();
//        } catch (SecurityException e) {
/// /            ShowMessage("打开串口失败:没有串口读/写权限!");
//        } catch (IOException e) {
/// /            ShowMessage("打开串口失败:未知错误!");
//        } catch (InvalidParameterException e) {
/// /            ShowMessage("打开串口失败:参数错误!");
//        }
//        byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) 0x00, (byte) 0x00,
//                (byte) ((byte) num), (byte) 0x00, (byte) 0x82, (byte) 0x00,
//                (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0xA2, (byte) 0x00};
//        int crcResult = crc16_CCITT(hexString, hexString.length);
//        String results = String.format("0x%04X", crcResult);
//
//        startCommand("FE EF 00 00 0" + num + " 00 82 00 05 00 03 A2 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//
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
//        String[] result = response.split(" ", -1);
//        try {
//            String checkError = result[10];
//            String newResult = result[14];
//            int decimal = Integer.parseInt(newResult, 16);
//
//            TextView amounttext = findViewById(R.id.amounttext);
//            amounttext.setText("Paid :" + decimal);
//
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) 0x00, (byte) 0x00,
//                            (byte) ((byte) num), (byte) 0x00, (byte) 0x82, (byte) 0x00,
//                            (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0xA2, (byte) 0x00};
//                    int crcResult = crc16_CCITT(hexString, hexString.length);
//                    String results = String.format("0x%04X", crcResult);
//
//                    startCommand("FE EF 00 00 0" + num + " 00 82 00 05 00 03 A2 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                }
//            }, 3000);
//
//
//        } catch (Exception ex) {
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
//}
