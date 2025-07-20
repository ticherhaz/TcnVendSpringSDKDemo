//package com.tcn.sdk.springdemo;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.RadioGroup;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
/// /import com.tcn.sdk.springdemo.ComAssistant.MyFunc;
/// /import com.tcn.sdk.springdemo.ComAssistant.SerialHelper;
//import com.tcn.sdk.springdemo.bean.ComBean;
//
//import java.io.IOException;
//import java.security.InvalidParameterException;
//import java.util.LinkedList;
//import java.util.Queue;
//
//public class MachineSettingInfo extends AppCompatActivity {
//    SerialControl ComA;
//    DispQueueThread DispQueue;
//    private TextView tv_surrounding_wet, tv_surrounding_degree, tv_cloud_degree, tv_pressure, tv_cloud, tv_fan, tv_set_degree, tv_serial,
//            tv_error;
//    private Button btn_Close, btn_surrounding_wet, btn_surrounding_degree, btn_cloud_degree, btn_pressure, btn_cloud, btn_fan, btn_set_degree,
//            btn_set_light_on, btn_set_light_off, btn_set_mist_on, btn_set_mist_off, btn_set_machine_on, btn_set_machine_off, btn_reset_note;
//    private String type = "";
//    private Spinner spinner_degree;
//    private EditText et_set_degree;
//    private ProgressBar progress;
//    private RadioGroup radio_group;
//    private int num = 0, isMain = 0;
//
//    private static byte[] hexStringToByteArray(String hexString) {
//        int len = hexString.length();
//        byte[] byteArray = new byte[len / 2];
//
//        for (int i = 0; i < len; i += 2) {
//            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
//                    + Character.digit(hexString.charAt(i + 1), 16));
//        }
//
//        return byteArray;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_machinesettinginfo);
//
//        tv_error = findViewById(R.id.tv_error);
//        progress = findViewById(R.id.progress);
//        et_set_degree = findViewById(R.id.et_set_degree);
/// /        tv_set_degree = findViewById(R.id.tv_set_degree);
/// /        tv_set_degree.setOnClickListener(new View.OnClickListener() {
/// /            @Override
/// /            public void onClick(View view) {
/// /                spinner_degree.performClick();
/// /            }
/// /        });
//        radio_group = findViewById(R.id.radio_group);
//        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
//                int radioBtnID = rGroup.getCheckedRadioButtonId();
//                View radioB = rGroup.findViewById(radioBtnID);
//                int position = rGroup.indexOfChild(radioB);
//                if (position == 1) {
//                    isMain = 0;
//                    startCommand("FE EF 00 00 04 00 19 00 05 00 03 00 00 6C 88");
//                    type = "serial_number";
//                } else {
//                    isMain = 1;
//                    startCommand("FE EF 01 00 01 00 19 00 05 00 03 00 00 E9 27");
//                    type = "serial_number";
//                }
//            }
//        });
//        tv_serial = findViewById(R.id.tv_serial);
//        tv_surrounding_wet = findViewById(R.id.tv_surrounding_wet);
//        tv_surrounding_degree = findViewById(R.id.tv_surrounding_degree);
//        tv_cloud_degree = findViewById(R.id.tv_cloud_degree);
//        tv_pressure = findViewById(R.id.tv_pressure);
//        tv_cloud = findViewById(R.id.tv_cloud);
//        tv_fan = findViewById(R.id.tv_fan);
//
//        spinner_degree();
//
//        btn_set_light_on = findViewById(R.id.btn_set_light_on);
//        btn_set_light_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
/// /                startCommand("FE EF 00 00 05 00 80 00 07 00 02 35 00 00 00 DC 93");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 35 00 00 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_light_off = findViewById(R.id.btn_set_light_off);
//        btn_set_light_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 06 00 80 00 07 00 02 35 00 01 00 17 D8");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x35, (byte) 0x00, (byte) 0x01, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 35 00 01 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_mist_on = findViewById(R.id.btn_set_mist_on);
//        btn_set_mist_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 07 00 80 00 07 00 02 37 00 00 00 07 DE");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x37, (byte) 0x00, (byte) 0x00, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 37 00 01 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "mist_on";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_mist_off = findViewById(R.id.btn_set_mist_off);
//        btn_set_mist_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 08 00 80 00 07 00 02 37 00 01 00 05 66");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x37, (byte) 0x00, (byte) 0x01, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 37 00 00 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "mist_off";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_machine_on = findViewById(R.id.btn_set_machine_on);
//        btn_set_machine_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 0A 00 80 00 07 00 02 36 00 00 00 33 83");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x36, (byte) 0x00, (byte) 0x00, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 36 00 01 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "machine_on";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_machine_off = findViewById(R.id.btn_set_machine_off);
//        btn_set_machine_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 0B 00 80 00 07 00 02 36 00 01 00 4B 68");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x36, (byte) 0x00, (byte) 0x01, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 36 00 00 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "machine_off";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_reset_note = findViewById(R.id.btn_reset_note);
//        btn_reset_note.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progress.setVisibility(View.VISIBLE);
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) 0x00,
//                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x82, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x02, (byte) 0xB4, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 00 00 00 00 82 00 05 00 02 B4 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//
//            }
//        });
//
//
//        btn_fan = findViewById(R.id.btn_fan);
//        btn_fan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 0A 00 80 00 05 00 03 55 00 1F BD");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x55, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 55 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "fan";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_set_degree = findViewById(R.id.btn_set_degree);
//        btn_set_degree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (et_set_degree.getText().toString().length() == 0) {
//                    //do nothing
//                    return;
//                }
//                try {
//                    int degreeint = Integer.parseInt(et_set_degree.getText().toString()) * 100;
//                    String degreeStr = decToHex16(degreeint);
//                    String degreeStr1 = degreeStr.toUpperCase();
//                    String degreeStr2 = hexconverttocrc(degreeStr1);
//
//                    String degreenum1 = degreeStr.toUpperCase().substring(2);
//                    String degreenum2 = degreeStr.toUpperCase().substring(0, 2);
//
//                    int intValue1 = Integer.parseInt(degreenum1, 16);
//                    int intValue2 = Integer.parseInt(degreenum2, 16);
//
//                    num = 1;
//                    byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                            (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                            (byte) 0x07, (byte) 0x00, (byte) 0x02, (byte) 0x30, (byte) 0x00, ((byte) (byte) intValue1), ((byte) (byte) intValue2)};
//                    int crcResult = crc16_CCITT(hexString, hexString.length);
//                    String results = String.format("0x%04X", crcResult);
//                    String results1 = results.substring(4, 6);
//                    String results2 = results.substring(2, 4);
//
//                    startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 07 00 02 30 00 " + degreenum1 + " " + degreenum2 + " " + results1 + " " + results2);
////                    startCommand("FE EF 00 00 10 00 80 00 07 00 02 30 00 " + degreeStr.toUpperCase().substring(2, degreeStr.toUpperCase().length()) + " "
////                            + degreeStr.toUpperCase().substring(0, 2) + " " + degreeStr2.substring(2, degreeStr2.length()) + " " +
////                            degreeStr2.substring(0, 2));
//                    type = "degree_set_on";
//                } catch (Exception ex) {
//                    Toast.makeText(MachineSettingInfo.this, "invalid format", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        btn_cloud = findViewById(R.id.btn_cloud);
//        btn_cloud.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 09 00 80 00 05 00 03 54 00 6A A3");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x54, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 54 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "cloud";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_pressure = findViewById(R.id.btn_pressure);
//        btn_pressure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 08 00 80 00 05 00 03 53 00 DE D1");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x53, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 53 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "pressure";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_cloud_degree = findViewById(R.id.btn_cloud_degree);
//        btn_cloud_degree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 07 00 80 00 05 00 03 52 00 BB 7A");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x52, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 52 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "cloud_degree";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//
//        btn_surrounding_degree = findViewById(R.id.btn_surrounding_degree);
//        btn_surrounding_degree.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 05 00 80 00 05 00 03 50 00 BE DA");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x50, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 50 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "surround_degree";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//        btn_surrounding_wet = findViewById(R.id.btn_surrounding_wet);
//        btn_surrounding_wet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                disableAll();
//                progress.setVisibility(View.VISIBLE);
////                startCommand("FE EF 00 00 04 00 80 00 05 00 03 51 00 AC 02");
//                byte[] hexString = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) ((byte) isMain),
//                        (byte) 0x00, (byte) ((byte) num), (byte) 0x00, (byte) 0x80, (byte) 0x00,
//                        (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x51, (byte) 0x00};
//                int crcResult = crc16_CCITT(hexString, hexString.length);
//                String results = String.format("0x%04X", crcResult);
//
//                startCommand("FE EF 0" + isMain + " 00 0" + num + " 00 80 00 05 00 03 51 00 " + results.substring(4, 6) + " " + results.substring(2, 4));
//                type = "surround_wet";
//                num++;
//                if (num > 9) {
//                    num = 0;
//                }
//            }
//        });
//        btn_Close = findViewById(R.id.btn_Close);
//        btn_Close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CloseComPort(ComA);
//                finish();
//            }
//        });
//
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
//        startCommand("FE EF 00 00 04 00 19 00 05 00 03 00 00 6C 88");
//        type = "serial_number";
//    }
//
//    private String moveLastTwoDigitsToDecimal(int value) {
//        int decimalPart = value % 100;
//        int integerPart = value / 100;
//
//        return integerPart + "." + decimalPart;
//    }
//
//    private void DispRecData(ComBean ComRecData) {
//        String response = MyFunc.ByteArrToHex(ComRecData.bRec);
//
//        String[] result = response.split(" ", -1);
//        try {
//            String checkError = result[10];
//            if (checkError.equalsIgnoreCase("00")) {
//                //error found
//                String errorCode = result[11];
//                switch (errorCode) {
//                    case "01":
//                        tv_error.setText("Restart Error");
//                        break;
//                    case "02":
//                        tv_error.setText("Reload Error");
//                        break;
//                    case "03":
//                        tv_error.setText("Stop Error");
//                        break;
//                    case "04":
//                        tv_error.setText("Set Param Error");
//                        break;
//                    case "05":
//                        tv_error.setText("Get Param Error");
//                        break;
//                    case "06":
//                        tv_error.setText("Update Error");
//                        break;
//                    case "07":
//                        tv_error.setText("Get Hardware Ver Error");
//                        break;
//                    case "08":
//                        tv_error.setText("Get Software Ver Error");
//                        break;
//                    case "09":
//                        tv_error.setText("Get S/N Error");
//                        break;
//                }
//                enableAllfunc();
//                return;
//            }
//            String newResult = result[15] + result[14];
//            int decimal = Integer.parseInt(newResult, 16);
//
//            if (type.equalsIgnoreCase("surround_wet")) {
//                tv_surrounding_wet.setText(decimal + " °C");
//            } else if (type.equalsIgnoreCase("surround_degree")) {
//                String degree = String.valueOf(decimal);
//                degree = degree.replace("0", "");
//                tv_surrounding_degree.setText(degree + " °C");
//            } else if (type.equalsIgnoreCase("cloud_degree")) {
//                String degree = String.valueOf(decimal);
//                degree = degree.replace("0", "");
//                if (degree.length() > 2) {
//                    String textint = moveLastTwoDigitsToDecimal(Integer.valueOf(degree));
//                    tv_cloud_degree.setText(textint + " °C");
//                } else {
//                    tv_cloud_degree.setText(degree + " °C");
//                }
//            } else if (type.equalsIgnoreCase("pressure") || type.equalsIgnoreCase("cloud") ||
//                    type.equalsIgnoreCase("fan")) {
//                try {
//                    int onoff = Integer.valueOf(result[14]);
//                    if (onoff == 0) {
//                        if (type.equalsIgnoreCase("pressure")) {
//                            tv_pressure.setText("Close");
//                        } else if (type.equalsIgnoreCase("cloud")) {
//                            tv_cloud.setText("Close");
//                        } else {
//                            tv_fan.setText("Close");
//                        }
//                    } else {
//                        if (type.equalsIgnoreCase("pressure")) {
//                            tv_pressure.setText("Open");
//                        } else if (type.equalsIgnoreCase("cloud")) {
//                            tv_cloud.setText("Open");
//                        } else {
//                            tv_fan.setText("Open");
//                        }
//                    }
//                } catch (Exception ex) {
//                }
//            } else if (type.equalsIgnoreCase("serial_number")) {
//                String text14 = textconvert(result[14]) + textconvert(result[15]) + textconvert(result[16]) + textconvert(result[17]) +
//                        textconvert(result[18]) + textconvert(result[19]) + textconvert(result[20]) + textconvert(result[21]) +
//                        textconvert(result[22]) + textconvert(result[23]) + textconvert(result[24]) + textconvert(result[25]) +
//                        textconvert(result[26]) + textconvert(result[27]) + textconvert(result[28]);
//                tv_serial.setText("S/N-" + text14);
//            }
//        } catch (IndexOutOfBoundsException ex) {
//        }
//        enableAllfunc();
//    }
//
//    private void enableAllfunc() {
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                handler.removeCallbacksAndMessages(null);
//                progress.setVisibility(View.GONE);
//                enableAll();
//            }
//        }, 2000);
//    }
//
//    private String textconvert(String text) {
//        text = text.substring(1);
//        return text;
//    }
//
//    public String decToHex16(int number) {
//        if (number < Short.MIN_VALUE || number > Short.MAX_VALUE) {
//            throw new IllegalArgumentException("Number is outside the range of a 16-bit signed integer.");
//        }
//
//        if (number < 0) {
//            number = 0xFFFF + 1 + number;
//        }
//
//        String hexRepresentation = String.format("%04x", number);
//
//        return hexRepresentation;
//    }
//
//    private void spinner_degree() {
//        spinner_degree = findViewById(R.id.spinner_degree);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.spinner_items_degree,
//                android.R.layout.simple_spinner_item
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner_degree.setAdapter(adapter);
//        spinner_degree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // Get the selected item
//                String selectedItem = parentView.getItemAtPosition(position).toString();
////                tv_set_degree.setText(selectedItem);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Do nothing here if nothing is selected
//            }
//        });
//    }
//
//    private String hexconverttocrc(String hexDegree) {
//
//        String firstd = hexDegree.substring(0, 2);
//        String secondd = hexDegree.substring(2);
//
//        byte[] byteArray = hexStringToByteArray(firstd);
//        byte[] byteArray1 = hexStringToByteArray(secondd);
//
//        byte test1 = 0, test2 = 0;
//        // Print the resulting byte array
//        for (byte b : byteArray) {
//            test1 = b;
//            System.out.printf("%02X ", b);
//        }
//
//        for (byte b : byteArray1) {
//            test2 = b;
//            System.out.printf("%02X ", b);
//        }
//
//        byte[] testdat = new byte[]{(byte) 0xFE, (byte) 0xEF, (byte) 0x00, (byte) 0x00,
//                (byte) 0x13, (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x07,
//                (byte) 0x00, (byte) 0x02, (byte) 0x30, (byte) 0x00, test1,
//                test2};
//        int test = crc16_CCITT(testdat, testdat.length);
//        String hex = intToHexStringWithLeadingZero(test);
//        return hex.toUpperCase();
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
//    private void enableAll() {
//        btn_Close.setEnabled(true);
//        btn_cloud.setEnabled(true);
//        btn_cloud_degree.setEnabled(true);
//        btn_fan.setEnabled(true);
//        btn_pressure.setEnabled(true);
//        btn_set_degree.setEnabled(true);
//        btn_set_machine_off.setEnabled(true);
//        btn_set_machine_on.setEnabled(true);
//        btn_set_mist_off.setEnabled(true);
//        btn_set_mist_on.setEnabled(true);
//        btn_surrounding_degree.setEnabled(true);
//        btn_surrounding_wet.setEnabled(true);
//    }
//
//    private void disableAll() {
//        btn_Close.setEnabled(false);
//        btn_cloud.setEnabled(false);
//        btn_cloud_degree.setEnabled(false);
//        btn_fan.setEnabled(false);
//        btn_pressure.setEnabled(false);
//        btn_set_degree.setEnabled(false);
//        btn_set_machine_off.setEnabled(false);
//        btn_set_machine_on.setEnabled(false);
//        btn_set_mist_off.setEnabled(false);
//        btn_set_mist_on.setEnabled(false);
//        btn_surrounding_degree.setEnabled(false);
//        btn_surrounding_wet.setEnabled(false);
//        tv_error.setText("");
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
//                    runOnUiThread(new Runnable() {
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
