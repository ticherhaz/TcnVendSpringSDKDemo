//package com.tcn.sdk.springdemo.Note;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.InputType;
//import android.text.method.KeyListener;
//import android.text.method.NumberKeyListener;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.gson.Gson;
//import com.tcn.sdk.springdemo.ComAssistant.MyFunc;
/// /import com.tcn.sdk.springdemo.ComAssistant.SerialHelper;
//import com.tcn.sdk.springdemo.Dispense.AppLogger;
//import com.tcn.sdk.springdemo.Model.UserObj;
//import com.tcn.sdk.springdemo.R;
//import com.tcn.sdk.springdemo.bean.AssistBean;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.security.InvalidParameterException;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//import cn.pedant.SweetAlert.SweetAlertDialog;
//
//public class CashNoteNewActivity extends AppCompatActivity {
//    private static CashNoteNewActivity activity;
//    private static Handler handlerTimer;
//    private static CountDownTimer cTimer = null;
//    private static SweetAlertDialog sweetAlertDialog;
//    private final String TAG = "CashNoteNewActivity";
//    private final String CMD_OPEN = "FEEF000000008200060002B20001C5FF";
//    private final String CMD_CLOSE = "FEEF000000008200060002B20000E4EF";
//    private final String CMD_ACCEPT_NOTE = "FEEF00000700820007000244000001E8D1";
//    private final String CMD_REJECT_NOTE = "FEEF000002008200070002440000028568";
//    private final String CMD_CLEAR_NOTE = "FEEF000000008200050002B10022D9";
//    private final String CMD_COIN_STATUS = "FEEF0000E4008200050003C300515B";
//    private final String CMD_NOTE_STATUS = "FEEF000000008200050003A10061ED";
//    private final String CMD_COIN_AMOUNT = "FEEF000007008200050003C200B302";
//    private final String CMD_COIN_DISPENSE_STATUS = "FEEF00002A008200050003C3004DC8";
//    private final Handler handlerCounter = new Handler();
//    SerialControl ComA;//4个串口
//    DispQueueThread DispQueue;//刷新显示线程
//    AssistBean AssistData;//用于界面数据序列化和反序列化
//    int iRecLines = 0;//接收区行数
//    Intent intent;
//    private String editTextRecDispStr, price;
//    private TextView amounttext;
//    private UserObj userObj;
//    private Button btnAccept, btnReject, backbtn;
//    private boolean isPaid = false;
//    private boolean isReadyClose = false;
//    private String msg = "";
//    private String commData = "";
//    private String prevStr = "";
//    private int balance;
//    private double total;
//    private long coinBalance;
//    private int coin100;
//    private int coin50;
//    private int coin20;
//    private int coin10;
//    private int note100;
//    private int note50;
//    private int note20;
//    private int note10;
//    private int note5;
//    private int note2;
//    private int note1;
//    private int counter;
//    private boolean isNoteUpdate;
//    private boolean isCoinUpdate;
//    private boolean isCashConnect;
//    private boolean isComplete;
//    private boolean isStuck;
//
//    private static void showsweetalerttimeout() {
//        sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
//
//        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
//        sweetAlertDialog.setContentText("This session will end in 10");
//        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                cancelTimer();
//                handlerReset();
//                sweetAlertDialog.dismissWithAnimation();
//                resetTimer();
//            }
//        });
//
//        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                sweetAlertDialog.dismiss();
//            }
//        });
//        if (!activity.isFinishing()) {
//            sweetAlertDialog.show();
//        }
//
//    }
//
//    static void startTimer() {
//        showsweetalerttimeout();
//        cTimer = new CountDownTimer(10000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//                sweetAlertDialog.dismiss();
//            }
//        };
//        cTimer.start();
//    }
//
//    static void cancelTimer() {
//        if (cTimer != null)
//            cTimer.cancel();
//    }
//
//    static void handlerReset() {
//        if (handlerTimer != null) {
//            handlerTimer.removeCallbacksAndMessages(null);
//        }
//    }
//
//    private static void resetTimer() {
//        handlerTimer = new Handler(Looper.getMainLooper());
//        handlerTimer.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handlerReset();
//                startTimer();
//            }
//        }, 60000);
//    }
//
//    public static byte[] hexStringToByteArray(String s) {
//        int len = s.length();
//        byte[] data = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//                    + Character.digit(s.charAt(i + 1), 16));
//        }
//        return data;
//    }
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        RollingLogger.i(TAG, "onCreate-Start");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cashnotenew);
//        ComA = new SerialControl();
//        DispQueue = new DispQueueThread();
//        DispQueue.start();
//        AssistData = getAssistData();
//        setControls();
//        openPort();
//        setHex();
//        activity = this;
//
//        commData = "";
//        prevStr = "";
//        balance = 0;
//        total = 0.0;
//        coinBalance = 0;
//        coin100 = 0;
//        coin50 = 0;
//        coin20 = 0;
//        coin10 = 0;
//        note100 = 0;
//        note50 = 0;
//        note20 = 0;
//        note10 = 0;
//        note5 = 0;
//        note2 = 0;
//        note1 = 0;
//        isNoteUpdate = false;
//        isCoinUpdate = false;
//        isCashConnect = false;
//        isComplete = false;
//        isStuck = false;
//
//        intent = getIntent();
//        price = intent.getStringExtra("price");
//
//        String jsonString = intent.getStringExtra("obj");
//        Gson gson = new Gson();
//        userObj = gson.fromJson(jsonString, UserObj.class);
//
//        TextView pricetext = findViewById(R.id.pricetext);
//        amounttext = findViewById(R.id.amounttext);
//        pricetext.setText("Total : RM " + price);
//        amounttext.setText("Paid : RM 0.00");
//
//        int totalPrice = (int) Math.round(Double.parseDouble(price) * 100);
//
//        backbtn = findViewById(R.id.backbtn);
//        if (totalPrice % 10 != 0) {
//            disableCashPayment("Cash payment service unavailable.", "Products price contain less than 10 Sen.");
//            return;
//        }
//
//        btnAccept = findViewById(R.id.btnAccept);
//        btnAccept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendPortData(ComA, CMD_ACCEPT_NOTE);
//            }
//        });
//        btnReject = findViewById(R.id.btnReject);
//        btnReject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendPortData(ComA, CMD_REJECT_NOTE);
//            }
//        });
//
//        backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                backbtn.setEnabled(false);
//                RollingLogger.i(TAG, "backbtn-onClick");
//                sendPortData(ComA, CMD_CLOSE);
//                checkReturn();
//                ExitPayment(-2);
//            }
//        });
//
//        setHandlerCounter();
//    }
//
//    private void setHandlerCounter() {
//        Runnable updateRunnable = new Runnable() {
//            @Override
//            public void run() {
//                counter++;
//                Log.d("Timer", String.valueOf(counter));
//                if (counter > 5 && !isCashConnect) {
//                    disableCashPayment("Cash payment service unavailable.", "Cash machine can't connect.");
//                    return;
//                }
//
//                if (isPaid & !isComplete) {
//                    sendPortData(ComA, CMD_COIN_DISPENSE_STATUS);
//                }
//
//                if (isComplete) {
//                    if (!isNoteUpdate)
//                        sendPortData(ComA, CMD_NOTE_STATUS);
//
//                    if (!isCoinUpdate)
//                        sendPortData(ComA, CMD_COIN_AMOUNT);
//
//                    if (isNoteUpdate && isCoinUpdate) {
//                        ExitPayment(RESULT_OK);
//                        return;
//                    }
//                }
//
//                if (counter <= 5) {
//                    handlerCounter.postDelayed(this, 1000);
//                    return;
//                }
//
//                if (isPaid) {
//                    if (!isStuck) {
//                        isStuck = true;
//                        isComplete = true;
//                        counter = 0;
//                    } else {
//                        ExitPayment(RESULT_OK);
//                        return;
//                    }
//                    handlerCounter.postDelayed(this, 1000);
//                }
//                // Update the UI or perform periodic task
//            }
//        };
//
//        handlerCounter.postDelayed(updateRunnable, 0);
//    }
//
//    @Override
//    public void onDestroy() {
//        RollingLogger.i(TAG, "onDestroy-Start");
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        RollingLogger.i(TAG, "onPause-Start");
//        super.onPause();
//        if (isFinishing()) {
//            RollingLogger.i(TAG, "isFinishing-Start");
//            // Here  you can be sure the Activity will be destroyed eventually
//            DispQueue.interrupt();
//            sendPortData(ComA, CMD_CLOSE);
//            saveAssistData(AssistData);
//            CloseComPort(ComA);
//            ComA = null;
//            handlerCounter.removeCallbacksAndMessages(null);
//        }
//    }
//
//    //----------------------------------------------------
//    private void setControls() {
//        String appName = getString(R.string.app_name);
//        try {
//            PackageInfo pinfo = getPackageManager().getPackageInfo("com.bjw.ComAssistant", PackageManager.GET_CONFIGURATIONS);
//            String versionName = pinfo.versionName;
//            String versionCode = String.valueOf(pinfo.versionCode);
//            setTitle(appName + " V" + versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.removeCallbacksAndMessages(null);
//                sendPortData(ComA, CMD_COIN_STATUS);
//                sendPortData(ComA, CMD_CLEAR_NOTE);
//                sendPortData(ComA, CMD_OPEN);
//            }
//        }, 1000);
//
//        DispAssistData(AssistData);
//    }
//
//    private void setHex() {
//        KeyListener HexkeyListener = new NumberKeyListener() {
//            public int getInputType() {
//                return InputType.TYPE_CLASS_TEXT;
//            }
//
//            @Override
//            protected char[] getAcceptedChars() {
//                return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                        'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
//            }
//        };
//        AssistData.setTxtMode(false);
//        setSendData();
//    }
//
//    // problem here, it did not connect to the correct port
//    private void openPort() {
//        ComA.setPort("/dev/ttyS1");
//        ComA.setBaudRate("19200");
//        OpenComPort(ComA);
//    }
//
//    //----------------------------------------------------刷新界面数据
//    private void DispAssistData(AssistBean AssistData) {
//        setSendData();
//        setDelayTime();
//    }
//
//    //----------------------------------------------------保存、获取界面数据
//    private void saveAssistData(AssistBean AssistData) {
//        AssistData.sTimeA = "500";
//        SharedPreferences msharedPreferences = getSharedPreferences("ComAssistant", Context.MODE_PRIVATE);
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(AssistData);
//            String sBase64 = new String(Base64.encode(baos.toByteArray(), 0));
//            SharedPreferences.Editor editor = msharedPreferences.edit();
//            editor.putString("AssistData", sBase64);
//            editor.commit();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //----------------------------------------------------
//    private AssistBean getAssistData() {
//        SharedPreferences msharedPreferences = getSharedPreferences("ComAssistant", Context.MODE_PRIVATE);
//        AssistBean AssistData = new AssistBean();
//        try {
//            String personBase64 = msharedPreferences.getString("AssistData", "");
//            byte[] base64Bytes = Base64.decode(personBase64.getBytes(), 0);
//            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            AssistData = (AssistBean) ois.readObject();
//            return AssistData;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return AssistData;
//    }
//
//    //----------------------------------------------------设置自动发送延时
//    private void setDelayTime() {
//        AssistData.sTimeA = "500";
//        SetiDelayTime(ComA, "500");
//    }
//
//    //----------------------------------------------------设置自动发送数据
//    private void setSendData() {
//        AssistData.setSendA("500");
//        SetLoopData(ComA, "500");
//    }
//
//    //----------------------------------------------------设置自动发送延时
//    private void SetiDelayTime(SerialHelper ComPort, String sTime) {
//        ComPort.setiDelay(Integer.parseInt(sTime));
//    }
//
//    //----------------------------------------------------设置自动发送数据
//    private void SetLoopData(SerialHelper ComPort, String sLoopData) {
//        ComPort.setHexLoopData(sLoopData);
//    }
//
//    //----------------------------------------------------显示接收数据
//    private void DispRecData(ComBean ComRecData) {
//        RollingLogger.i(TAG, "DispRecData-Start");
//        //timer count down reset
//        handlerReset();
//        cancelTimer();
//        resetTimer();
//        //end timer
//
//        String sMsg = ComRecData.sRecTime +
//                "[" +
//                ComRecData.sComPort +
//                "]" +
//                "[Hex] " +
//                MyFunc.ByteArrToHex(ComRecData.bRec) +
//                "\r\n";
//        editTextRecDispStr = sMsg;
//
//        Log.d("edit", editTextRecDispStr);
//
//        int totalPrice = (int) Math.round(Double.parseDouble(price) * 100);
//
//        List<String> cmdList = Arrays.asList(MyFunc.ByteArrToHex(ComRecData.bRec).split(" "));
//        if (cmdList.size() > 0) {
//            String replaceStr = MyFunc.ByteArrToHex(ComRecData.bRec).replace(" ", "");
//
//            commData = commData.concat(replaceStr);
//            Log.d("commData", "commData-" + commData);
//            RollingLogger.i(TAG, "commData-" + commData);
//
//            while (commData.length() > 0) {
//                counter = 0;
//
//                if (commData.length() < 8) {
//                    return;
//                }
//
//                if (checkCRC(commData)) {
//                    replaceStr = commData;
//                    commData = "";
//                    if (replaceStr == prevStr) {
//                        return;
//                    }
//                    prevStr = replaceStr;
//
//                } else if (commData.substring(4).contains("FEEF")) {
//                    replaceStr = commData.substring(0, commData.substring(4).indexOf("FEEF") + 4);
//                    commData = commData.substring(commData.substring(4).indexOf("FEEF") + 4);
//
//                    if (replaceStr == prevStr) {
//                        return;
//                    }
//
//                    prevStr = replaceStr;
//                } else {
//                    return;
//                }
//
//                Log.d("response", "response-" + replaceStr);
//                RollingLogger.i(TAG, "response-" + replaceStr);
//
//
//                if (!replaceStr.startsWith("FEEF")) {
//                    return;
//                }
//
//                if (checkErrCmd(replaceStr)) {
//                    return;
//                }
//
//                if (getNoteInfo(replaceStr)) {
//                    isNoteUpdate = true;
//                    return;
//                }
//
//                if (getCoinInfo(replaceStr)) {
//                    isCoinUpdate = true;
//                    return;
//                }
//
//                if (checkCoinStatus(replaceStr)) {
//                    return;
//                }
//
//                if (checkCashCtrl(replaceStr)) {
//                    return;
//                }
//
//                if (replaceStr.startsWith("FEEF0000E400828007000103C30000")) {
//                    disableCashPayment();
//                    return;
//                }
//                //if(replaceStr.startsWith("FEEF000004008200150004B20000000000")){
//                if (replaceStr.length() == 70) {
//                    sendACK(replaceStr);
//
//                    //FEEF00002C018200150004B200 DC00 E803 0000 6400 B8010000 00 00 00 00 00 00 00 00 xxxx
//                    String coinMachineStatus = replaceStr.substring(50, 52);
//                    String coinMachineAllowInsert = replaceStr.substring(52, 54);
//                    String noteMachineStatus = replaceStr.substring(54, 56);
//                    String noteMachineAllowInsert = replaceStr.substring(56, 58);
//
//                    setCoinAmount(replaceStr);
//
//                    if (!isCoinEnough()) {
//                        disableCashPayment();
//                        return;
//                    }
//
//                    String returnCoin = replaceStr.substring(42, 50);
//
//                    long i = Long.parseLong(returnCoin, 16);
//                    coinBalance = (i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff;
//
//                    if (coinBalance < totalPrice) {
//                        disableCashPayment();
//                        return;
//                    }
//
//                    int decimalValue = convertHexToInt(replaceStr.substring(34, 38));
//                    int acceptNoteValue = convertHexToInt(replaceStr.substring(30, 34));
//                    int acceptCoinValue = convertHexToInt(replaceStr.substring(26, 30));
//                    int returnCoinValue = convertHexToInt(replaceStr.substring(38, 42));
//
//                    int totalAcceptValue = acceptNoteValue + acceptCoinValue;
//                    balance = totalAcceptValue - totalPrice;
//
//                    total = (totalAcceptValue) / 100.00;
//
//                    Log.d("balance", String.valueOf(balance));
//                    Log.d("decimalValue", String.valueOf(decimalValue));
//
//                    if (!isPaid) {
//                        if (decimalValue > 0) {
//                            if (totalAcceptValue + decimalValue >= totalPrice) {
//                                if (coinBalance >= balance + decimalValue) {
//                                    sendPortData(ComA, CMD_ACCEPT_NOTE);
//                                    msg = "\nCash Accepted.";
//                                } else {
//                                    sendPortData(ComA, CMD_REJECT_NOTE);
//                                    msg = "\nCash Rejected. Not enough coins to return";
//                                }
//                            } else {
//                                sendPortData(ComA, CMD_ACCEPT_NOTE);
//                            }
//                        } else {
//                            if (balance >= 0) {
//                                if (balance >= 0 && !isPaid) {
//                                    backbtn.setEnabled(false);
//                                    isPaid = true;
//                                    sendPortData(ComA, CMD_CLOSE);
//                                    sendPortData(ComA, CMD_NOTE_STATUS);
//                                    returnBalance(balance);
//
//                                    if (balance > 0) {
//                                        msg = "\nReturning balance RM" + String.format("%.2f", balance / 100.00);
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        if (decimalValue > 0) {
//                            sendPortData(ComA, CMD_REJECT_NOTE);
//                        }
//                    }
//
//                    amounttext.setText("Paid : RM " + String.format("%.2f", total) + msg);
//                    //"Paid: " didn't change because there is no process happens. It took the text from the design instead
//                }
//
//                if (isPaid) {
//                    if (replaceStr.startsWith("FEEF0000F2008280")) {
//                        isReadyClose = true;
//                    }
//                }
//            }
//        }
//    }
//
//    private void sendACK(String data) {
//        String cmd = data.substring(0, 12) + "8280" + "0600" + Result.OK.getValue() + Type.REPORT.getValue() + "B200";
//        cmd = cmd + getCRC(cmd);
//        sendPortData(ComA, cmd);
//    }
//
//    private boolean isCoinEnough() {
//        if (coin10 == 0) {
//            return false;
//        }
//
//        if (coin50 == 0) {
//            return (coin10 * 10) + (coin20 * 20) >= 90;
//        } else {
//            return (coin10 * 10) + (coin20 * 20) >= 40;
//        }
//    }
//
//    private void setCoinAmount(String data) {
//        coin100 = Integer.parseInt(data.substring(58, 60), 16);
//        coin50 = Integer.parseInt(data.substring(60, 62), 16);
//        coin20 = Integer.parseInt(data.substring(62, 64), 16);
//        coin10 = Integer.parseInt(data.substring(64, 66), 16);
//
//        Log.d("Coin", "100Cents: " + coin100 + "; 50Cents: " + coin50 + "; 20Cents: " + coin20 + "; 10Cents: " + coin10);
//        RollingLogger.i(TAG, "100Cents: " + coin100 + "; 50Cents: " + coin50 + "; 20Cents: " + coin20 + "; 10Cents: " + coin10);
//    }
//
//    private boolean checkErrCmd(String data) {
//        if (data.startsWith("828006000005", 12)) {
//            String errCode = data.substring(24, 28);
//            switch (errCode) {
//                case "6027":
//                    disableCashPayment("Cash payment service unavailable.", "BANKNOTE_COIN_DISCONNECT");
//                    break;
//                default:
//                    disableCashPayment("Cash payment service unavailable.", "Machine Error - " + errCode);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private boolean getNoteInfo(String data) {
//        if (data.startsWith("828014000103A100", 12)) {
//            note100 = convertHexToInt(data.substring(28, 32));
//            note50 = convertHexToInt(data.substring(32, 36));
//            note20 = convertHexToInt(data.substring(36, 40));
//            note10 = convertHexToInt(data.substring(40, 44));
//            note5 = convertHexToInt(data.substring(44, 48));
//            note2 = convertHexToInt(data.substring(48, 52));
//            note1 = convertHexToInt(data.substring(52, 56));
//
//            Log.d("Note", note100 + "|" + note50 + "|" + note20 + "|" + note10 + "|" + note5 + "|" + note2 + "|" + note1);
//            RollingLogger.i(TAG, "Note-" + note100 + "|" + note50 + "|" + note20 + "|" + note10 + "|" + note5 + "|" + note2 + "|" + note1);
//            return true;
//        }
//        return false;
//    }
//
//    private boolean getCoinInfo(String data) {
//        if (data.startsWith("82801A000103C200", 12)) {
//            String returnCoin = data.substring(44, 52);
//
//            long i = Long.parseLong(returnCoin, 16);
//            coinBalance = (i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff;
//
//            String coin100Count = data.substring(60, 62);
//            String coin50Count = data.substring(62, 64);
//            String coin20Count = data.substring(64, 66);
//            String coin10Count = data.substring(66, 68);
//
//            coin100 = Integer.parseInt(coin100Count, 16);
//            coin50 = Integer.parseInt(coin50Count, 16);
//            coin20 = Integer.parseInt(coin20Count, 16);
//            coin10 = Integer.parseInt(coin10Count, 16);
//
//            Log.d("Coin", coin100 + "|" + coin50 + "|" + coin20 + "|" + coin10);
//            RollingLogger.i(TAG, "Coin-" + coin100 + "|" + coin50 + "|" + coin20 + "|" + coin10);
//
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkCoinStatus(String data) {
//        if (data.startsWith("828007000103C300", 12) && isPaid) {
//            if (data.startsWith("01", 28)) {
//                isComplete = true;
//                return true;
//            }
//
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkCashCtrl(String data) {
//        if (data.startsWith("82800A000102B200", 12)) {
//            String coinStatus = data.substring(28, 30);
//            String coinErrCode = data.substring(30, 32);
//            String noteStatus = data.substring(32, 34);
//            String noteErrCode = data.substring(34, 36);
//
//            if (!coinStatus.equals("00") || !noteStatus.equals("00")) {
//                disableCashPayment();
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private void ExitPayment(int result) {
//        RollingLogger.i(TAG, "ExitPayment");
//        CloseComPort(ComA);
//
//        Intent intent = new Intent();
//
//        String versionName = "";
//        try {
//            versionName = activity.getPackageManager()
//                    .getPackageInfo(activity.getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        userObj.setMtd(userObj.getMtd() + " (Rcv " + String.format("%.2f", total) +
//                " Rtn " + String.format("%.2f", balance / 100.00) +
//                " Bal " + String.format("%.2f", coinBalance / 100.00) + ")" +
//                " (0.50: " + coin50 + " | 0.20: " + coin20 + " | 0.10: " + coin10 + ")" +
//                " (1: " + note1 + " | 2: " + note2 + " | 5: " + note5 + " | 10: " + note10 +
//                " | 20: " + note20 + ") (Cash Note: " + ((note1) + (note2 * 2) + (note5 * 5) + (note10 * 10) + (note20 * 20)) + ") " + versionName
//        );
//        Gson gson = new Gson();
//        String json = gson.toJson(userObj);
//        intent.putExtra("obj", json);
//
//        setResult(result, intent);
//        finish();
//    }
//
//    private void disableCashPayment(String title, String content) {
//        sendPortData(ComA, CMD_CLOSE);
//        CloseComPort(ComA);
//        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
//
//        dialog.setTitleText(title)
//                .setContentText(content);
//        dialog.setCancelable(false);
//        dialog.show();
//        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                dialog.dismiss();
//                activity.setVisible(false);
//                backbtn.setEnabled(true);
//                ExitPayment(RESULT_CANCELED);
//            }
//        });
//    }
//
//    private void checkReturn() {
//        RollingLogger.i(TAG, "checkReturn-total:" + total + "|coinBalance:" + coinBalance);
//        if ((total * 100) > coinBalance) {
//            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
//
//            dialog.setTitleText("Coin not enough to return.")
//                    .setContentText("Please contact helpdesk.");
//            dialog.setCancelable(false);
//            dialog.show();
//            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                @Override
//                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                    dialog.dismiss();
//                }
//            });
//        } else {
//            returnBalance((int) (total * 100));
//        }
//    }
//
//    private void disableCashPayment() {
//        sendPortData(ComA, CMD_CLOSE);
//        CloseComPort(ComA);
//        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
//
//        dialog.setTitleText("Cash payment service unavailable.")
//                .setContentText("We're out of coin.");
//        dialog.setCancelable(false);
//        dialog.show();
//        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//            @Override
//            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                dialog.dismiss();
//                activity.setVisible(false);
//                ExitPayment(RESULT_CANCELED);
//            }
//        });
//    }
//
//    private void returnBalance(int balance) {
//        RollingLogger.i(TAG, "returnBalance-" + balance);
//        ByteBuffer bbuf = ByteBuffer.allocate(4);
//        bbuf.order(ByteOrder.BIG_ENDIAN);
//        bbuf.putInt(balance);
//        bbuf.order(ByteOrder.LITTLE_ENDIAN);
//        int coin = bbuf.getInt(0);
//
//        byte[] fullcommand = new byte[]{
//                (byte) 0xFE, (byte) 0xEF, (byte) 0x00, (byte) 0x00,
//                (byte) 0xF2, (byte) 0x00, (byte) 0x82, (byte) 0x00, (byte) 0x07, (byte) 0x00,
//                (byte) 0x02, (byte) 0xB3, (byte) 0x00, bbuf.get(3), bbuf.get(2)
//        };
//
//        int crc16_ccitt = crc16_CCITT(fullcommand, fullcommand.length);
//
//        String hexString = Integer.toHexString(crc16_ccitt);
//
//        hexString = "0000" + hexString;
//        hexString = hexString.substring(hexString.length() - 4).toUpperCase();
//
//        String cmdCrc = hexString.substring(2, 4) + hexString.substring(0, 2);
//        String cmdCoin = String.format("%02X", bbuf.get(3)) + String.format("%02X", bbuf.get(2));
//
//        String cmd = "FEEF0000F2008200070002B300" + cmdCoin + cmdCrc;
//
//        sendPortData(ComA, cmd);
//    }
//
//    //----------------------------------------------------设置自动发送模式开关
//    private void SetAutoSend(SerialHelper ComPort, boolean isAutoSend) {
//        if (isAutoSend) {
//            ComPort.startSend();
//        } else {
//            ComPort.stopSend();
//        }
//    }
//
//    //----------------------------------------------------串口发送
//    private void sendPortData(SerialHelper ComPort, String sOut) {
//        Log.d("port", sOut);
//        RollingLogger.i(TAG, "port-" + sOut);
//        if (ComPort != null && ComPort.isOpen()) {
//            ComPort.sendHex(sOut);
//        }
//    }
//
//    //----------------------------------------------------关闭串口
//    private void CloseComPort(SerialHelper ComPort) {
//        RollingLogger.i(TAG, "CloseComPort");
//        if (ComPort != null) {
//            ComPort.stopSend();
//            ComPort.close();
//        }
//    }
//
//    //----------------------------------------------------打开串口
//    private void OpenComPort(SerialHelper ComPort) {
//        RollingLogger.i(TAG, "OpenComPort");
//        try {
//            ComPort.open();
//        } catch (SecurityException e) {
//            ShowMessage("打开串口失败:没有串口读/写权限!");
//        } catch (IOException e) {
//            ShowMessage("打开串口失败:未知错误!");
//        } catch (InvalidParameterException e) {
//            ShowMessage("打开串口失败:参数错误!");
//        }
//    }
//
//    //------------------------------------------显示消息
//    private void ShowMessage(String sMsg) {
//        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
//    }
//
//    private int convertHexToInt(String data) {
//        return Integer.parseInt(data.substring(2, 4) + data.substring(0, 2), 16);
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
//    private boolean checkCRC(String data) {
//        byte[] a = hexStringToByteArray(data.substring(0, data.length() - 4));
//        int b = crc16_CCITT(a, a.length);
//
//        String c = data.substring(data.length() - 4);
//        c = c.substring(2, 4) + c.substring(0, 2);
//
//        int d = Integer.parseInt(c, 16);
//
//        return b == d;
//    }
//
//    private String getCRC(String data) {
//        byte[] a = hexStringToByteArray(data);
//        int b = crc16_CCITT(a, a.length);
//
//        String hexString = Integer.toHexString(b);
//
//        hexString = "0000" + hexString;
//        hexString = hexString.substring(hexString.length() - 4).toUpperCase();
//
//        return hexString.substring(2, 4) + hexString.substring(0, 2);
//    }
//
//    enum Type {
//        SET("02"),
//        GET("03"),
//        REPORT("04"),
//        ERROR("05"),
//        OTHER("06");
//
//        private final String id;
//
//        Type(String id) {
//            this.id = id;
//        }
//
//        public String getValue() {
//            return id;
//        }
//    }
//
//    enum Result {
//        NG("00"),
//        OK("01");
//
//        private final String id;
//
//        Result(String id) {
//            this.id = id;
//        }
//
//        public String getValue() {
//            return id;
//        }
//    }
//
//    //----------------------------------------------------串口控制类
//    private class SerialControl extends SerialHelper {
//
//        //		public SerialControl(String sPort, String sBaudRate){
////			super(sPort, sBaudRate);
////		}
//        public SerialControl() {
//        }
//
//        @Override
//        protected void onDataReceived(final ComBean ComRecData) {
//            DispQueue.AddQueue(ComRecData);
//        }
//    }
//
//    //----------------------------------------------------刷新显示线程
//    private class DispQueueThread extends Thread {
//        private final Queue<ComBean> QueueList = new LinkedList<ComBean>();
//
//        @Override
//        public void run() {
//            super.run();
//            while (!isInterrupted()) {
//                final ComBean ComData;
//                boolean isInterrupted = false;
//
//                if (isInterrupted) {
//                    break;
//                }
//
//                while ((ComData = QueueList.poll()) != null) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            DispRecData(ComData);
//                        }
//                    });
//                    try {
//                        Thread.sleep(100);//显示性能高的话，可以把此数值调小。
//                    } catch (InterruptedException e) {
//                        isInterrupted = true;
//                        break;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
//            RollingLogger.i(TAG, "Thread Interrupted.");
//        }
//
//        public synchronized void AddQueue(ComBean ComData) {
//            QueueList.add(ComData);
//        }
//    }
//}