//package com.tcn.sdk.springdemo.DispenseTesting;
//
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.serialport.SerialPort;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//
//import com.tcn.sdk.springdemo.R;
//import com.tcn.sdk.springdemo.Utilities.HexDataHelper;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//
//public class MainActivity_Dispense extends AppCompatActivity implements View.OnClickListener {
//    private final ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
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
//    List<reuseObj> arr_mylist_total;
//    private GridView grid_view;
//    private list_adapter_1 adapter;
//    private Dialog dialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        setContentView(R.layout.activity_main_dispense);
//        grid_view = findViewById(R.id.grid_view);
//        List<reuseObj> arr_mylist = new ArrayList<>();
//        arr_mylist_total = new ArrayList<>();
//        reuseObj obj = new reuseObj();
//        obj.setname1("101");
//        obj.setname2("0");
//        obj.setname3("0");
//        arr_mylist.add(obj);
//
//        reuseObj obj1 = new reuseObj();
//        obj1.setname1("102");
//        obj1.setname2("0");
//        obj1.setname3("0");
//        arr_mylist.add(obj1);
//
//        reuseObj obj2 = new reuseObj();
//        obj2.setname1("103");
//        obj2.setname2("0");
//        obj2.setname3("0");
//        arr_mylist.add(obj2);
//
//        reuseObj obj3 = new reuseObj();
//        obj3.setname1("104");
//        obj3.setname2("0");
//        obj3.setname3("0");
//        arr_mylist.add(obj3);
//
//        GridAdapter customAdapter = new GridAdapter(this, R.layout.grid_mylist, arr_mylist);
//        grid_view.setAdapter(customAdapter);
//        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (arr_mylist.get(i).getname2().equals("0")) {
//                    arr_mylist.get(i).setname2("1");
//                } else {
//                    arr_mylist.get(i).setname2("0");
//                }
//                customAdapter.update(arr_mylist);
//                arr_mylist_total = new ArrayList<>();
//                for (int j = 0; j < arr_mylist.size(); j++) {
//                    if (arr_mylist.get(j).getname2().equals("1")) {
//                        reuseObj obj = new reuseObj();
//                        obj.setname1(arr_mylist.get(j).name1);
//                        obj.setname2(arr_mylist.get(j).name2);
//                        obj.setname3(arr_mylist.get(j).name3);
//                        arr_mylist_total.add(obj);
//                    }
//                }
//            }
//        });
//        Button btn_confirm = findViewById(R.id.btn_confirm);
//        btn_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int k = 0; k < arr_mylist_total.size(); k++) {
//                    arr_mylist_total.get(k).setname3("0");
//                }
//                String text = "";
//                handler.post(new RunableEx(text) {
//                    public void run() {
//                        adapter.update(arr_mylist_total);
//                    }
//                });
//
//                if (arr_mylist_total.size() > 0) {
//                    for (int i = 0; i < arr_mylist_total.size(); i++) {
//                        if (arr_mylist_total.get(i).getname1().equals("101")) {
//                            int hdhInt = Integer.parseInt("101");
//                            short[] hdhbyte = HexDataHelper.Int2Short16_2(hdhInt);
//                            if (hdhbyte.length == 1) {
//                                short temp = hdhbyte[0];
//                                hdhbyte = new short[2];
//                                hdhbyte[0] = 0;
//                                hdhbyte[1] = temp;
//                            }
//                            byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
//                            data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
//                            //writeCmd(data);
//                            queue.add(data);
//                        }
//
//                        if (arr_mylist_total.get(i).getname1().equals("102")) {
//                            int hdh1Int = Integer.parseInt(arr_mylist_total.get(i).getname1());
//                            short[] hdhbyte1 = HexDataHelper.Int2Short16_2(hdh1Int);
//                            //货道号补齐两字节
//
//                            if (hdhbyte1.length == 1) {
//                                short temp1 = hdhbyte1[0];
//                                hdhbyte1 = new short[2];
//                                hdhbyte1[0] = 0;
//                                hdhbyte1[1] = temp1;
//                            }
//
//                            byte[] data1 = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte1[0], (byte) hdhbyte1[1], 0x00};
//                            data1[data1.length - 1] = (byte) HexDataHelper.computerXor(data1, 0, data1.length - 1);
//                            //writeCmd(data1);
//                            queue.add(data1);
//                        }
//
//                        if (arr_mylist_total.get(i).getname1().equals("103")) {
//                            int hdh2Int = Integer.parseInt(arr_mylist_total.get(i).getname1());
//                            short[] hdhbyte2 = HexDataHelper.Int2Short16_2(hdh2Int);
//                            //货道号补齐两字节
//
//                            if (hdhbyte2.length == 1) {
//                                short temp1 = hdhbyte2[0];
//                                hdhbyte2 = new short[2];
//                                hdhbyte2[0] = 0;
//                                hdhbyte2[1] = temp1;
//                            }
//
//                            byte[] data2 = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte2[0], (byte) hdhbyte2[1], 0x00};
//                            data2[data2.length - 1] = (byte) HexDataHelper.computerXor(data2, 0, data2.length - 1);
//                            //writeCmd(data1);
//                            queue.add(data2);
//                        }
//
//                        if (arr_mylist_total.get(i).getname1().equals("104")) {
//                            int hdh3Int = Integer.parseInt(arr_mylist_total.get(i).getname1());
//                            short[] hdhbyte3 = HexDataHelper.Int2Short16_2(hdh3Int);
//                            //货道号补齐两字节
//
//                            if (hdhbyte3.length == 1) {
//                                short temp1 = hdhbyte3[0];
//                                hdhbyte3 = new short[2];
//                                hdhbyte3[0] = 0;
//                                hdhbyte3[1] = temp1;
//                            }
//
//                            byte[] data3 = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte3[0], (byte) hdhbyte3[1], 0x00};
//                            data3[data3.length - 1] = (byte) HexDataHelper.computerXor(data3, 0, data3.length - 1);
//                            //writeCmd(data1);
//                            queue.add(data3);
//                        }
//                    }
//                    popup();
//                }
//            }
//        });
//        findViewById(R.id.connect).setOnClickListener(this);
//        findViewById(R.id.driverhd).setOnClickListener(this);
//    }
//
//    private void popup() {
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.dialog);
//        ListView list = dialog.findViewById(R.id.list);
//        adapter = new list_adapter_1(this, arr_mylist_total);
//        list.setAdapter(adapter);
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.dimAmount = 0.8f;
//        dialog.getWindow().setAttributes(lp);
//        Button tv_cancel = dialog.findViewById(R.id.btn_cancel);
//
//
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        final int id = view.getId();
//        if (id == R.id.connect) {
//            if (null == serialPort)
//                bindSerialPort();
//            else {
//                try {
//                    serialPort.close();
//                    serialPort = null;
//                    ((Button) view).setText("连接");
//                } catch (Exception e) {
//
//                }
//            }
//        } else if (id == R.id.driverhd) {
//            try {
//                //int hdh = Integer.parseInt(((EditText) findViewById(R.id.hdh)).getText().toString());
//                EditText hdh = findViewById(R.id.hdh);
//                if (hdh.getText().toString().length() > 0) {
//                    int hdhInt = Integer.parseInt(hdh.getText().toString());
//                    short[] hdhbyte = HexDataHelper.Int2Short16_2(hdhInt);
//                    if (hdhbyte.length == 1) {
//                        short temp = hdhbyte[0];
//                        hdhbyte = new short[2];
//                        hdhbyte[0] = 0;
//                        hdhbyte[1] = temp;
//                    }
//                    byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
//                    data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
//                    //writeCmd(data);
//                    queue.add(data);
//                }
//                EditText hdh1 = findViewById(R.id.hdh1);
//                if (hdh1.getText().toString().length() > 0) {
//                    int hdh1Int = Integer.parseInt(hdh1.getText().toString());
//                    short[] hdhbyte1 = HexDataHelper.Int2Short16_2(hdh1Int);
//                    //货道号补齐两字节
//
//                    if (hdhbyte1.length == 1) {
//                        short temp1 = hdhbyte1[0];
//                        hdhbyte1 = new short[2];
//                        hdhbyte1[0] = 0;
//                        hdhbyte1[1] = temp1;
//                    }
//
//                    byte[] data1 = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte1[0], (byte) hdhbyte1[1], 0x00};
//                    data1[data1.length - 1] = (byte) HexDataHelper.computerXor(data1, 0, data1.length - 1);
//                    //writeCmd(data1);
//                    queue.add(data1);
//                }
//            } catch (Exception e) {
//
//            }
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
//    public void onSerialPortConnectStateChanged(boolean connected) {
//        if (connected) {
//            ((Button) findViewById(R.id.connect)).setText("断开");
//        } else {
//            ((Button) findViewById(R.id.connect)).setText("连接");
//        }
//    }
//
//    private void bindSerialPort() {
//        devPath = ((EditText) findViewById(R.id.dev)).getText().toString();
//        baudrate = Integer.parseInt(((EditText) findViewById(R.id.baudrate)).getText().toString());
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
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                onSerialPortConnectStateChanged(true);
//                            }
//                        });
//                        readSerialPortData();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                } finally {
//                    if (null != serialPort) {
//                        try {
//                            serialPort.close();
//                            serialPort = null;
//                        } catch (Exception e) {
//                        }
//                    }
//
//                }
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        onSerialPortConnectStateChanged(false);
//                    }
//                });
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
//        try {
//            serialPort.getOutputStream().write(cmd);
//            serialPort.getOutputStream().flush();
//            addText(">> " + HexDataHelper.hex2String(cmd));
//            String out = HexDataHelper.hex2String(cmd);
//            System.out.println("loggings-out-" + HexDataHelper.hex2String(cmd));
//        } catch (Exception e) {
//
//        }
//    }
//
//    public void proccessCmd(byte[] cmd) {
//        System.out.println("loggings-in-" + HexDataHelper.hex2String(cmd));
//        String in = HexDataHelper.hex2String(cmd);
//        addText("<<" + HexDataHelper.hex2String(cmd));
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
//            //writeCmd(ackBytes);
//        } else if (0x04 == (short) (cmd[2] & 0xff)) {
/// /            System.out.println("loggings-get-"+HexDataHelper.hex2String(cmd));
/// /            serialPort.close();
//            writeCmd(ackBytes);
//            //update status here
//            if (0x01 == (short) (cmd[5] & 0xff)) {
//                //dispensing
//            } else if (0x02 == (short) (cmd[5] & 0xff)) {
//                //dispensed
//                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
//                updateStatus(product);
//            } else {
//                //dispensed with no dropsensor
//                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
//                updateStatus(product);
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
//    private void updateStatus(int product) {
//        String productcode = "";
//        switch (product) {
//            case 101:
//                productcode = "101";
//                break;
//            case 102:
//                productcode = "102";
//                break;
//            case 103:
//                productcode = "103";
//                break;
//            case 104:
//                productcode = "104";
//                break;
//        }
//        for (int i = 0; i < arr_mylist_total.size(); i++) {
//            if (productcode.equalsIgnoreCase(arr_mylist_total.get(i).getname1())) {
//                arr_mylist_total.get(i).setname3("1");
//            }
//        }
//        String text = "";
//        if (arr_mylist_total.size() > 0) {
//            handler.post(new RunableEx(text) {
//                public void run() {
//                    adapter.update(arr_mylist_total);
//
//                    int check = 0;
//                    for (int i = 0; i < arr_mylist_total.size(); i++) {
//                        if (arr_mylist_total.get(i).getname3().equals("1")) {
//                            check++;
//                        }
//                    }
//                    if (check == arr_mylist_total.size()) {
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity_Dispense.this);
//                        builder1.setMessage("All item dispensed");
//                        builder1.setCancelable(true);
//
//                        builder1.setPositiveButton(
//                                "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog1, int id) {
//                                        dialog1.cancel();
//                                        dialog.dismiss();
//                                    }
//                                });
//
//                        builder1.setNegativeButton(
//                                "Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog1, int id) {
//                                        dialog1.cancel();
//                                    }
//                                });
//
//                        AlertDialog alert11 = builder1.create();
//                        alert11.show();
//                    }
//                }
//            });
//        }
//    }
//
//    public void addText(String text) {
//    }
//
//    abstract class RunableEx implements Runnable {
//        String data;
//
//        public RunableEx(String data) {
//            this.data = data;
//        }
//    }
//}
