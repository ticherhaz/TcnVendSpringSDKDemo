package com.tcn.sdk.springdemo.Note;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.tcn.sdk.springdemo.Dispense.AppLogger;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.PortsandVeriables;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CashNotePopUp {
    static CashNotePopUp instance;
    private static ITLDeviceComPopUp deviceCom;
    private static D2xxManager ftD2xx = null;
    private static FT_Device ftDev = null;
    private final String LPayment = "";
    private final String type = "";
    private final String paytype = "";
    private final boolean isuserpaying = false;
    private final boolean checkRunOneTimeOnly = false;
    private final String TAG = "IpaySummaryPopUp";
    private final long delay = 1000;
    private final long last_text_edit = 0;
    private final Handler handler = new Handler();
    public Dialog customDialogDispense;
    public SweetAlertDialog pDialog, globalDialog;
    private String fid;
    private String mid;
    private TypeProfuctActivity activity;
    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // never come here(when attached, go to onNewIntent)
                openDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
            }
        }
    };
    private String productsids = "";
    private UserObj userObj;
    private PortsandVeriables portsandVeriables;
    private TextView txtmtd, ttltxt;
    private Button backbtn;
    private String vids = "";
    private String mtd;
    private String merchantcode;
    private String merchantkey;
    private String payid;
    private String paystatus;
    private boolean isloggedin = false;
    private boolean isVocher = false;
    private EditText et;
    private double rrp = 0.00;
    private RequestQueue requestQueue;
    private boolean checkdisablecancel = false;

    public CashNotePopUp() {
        // private constructor to enforce singleton pattern
    }

    public static CashNotePopUp getInstance() {
        if (instance == null) {
            instance = new CashNotePopUp();
        }
        return instance;
    }

    private static void closeDevice() {

        if (deviceCom != null) {
            deviceCom.Stop();
        }
        if (ftDev != null) {
            ftDev.close();
        }
    }

    public static void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        if (!ftDev.isOpen()) {
            return;
        }

        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);
    }

    public void cashPopUp(UserObj userobj, TypeProfuctActivity activity, Dialog customDialogDispense, RequestQueue requestQueue) {
        checkdisablecancel = false;
        this.requestQueue = requestQueue;
        this.customDialogDispense = customDialogDispense;
        this.activity = activity;
        this.userObj = userobj;
        this.mtd = userobj.getMtd();

        RollingLogger.i(TAG, "Start Cash Payment Activity");

        SharedPref.init(activity);
        portsandVeriables = new PortsandVeriables();
        setupDialog();
        setupLayoutId();
        setUpValue();
        setupNote();
    }

    private void setupNote() {
        try {
            ftD2xx = D2xxManager.getInstance(activity);
        } catch (D2xxManager.D2xxException ex) {
            Log.e("SSP FTmanager", ex.toString());
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        activity.registerReceiver(mUsbReceiver, filter);


        deviceCom = new ITLDeviceComPopUp();
        openDevice();
    }

    private void setupDialog() {
        activity.clearCustomDialogDispense();
        customDialogDispense = activity.getCustomDialogDispense("cash");

        if (!activity.isFinishing()) {
            customDialogDispense.show();
        }
    }

    private void setupLayoutId() {
        txtmtd = customDialogDispense.findViewById(R.id.mtd);
        ttltxt = customDialogDispense.findViewById(R.id.pricetext);
        backbtn = customDialogDispense.findViewById(R.id.backbtn);
    }

    private void setUpValue() {
        for (CongifModel cn : userObj.getConfigModel()) {
            fid = cn.getFid();
            mid = cn.getMid();
            merchantcode = cn.getMerchantcode();
            merchantkey = cn.getMerchantkey();
        }
        for (CartListModel cart : userObj.getCartModel()) {
            RollingLogger.i(TAG, "cart item number-" + cart.getItemnumber());
            RollingLogger.i(TAG, "cart item qty-" + cart.getItemqty());
            RollingLogger.i(TAG, "cart item name-" + cart.getItemname());
            RollingLogger.i(TAG, "cart item price-" + cart.getItemprice());

            int qty;
            qty = Integer.parseInt(cart.getItemqty());
            for (int x = 0; x < qty; x++) {
                productsids += cart.getProdid() + ",";
            }
            if (isloggedin) {
                rrp += ((Double.parseDouble(cart.itemprice) * Double.parseDouble(cart.rrp_percent)) / 100);

                if (cart.getVoucher() != null) {
                    vids += cart.getVoucher() + ",";
                    isVocher = true;
                }
            }
        }
        isloggedin = userObj.getIsloggedin();

        RollingLogger.i(TAG, "TOTAL : RM " + String.format("%.2f", userObj.getChargingprice()));
        ttltxt.setText("TOTAL : RM " + String.format("%.2f", userObj.getChargingprice()));
        txtmtd.setText(mtd);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.finish();
                Button pay = activity.getpaybuttonenable();

                pay.setEnabled(true);
                activity.isPaymentinProgress(true);
                activity.clearCustomDialogDispense();
                activity.setEnableaddproduct(true);
                customDialogDispense.dismiss();
                RollingLogger.i(TAG, "back button clicked");
            }
        });
    }

    private void openDevice() {


        if (ftDev != null) {
            if (ftDev.isOpen()) {
                // if open and run thread is stopped, start thread
                SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
                ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
                ftDev.restartInTask();
                return;
            }
        }

        int devCount = 0;

        if (ftD2xx != null) {
            // Get the connected USB FTDI devoces
            devCount = ftD2xx.createDeviceInfoList(activity);
        } else {
            return;
        }

        D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
        ftD2xx.getDeviceInfoList(devCount, deviceList);
        // none connected
        if (devCount <= 0) {
            return;
        }
        if (ftDev == null) {
            ftDev = ftD2xx.openByIndex(activity, 0);
        } else {
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(activity, 0);
            }
        }
        // run thread
        if (ftDev.isOpen()) {
            SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }
    }

    public void testing() {

    }
}
