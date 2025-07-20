//package com.tcn.sdk.springdemo.Utilities;
//
//import android.widget.TextView;
//
//import com.tcn.sdk.springdemo.CheckPortActivitiy;
//import com.tcn.sdk.springdemo.ComAssistant.MyFunc;
//import com.tcn.sdk.springdemo.ComAssistant.SerialHelper;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import java.io.IOException;
//import java.security.InvalidParameterException;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class CheckMachineStatus {
//    SerialControl ComA;
//    DispQueueThread DispQueue;
//    long currentTimeMillis;
//    private CheckPortActivitiy activity;
//    private TextView tv_ms;
//
//    public void status(CheckPortActivitiy activity, TextView tv_ms) {
//        currentTimeMillis = System.currentTimeMillis();
//        this.activity = activity;
//        this.tv_ms = tv_ms;
//        activity.onlinestatus = false;
//        ComA = new SerialControl();
//        DispQueue = new DispQueueThread();
//        DispQueue.start();
//        startOpenPort();
//    }
//
//    private void startCommand(String commandSend) {
//        sendPortData(ComA, sentCommand(commandSend));
//    }
//
//    private void sendPortData(SerialHelper ComPort, String sOut) {
//        if (ComPort != null && ComPort.isOpen()) {
//            ComPort.sendHex(sOut);
//        }
//    }
//
//    private String sentCommand(String commandSend) {
//        commandSend = commandSend.replace(" ", "");
//        return commandSend;
//    }
//
//    private void startOpenPort() {
//        ComA.setPort("/dev/ttyS1");
//        ComA.setBaudRate("19200");
//        OpenComPort(ComA);
//    }
//
//    public void ClosePort() {
//        CloseComPort(ComA);
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
/// /            ShowMessage("打开串口失败:没有串口读/写权限!");
//        } catch (IOException e) {
/// /            ShowMessage("打开串口失败:未知错误!");
//        } catch (InvalidParameterException e) {
/// /            ShowMessage("打开串口失败:参数错误!");
//        }
//        startCommand("FE EF 00 00 03 00 10 00 05 00 03 00 00 0B 12");
//    }
//
//    private void DispRecData(ComBean ComRecData) {
//        long finalTimeMillis = System.currentTimeMillis();
//        long total = finalTimeMillis - currentTimeMillis;
//        tv_ms.setText(total + " ms");
//        String response = MyFunc.ByteArrToHex(ComRecData.bRec);
//        activity.onlinestatus = true;
//        CloseComPort(ComA);
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
