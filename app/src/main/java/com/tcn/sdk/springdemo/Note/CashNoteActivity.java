package com.tcn.sdk.springdemo.Note;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.TempTrans;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import device.itl.sspcoms.DeviceEvent;
import device.itl.sspcoms.ItlCurrency;
import device.itl.sspcoms.ItlCurrencyValue;
import device.itl.sspcoms.SSPDevice;
import device.itl.sspcoms.SSPDeviceType;
import device.itl.sspcoms.SSPPayoutEvent;
import device.itl.sspcoms.SSPUpdate;

public class CashNoteActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;
    private static final String m_DeviceCountry = null;
    private static final String TAG = "CashNoteActivity";
    static FloatingActionButton fab;
    static LinearLayout bvDisplay;
    static CashNoteActivity mainActivity;
    static ListView listChannels;
    static ListView listEvents;
    static Button bttnAccept;
    static Button bttnReject;
    static Switch swEscrow;
    static Button bttnPay;
    static Button bttnEmpty;
    static TextView txtFirmware;
    static TextView txtDevice;
    static TextView txtDataset;
    static TextView txtSerial;
    static LinearLayout lPayoutControl;
    static TextView txtPayoutStatus;
    static TextView txtConnect;
    static ProgressBar prgConnect;
    static Button bttnPayNext;
    static Button bttnStackNext;
    static ArrayList<HashMap<String, String>> list;
    static String[] pickerValues;
    static ProgressDialog progress;
    static List<String> channelValues;
    static String[] eventValues;
    static ArrayAdapter<String> adapterChannels;
    static ArrayAdapter<String> adapterEvents;
    static String productsids = "", noteone = "", notefive = "", noteten = "", notetwenty = "", notefifty = "", notehundred = "";
    private static ITLDeviceComPopUp deviceCom;
    private static SSPDevice sspDevice = null;
    private static CashNoteActivity instance = null;
    //static NumberPicker numPayAmount;
    private static D2xxManager ftD2xx = null;
    private static FT_Device ftDev = null;
    private static MenuItem downloadMenuItem;
    private static MenuItem storedBillMenuItem;
    private static TextView scantext, pricetext, balancetext, amounttext, outputtext; //added outputtext
    private static UserObj userObj;
    private static CashNoteActivity activity;
    private static String price;
    private static Handler handler;
    private static CountDownTimer cTimer = null;
    private static SweetAlertDialog sweetAlertDialog, sweetAlertDialogOK, sweetAlertDialogBack, sweetAlertDialogCancel;
    private static int count = 0, total = 0, stackint = 0, storeint = 0, refundint = 0, dispensedint = 0;
    private static String fid = "";
    private static String mid = "";
    private static boolean refundboo = false;
    private static RequestQueue requestQueue;
    private static List<CongifModel> congifModels;
    private static List<CartListModel> cartListModelList;
    private static int totalintnew = 0;
    /**********   USB functions   ******************************************/


    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                // never come here(when attached, go to onNewIntent)
                openDevice();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                closeDevice();
//                bvDisplay.setVisibility(View.INVISIBLE);
//                fab.setVisibility(View.VISIBLE);
//                fab.setEnabled(true);
            }
        }
    };
    private SSPUpdate sspUpdate = null;
    private Intent intent;

    public static CashNoteActivity getInstance() {
        return instance;
    }


    public static void DisplaySetUp(SSPDevice dev) {
        // set this instance device object
        sspDevice = dev;

        // check for type comparable
        if (dev.type != SSPDeviceType.NoteFloat) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CashNoteActivity.getInstance());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Connected device is not Note Float (" + dev.type.toString() + ")")
                    .setTitle("BNV");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getInstance().finish();
                }
            });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            // 4. Show the dialog
            dialog.show();// show error
            outputtext.setText("SSPDeviceType problem");
            return;

        }

        outputtext.setText("displaysetupProblem");
    }


    private static void DisplayChannels() {

        /* display the channel info */
        list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hdr = new HashMap<String, String>();
        hdr.put(Constants.FIRST_COLUMN, "Code");
        hdr.put(Constants.SECOND_COLUMN, "Value");
        hdr.put(Constants.THIRD_COLUMN, "Stored");
        hdr.put(Constants.FOURTH_COLUMN, "Route");
        double totalValue = 0.0;
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put(Constants.FIRST_COLUMN, itlCurrency.country);
            temp.put(Constants.SECOND_COLUMN, String.format("%.2f", itlCurrency.realvalue));
            temp.put(Constants.THIRD_COLUMN, String.valueOf(itlCurrency.storedLevel));
            double vl = itlCurrency.realvalue * (double) itlCurrency.storedLevel;
            temp.put(Constants.FOURTH_COLUMN, itlCurrency.route.toString());
            list.add(temp);
            totalValue += vl;
        }
        HashMap<String, String> tot = new HashMap<String, String>();
        tot.put(Constants.FIRST_COLUMN, "");
        tot.put(Constants.SECOND_COLUMN, "Total");
        tot.put(Constants.THIRD_COLUMN, String.format("%.2f", totalValue));
        tot.put(Constants.FOURTH_COLUMN, "");
        list.add(tot);

        ListViewAdapter adapter = new ListViewAdapter(CashNoteActivity.getInstance(), list);
        listChannels.setAdapter(adapter);


        /*

        // update the picker values
        if (sspDevice.minPayout > 0 && sspDevice.minPayout != -1) {
            int intervalCount = sspDevice.storedPayoutValue / sspDevice.minPayout;
            pickerValues = new String[intervalCount];
            for (int i = 1; i <= intervalCount; i++) {
                String number = Integer.toString(i * (sspDevice.minPayout / 100));
                pickerValues[i - 1] = number;
            }
        }*/

        // update the picker values
        if (sspDevice.minPayout > 0 && sspDevice.minPayout != -1) {
            int intervalCount = sspDevice.storedPayoutValue / sspDevice.minPayout;
            int ind = 0;
            int[] vl = new int[intervalCount];
            // build the picker interval values
            for (int i = 1; i <= intervalCount; i++) {
                // check if this payout is possible
                if (sspDevice.IsSPPayoutPossible(sspDevice.shortDatasetVersion, i * (sspDevice.minPayout))) {
                    vl[ind++] = i * (sspDevice.minPayout / 100);
                }
            }
            if (ind > 0) {
                // all valid picker values
                pickerValues = new String[ind];
                for (int i = 0; i < ind; i++) {
                    pickerValues[i] = Integer.toString(vl[i]);
                }
            }

        }


        if (sspDevice.storedPayoutValue > 0) {
            bttnPay.setEnabled(true);
            bttnEmpty.setEnabled(true);
            bttnPayNext.setEnabled(true);
            bttnStackNext.setEnabled(true);
        } else {
            bttnEmpty.setEnabled(false);
            bttnPay.setEnabled(false);
            bttnStackNext.setEnabled(false);
            bttnPayNext.setEnabled(false);
        }


    }


    public static void DisplayEvents(DeviceEvent ev) {
        handlerReset();
        cancelTimer();
        resetTimer();
        if (sweetAlertDialog != null) {
            if (!activity.isFinishing()) {
                if (sweetAlertDialog.isShowing()) {
                    sweetAlertDialog.dismiss();
                }
            }
        }

        //test display
        outputtext.setText("DisplayChannel got in");
        //test display

        switch (ev.event) {
            case CommunicationsFailure:
                Toast.makeText(getInstance(), "Device coms Failure " + ev.currency, Toast.LENGTH_SHORT).show();
                break;
            case Ready:
                RollingLogger.i(TAG, "Ready");
                eventValues[0] = "Ready";
                eventValues[1] = "";
                break;
            case BillRead:
//                txtPayoutStatus.setText("");
                RollingLogger.i(TAG, "Reading");
                eventValues[0] = "Reading";
                eventValues[1] = "";
                break;
            case BillEscrow:
                eventValues[0] = "Bill Escrow";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case BillStacked:
                RollingLogger.i(TAG, "Bill Stacked " + (int) ev.value + ".00");
                eventValues[0] = "Bill Stacked";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";

//                lPayoutControl.setVisibility(View.VISIBLE);
                stackint = stackint + (int) ev.value;
                amounttext.setText("Paid : RM " + stackint + ".00");
                if (count == 0) {
                    price = price.replace(".00", "");
                    int priceInt = Integer.parseInt(price);
                    total = priceInt - (int) ev.value;
                    count++;
                } else {
                    total = total - (int) ev.value;
                }

                if (total == 0) {
                    dispenseStart(activity);
                } else {
                    price = price.replace(".00", "");
                    int priceInt = Integer.parseInt(price);
                    pricetext.setText("Total : RM " + priceInt + ".00");
                }
                if (total < 0) {
                    balancetext.setVisibility(View.VISIBLE);
                    total = balanceTotal(total);
                }
                break;
            case BillReject:
                RollingLogger.i(TAG, "Bill Reject " + (int) ev.value + ".00");
                eventValues[0] = "Bill Reject";
                eventValues[1] = "";
//                if (swEscrow.isChecked()) {
//                    bttnAccept.setVisibility(View.INVISIBLE);
//                    bttnReject.setVisibility(View.INVISIBLE);
//                }
                break;
            case BillJammed:
                RollingLogger.i(TAG, "Bill jammed " + (int) ev.value + ".00");
                eventValues[0] = "Bill jammed";
                eventValues[1] = "";
                break;
            case BillFraud:
                RollingLogger.i(TAG, "Bill Fraud " + (int) ev.value + ".00");
                eventValues[0] = "Bill Fraud";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case BillCredit:
                RollingLogger.i(TAG, "Bill Credit " + (int) ev.value + ".00");
                eventValues[0] = "Bill Credit";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case Full:
                RollingLogger.i(TAG, "Bill Cashbox full");
                eventValues[0] = "Bill Cashbox full";
                eventValues[1] = "";
                break;
            case Initialising:

                break;
            case Disabled:
                RollingLogger.i(TAG, "Disbled");
                eventValues[0] = "Disabled";
                eventValues[1] = "";
                break;
            case SoftwareError:
                RollingLogger.i(TAG, "Software error");
                eventValues[0] = "Software error";
                eventValues[1] = "";
                break;
            case AllDisabled:
                eventValues[0] = "All channels disabled";
                eventValues[1] = "";
                break;
            case CashboxRemoved:
                eventValues[0] = "Cashbox removed";
                eventValues[1] = "";
                break;
            case CashboxReplaced:
                eventValues[0] = "Cashbox replaced";
                eventValues[1] = "";
                break;
            case NotePathOpen:
                eventValues[0] = "Note path open";
                eventValues[1] = "";
                break;
            case BarCodeTicketEscrow:
                eventValues[0] = "Barcode ticket escrow:";
                eventValues[1] = ev.currency;
                break;
            case BarCodeTicketStacked:
                eventValues[0] = "Barcode ticket stacked";
                eventValues[1] = "";
                break;
            case BillStoredInPayout:
                RollingLogger.i(TAG, "Bill Stored in payout " + (int) ev.value + ".00");
                eventValues[0] = "Bill Stored in payout";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";

                storeint = storeint + (int) ev.value;
                amounttext.setText("Paid : RM " + storeint + ".00");
                if (count == 0) {
                    price = price.replace(".00", "");
                    int priceInt = Integer.parseInt(price);
                    total = priceInt - (int) ev.value;
                    count++;
                } else {
                    total = total - (int) ev.value;
                }

                if (total == 0) {
                    dispenseStart(activity);
                } else {
                    price = price.replace(".00", "");
                    int priceInt = Integer.parseInt(price);
                    pricetext.setText("Total : RM " + priceInt + ".00");
                }
                if (total < 0) {
                    balancetext.setVisibility(View.VISIBLE);
                    total = balanceTotal(total);
                }
                break;
            case PayoutOutOfService:
                eventValues[0] = "Payout out of service!";
                eventValues[1] = "";
                break;
            case Dispensing:
//                RollingLogger.i(TAG, "Bill dispensing "+String.valueOf((int) ev.value) + ".00");
                eventValues[0] = "Bill dispensing";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case Dispensed:
                RollingLogger.i(TAG, "Bill Dispensed " + (int) ev.value + ".00");
                eventValues[0] = "Bill Dispensed";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";

                if (!refundboo) {
                    dispensedint = dispensedint + (int) ev.value;
                    ItlCurrency curpay = new ItlCurrency();
                    curpay.country = m_DeviceCountry;
                    curpay.value = 100;
                    deviceCom.PayoutAmount(curpay);

                    balancetext.setText("Change : RM " + Math.abs(totalintnew) + ".00");
                    if (totalintnew == 0) {
                        dispenseStart(activity);
                    }
                    totalintnew = totalintnew + 1;
                } else {
                    refundint = refundint - 1;
                    dispensedint = dispensedint + (int) ev.value;
                    if (refundint == 0) {
                        //clear
                        TempTrans(2);
                        closeDevice();
                        dismissDialog();
                        handlerReset();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        ItlCurrency curpay = new ItlCurrency();
                        curpay.country = m_DeviceCountry;
                        curpay.value = 100;
                        deviceCom.PayoutAmount(curpay);
                    }
                }
                break;
            case Emptying:
                eventValues[0] = "Payout emptying...";
                eventValues[1] = "";
                break;
            case Emptied:
                eventValues[0] = "Payout emptied";
                eventValues[1] = "";
                break;
            case SmartEmptying:
                eventValues[0] = "Payout emptying...";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case SmartEmptied:
                eventValues[0] = "Payout emptied";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case BillTransferedToStacker:
                eventValues[0] = "Stacker transfer";
                eventValues[1] = ev.currency + " " +
                        (int) ev.value + ".00";
                break;
            case BillHeldInBezel:
                break;
            case BillInStoreAtReset:
                break;
            case BillInStackerAtReset:
                break;
            case BillDispensedAtReset:
                break;
            case NoteFloatRemoved:
                eventValues[0] = "NF detatched";
                eventValues[1] = "";
                break;
            case NoteFloatAttached:
                eventValues[0] = "NF attached";
                eventValues[1] = "";
                break;
            case DeviceFull:
                eventValues[0] = "Payout Device Full";
                eventValues[1] = "";
                break;
            case RefillBillCredit:
                break;

        }
        StringBuilder combinedString = new StringBuilder();

        for (String s : eventValues) {
            combinedString.append(s);
        }

        String result = combinedString.toString();
        scantext.setText(result);

    }

    private static int balanceTotal(int totalint) {
//        pricetext.setVisibility(View.INVISIBLE);
//        balancetext.setVisibility(View.VISIBLE);
        totalintnew = totalint;

        ItlCurrency curpay = new ItlCurrency();
        curpay.country = m_DeviceCountry;
        curpay.value = 100;
        deviceCom.PayoutAmount(curpay);

        balancetext.setText("Change : RM " + Math.abs(totalintnew) + ".00");
        if (totalintnew == 0) {
            dispenseStart(activity);
        }
        totalintnew = totalintnew + 1;

        return totalintnew;
    }

    private static void refundStart() {

        ItlCurrency curpay = new ItlCurrency();
        curpay.country = m_DeviceCountry;
        curpay.value = 100;
        deviceCom.PayoutAmount(curpay);

    }

    public static void DisplayPayoutEvents(SSPPayoutEvent ev) {

        String pd = null;

        switch (ev.event) {
            case CashPaidOut:
                pd = "Paying " + ev.country + " " + String.format("%.2f", ev.realvalue) + " of " +
                        " " + ev.country + " " + String.format("%.2f", ev.realvalueRequested);
//                txtPayoutStatus.setText(pd);
//                DisplayChannels();
                break;
            case CashStoreInPayout:
//                DisplayChannels();
                break;
            case CashLevelsChanged:
//                DisplayChannels();
                break;
            case PayoutStarted:
                pd = "Request " + ev.country + " " + String.format("%.2f", ev.realvalue) + " of " +
                        " " + ev.country + " " + String.format("%.2f", ev.realvalueRequested);
//                txtPayoutStatus.setText(pd);
//                lPayoutControl.setVisibility(View.INVISIBLE);
                break;
            case PayoutEnded:
                pd = "Paid " + ev.country + " " + String.format("%.2f", ev.realvalue) + " of " +
                        " " + ev.country + " " + String.format("%.2f", ev.realvalueRequested);
//                txtPayoutStatus.setText(pd);
//                lPayoutControl.setVisibility(View.VISIBLE);
                break;
            case PayinStarted:
//                lPayoutControl.setVisibility(View.INVISIBLE);
                break;
            case PayinEnded:
//                lPayoutControl.setVisibility(View.VISIBLE);
                break;
            case EmptyStarted:
//                txtPayoutStatus.setText("");
//                lPayoutControl.setVisibility(View.INVISIBLE);
                Toast.makeText(getInstance(), "Empty started", Toast.LENGTH_SHORT).show();
                break;
            case EmptyEnded:
//                txtPayoutStatus.setText("");
//                lPayoutControl.setVisibility(View.VISIBLE);
                Toast.makeText(getInstance(), "Empty ended", Toast.LENGTH_SHORT).show();
                break;
            case PayoutConfigurationFail:
                //TODO handle config failures
                break;
            case PayoutAmountInvalid:
                RollingLogger.i(TAG, "Payout request invalid amount " + ev.value + ".00");
                eventValues[0] = "Payout request invalid amount";
                eventValues[1] = ev.country + " " + ev.value;
                closeDevice();
                TempTrans(0);
//                adapterEvents.notifyDataSetChanged();
                Toast.makeText(getInstance(), "Payout amount request invalid", Toast.LENGTH_SHORT).show();
                sweetAlertDialogOK = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialogOK.setTitleText("Payout request invalid amount");
                sweetAlertDialogOK.setContentText("Please contact careline");
                sweetAlertDialogOK.show();
                sweetAlertDialogOK.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
//                        closeDevice();
//                        cancelTimer();
////                        dismissDialog();
////                        handlerReset();
////                        Intent intent = new Intent(activity, MainActivity.class);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                        activity.startActivity(intent);
////                        activity.finish();
                        dispenseStart(activity);
                    }
                });
                break;
            case PayoutRequestFail:
                //TODO handle this
                break;
            case PayStackerBillStarted:
                pd = "Stacking " + ev.country + " " + String.format("%.2f", ev.realvalue) + " of " +
                        " " + ev.country + " " + String.format("%.2f", ev.realvalueRequested);
//                txtPayoutStatus.setText(pd);
//                lPayoutControl.setVisibility(View.INVISIBLE);
                break;
            case PayStackerBillEnded:

                break;
            case RouteChanged:
//                DisplayChannels();
                break;
            case PayoutDeviceNotConnected:
                eventValues[0] = "Payout device not connected!";
                eventValues[1] = "";
//                adapterEvents.notifyDataSetChanged();
                Toast.makeText(getInstance(), "Payout device not connected", Toast.LENGTH_SHORT).show();
                break;
            case PayoutDeviceEmpty:
                eventValues[0] = "Payout device is empty!";
                eventValues[1] = "";
//                adapterEvents.notifyDataSetChanged();
                Toast.makeText(getInstance(), "Payout device empty", Toast.LENGTH_SHORT).show();
                break;
            case PayoutDeviceDisabled:
                eventValues[0] = "Payout device is disabled";
                eventValues[1] = "";
//                adapterEvents.notifyDataSetChanged();
                Toast.makeText(getInstance(), "Payout device disabled", Toast.LENGTH_SHORT).show();
                break;

        }

        StringBuilder combinedString = new StringBuilder();

        for (String s : eventValues) {
            combinedString.append(s);
        }

        String result = combinedString.toString();
        scantext.setText(result);
    }

    public static void DeviceDisconnected(SSPDevice dev) {

        eventValues[0] = "DISCONNECTED!!!";
        eventValues[1] = "";
//        adapterEvents.notifyDataSetChanged();

    }

    public static void UpdateFileDownload(SSPUpdate sspUpdate) {


        switch (sspUpdate.UpdateStatus) {
            case dwnInitialise:
                progress.setMessage("Downloading Ram");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(false);
                progress.setProgress(0);
                progress.setMax(sspUpdate.numberOfRamBlocks);
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                break;
            case dwnRamCode:
                progress.setProgress(sspUpdate.blockIndex);
                break;
            case dwnMainCode:
                progress.setMessage("Downloading flash");
                progress.setMax(sspUpdate.numberOfBlocks);
                progress.setProgress(sspUpdate.blockIndex);
                break;
            case dwnComplete:
                progress.dismiss();
                break;
            case dwnError:
                progress.dismiss();
                break;
        }


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

    private static void dispenseStart(CashNoteActivity context) {
        cancelTimer();
        handlerReset();
        dismissDialog();
        closeDevice();

        SimpleDateFormat apiDateTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
        String traceNo = apiDateTimeFormat.format(new Date());

        String versionName = "";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int showInt = 0;
        if (stackint > 0) {
            showInt = stackint;
        } else {
            showInt = storeint;
        }
        userObj.setMtd(userObj.getMtd() + " (Rcv " + showInt + ".00 Rtn " + dispensedint + ".00 Bal " + getTotal() + ") " + versionName);

        CardView cardView5 = context.findViewById(R.id.cardView5);
        cardView5.setVisibility(View.INVISIBLE);
//        DispensePopUpCashM5 dispensePopUpCashM5 = new DispensePopUpCashM5();
//        dispensePopUpCashM5.DispensePopUp(context, userObj, "success", "", null);
    }

    private static void resetTimer() {
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handlerReset();
                startTimer();
            }
        }, 60000);
    }

    private static void showsweetalerttimeout() {
        sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
        sweetAlertDialog.setContentText("This session will end in 10");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                cancelTimer();
                handlerReset();
                sweetAlertDialog.dismissWithAnimation();
                resetTimer();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissDialog();
                sweetAlertDialogCanncelDialog();
//                closeDevice();
//                cancelTimer();
//                dismissDialog();
//                handlerReset();
//                Intent intent = new Intent(activity, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                activity.startActivity(intent);
//                activity.finish();
            }
        });

//        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
////                sweetAlertDialog.dismissWithAnimation();
////                handlerReset();
////                closeDevice();
////                cancelTimer();
//                dismissDialog();
//                sweetAlertDialogCanncelDialog();
//            }
//        });
        if (!activity.isFinishing()) {
            if (sweetAlertDialogOK != null) {
                if (sweetAlertDialogOK.isShowing()) {
                    sweetAlertDialogOK.dismiss();
                }
            }
            sweetAlertDialog.show();
        }

    }

    private static void showsweetalertBack() {
        sweetAlertDialogBack = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialogBack.setTitleText("Are you sure?");
        sweetAlertDialogBack.setContentText("Are you sure to cancel this order?");
        sweetAlertDialogBack.setConfirmButton("No", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //continue
                sweetAlertDialogBack.dismiss();
            }
        });

        sweetAlertDialogBack.setCancelButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //refund

                dismissDialog();
                sweetAlertDialogCanncelDialog();
            }
        });

        sweetAlertDialogBack.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //nothing
                sweetAlertDialogBack.dismiss();
            }
        });
        if (!activity.isFinishing()) {
            if (sweetAlertDialogBack != null) {
                if (sweetAlertDialogBack.isShowing()) {
                    sweetAlertDialogBack.dismiss();
                }
            }
            sweetAlertDialogBack.show();
        }

    }

    private static void dismissDialog() {
        if (sweetAlertDialog != null) {
            if (sweetAlertDialog.isShowing()) {
                sweetAlertDialog.dismiss();
            }
        }
        if (sweetAlertDialogOK != null) {
            if (sweetAlertDialogOK.isShowing()) {
                sweetAlertDialogOK.dismiss();
            }
        }
        if (sweetAlertDialogBack != null) {
            if (sweetAlertDialogBack.isShowing()) {
                sweetAlertDialogBack.dismiss();
            }
        }
        if (sweetAlertDialogCancel != null) {
            if (sweetAlertDialogCancel.isShowing()) {
                sweetAlertDialogCancel.dismiss();
            }
        }
    }

    static void startTimer() {
        showsweetalerttimeout();
        cTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
            }

            public void onFinish() {
//                if(stackint==0 && storeint==0) {
//                    closeDevice();
//                    cancelTimer();
//                    dismissDialog();
//                    handlerReset();
//                    Intent intent = new Intent(activity, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    activity.startActivity(intent);
//                    activity.finish();
//                }else{
                dismissDialog();
                sweetAlertDialogCanncelDialog();
//                }
            }
        };
        cTimer.start();
    }

    static void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    static void handlerReset() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private static void sweetAlertDialogCanncelDialog() {
        if (stackint == 0 && storeint == 0) {
            //return
            closeDevice();
            dismissDialog();
            cancelTimer();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
            return;
        }
        refundboo = true;
        if (stackint > 0) {
            refundint = stackint;
        } else {
            refundint = storeint;
        }
        refundStart();

        sweetAlertDialogCancel = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialogCancel.setTitleText("Order Cancelled");
        sweetAlertDialogCancel.setContentText("Please collect your cash");
        sweetAlertDialogCancel.show();
        sweetAlertDialogCancel.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
    }

    private static void TempTrans(int paymentStatus) {

        try {
            RollingLogger.i(TAG, "temp api call start");

            String versionName = "";
            try {
                versionName = activity.getPackageManager()
                        .getPackageInfo(activity.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            congifModels = userObj.configModel;

            for (CongifModel cn : congifModels) {
                fid = cn.getFid();
                mid = cn.getMid();
            }

            int showInt = 0;
            if (stackint > 0) {
                showInt = stackint;
            } else {
                showInt = storeint;
            }
            String mtd = userObj.getMtd() + " (Rcv " + showInt + ".00 Rtn " + dispensedint + ".00 Bal " + getTotal() + ") " + versionName;

            cartListModelList = userObj.cartModel;
            for (CartListModel cart : cartListModelList) {
                int qty;
                qty = Integer.parseInt(cart.getItemqty());
                for (int x = 0; x < qty; x++) {
                    productsids += cart.getProdid() + ",";
                }
            }

            Date currentTime = Calendar.getInstance().getTime();

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(activity);
            }
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = "https://vendingappapi.azurewebsites.net/Api/" + "TempTrans";
            TempTrans transactionModel = new TempTrans();
            transactionModel.setAmount(userObj.getChargingprice());
            transactionModel.setTransDate(currentTime);
            transactionModel.setUserID(userObj.getUserid());
            transactionModel.setFranID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductIDs(productsids);
            transactionModel.setPaymentType(mtd);
            transactionModel.setPaymentMethod("Cash payment");
            transactionModel.setPaymentStatus(paymentStatus);
            transactionModel.setFreePoints("");
            transactionModel.setPromocode(userObj.getPromname());
            transactionModel.setPromoAmt(userObj.getPromoamt() + "");
            transactionModel.setVouchers("");
            transactionModel.setPaymentStatusDes("");


            try {
                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONObject finalJsonParam = jsonParam;

            JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST, url, jsonParam, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    System.out.println(response.toString());
                    RollingLogger.i(TAG, "temp api call response-" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    RollingLogger.i(TAG, "temp api call error-" + error.toString());
                }
            });
            // pDialog.dismissWithAnimation();

            requestQueue.add(myReq);
        } catch (Exception ex) {

        }
    }

    private static String getTotal() {
        double totalValue = 0.0;
        for (ItlCurrency itlCurrency : sspDevice.currency) {
            double vl = itlCurrency.realvalue * (double) itlCurrency.storedLevel;
            totalValue += vl;
        }
        return String.format("%.2f", totalValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);

        SharedPref.init(this);
        noteone = SharedPref.read(SharedPref.noteone, "");
        notefive = SharedPref.read(SharedPref.notefive, "");
        noteten = SharedPref.read(SharedPref.noteten, "");
        notetwenty = SharedPref.read(SharedPref.notetwenty, "");
        notefifty = SharedPref.read(SharedPref.notefifty, "");
        notehundred = SharedPref.read(SharedPref.nitehundred, "");

        requestQueue = null;
        congifModels = new ArrayList<>();
        cartListModelList = new ArrayList<>();
        productsids = "";
        RollingLogger.i(TAG, "CashNoteActivity start");
        activity = this;
        Button backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "back button clicked");
                if (sweetAlertDialog != null) {
                    if (sweetAlertDialog.isShowing()) {
                        sweetAlertDialog.dismiss();
                    }
                }
                if (sweetAlertDialogBack != null) {
                    if (sweetAlertDialogBack.isShowing()) {
                        sweetAlertDialogBack.dismiss();
                    }
                }
                if (stackint == 0 && storeint == 0) {
                    closeDevice();
                    dismissDialog();
                    cancelTimer();
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                    return;
                }
                showsweetalertBack();

            }
        });
        Button refres = findViewById(R.id.refresh);
        refres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "refresh button clicked");
                outputtext.setText("" + ftDev);

            }
        });
        dispensedint = 0;
        refundboo = false;
        refundint = 0;
        count = 0;
        total = 0;
        stackint = 0;
        storeint = 0;
        cTimer = null;
        sweetAlertDialog = null;
        sweetAlertDialogOK = null;
        sweetAlertDialogBack = null;
        sweetAlertDialogCancel = null;
        handler = null;
        mainActivity = this;
        instance = this;
        intent = getIntent();
        price = intent.getStringExtra("price");
        String jsonString = intent.getStringExtra("obj");
        Gson gson = new Gson();
        userObj = gson.fromJson(jsonString, UserObj.class);

        amounttext = findViewById(R.id.amounttext);
        pricetext = findViewById(R.id.pricetext);
        scantext = findViewById(R.id.scantext);
        pricetext.setText("Total : RM " + price);
        balancetext = findViewById(R.id.balancetext);

        //testing output
        outputtext = findViewById(R.id.outputTest);

        outputtext.setText("status: ");
        //testing end

        eventValues = new String[]{"", ""};
        channelValues = new ArrayList<String>();

        try {
            ftD2xx = D2xxManager.getInstance(this);
        } catch (D2xxManager.D2xxException ex) {
            Log.e("SSP FTmanager", ex.toString());
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);

        resetTimer();
        deviceCom = new ITLDeviceComPopUp();
        openDevice();
        if (ftDev != null) {
            // setup to use essp
            deviceCom.setup(ftDev, 0, false, true, 0x0123456701234567L);
            deviceCom.start();
            outputtext.setText("usb connection ok");
        } else {
            Toast.makeText(CashNoteActivity.this, "No USB connection detected!", Toast.LENGTH_SHORT).show();
            outputtext.append("usb connection not connected ");
        }
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        this.unregisterReceiver(mUsbReceiver);
        handlerReset();
        closeDevice();
        dismissDialog();
        super.onDestroy();
    }

    /**
     * Start a new activity to display a list of the stored bills in the note float
     */
    private void ShowNFBills() {

        Intent intent = new Intent(this, ListBills.class);
        ArrayList<String> strList = new ArrayList<>();
        ArrayList<ItlCurrencyValue> cvals = deviceCom.GetBillPositions();
        int i = 0;
        // create strin garray to pass to intent
        for (ItlCurrencyValue cval : cvals
        ) {
            String nv = i + 1 + " " + cval.country + " " + String.format("%.2f", cval.realValue);
            strList.add(nv);
            i++;
        }
        intent.putStringArrayListExtra("NF_Bills", strList);
        startActivity(intent);

    }

    /***
     *  Handler for selecting a download file
     *  All download files need to be in the Download folder
     */
    public void openFolder() {

        if (deviceCom == null) {
            return;
        }

        int devcode = deviceCom.GetDeviceCode();
        if (devcode < 0) {
            return;
        }

        Intent intent = new Intent(this, ListFiles.class);
        // send the current device code
        intent.putExtra("deviceCode", (byte) devcode);
        startActivityForResult(intent, 123);


    }

    //The select file screen returns to here with a selected file string
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 123 && resultCode == RESULT_OK) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

            path += "/";
            String flname = "";

            if (data.hasExtra("filename")) {
                flname = data.getStringExtra("filename");
                path += flname;

            } else {
                txtDevice.setText(R.string.no_file_data_error);
                return;
            }


            sspUpdate = new SSPUpdate(flname);
            try {
                final File up = new File(path);

                sspUpdate.fileData = new byte[(int) up.length()];
                DataInputStream dis = new DataInputStream(new FileInputStream(up));
                dis.readFully(sspUpdate.fileData);
                dis.close();

                sspUpdate.SetFileData();
                ClearDisplay();
                deviceCom.SetSSPDownload(sspUpdate);


            } catch (IOException e) {
                e.printStackTrace();
                //   txtEvents.append(R.string.unable_to_load + "\r\n");
            }
        }
    }

    private void ClearDisplay() {
        progress.setProgress(0);
        txtFirmware.setText(getResources().getString(R.string.firmware_title));
        txtDevice.setText(getResources().getString(R.string.device_title));
        txtDataset.setText(getResources().getString(R.string.dataset_title));
        txtSerial.setText(getResources().getString(R.string.serial_number_title));

        adapterChannels.clear();
        adapterChannels.notifyDataSetChanged();

        eventValues[0] = "";
        eventValues[1] = "";

        adapterEvents.notifyDataSetChanged();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            devCount = ftD2xx.createDeviceInfoList(this);
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
            ftDev = ftD2xx.openByIndex(this, 0);
        } else {
            synchronized (ftDev) {
                ftDev = ftD2xx.openByIndex(this, 0);
            }
        }
        // run thread
        if (ftDev.isOpen()) {
            SetConfig(9600, (byte) 8, (byte) 2, (byte) 0, (byte) 0);
            ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
            ftDev.restartInTask();
        }
    }
}
