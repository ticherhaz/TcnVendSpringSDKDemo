package com.tcn.sdk.springdemo.tcnSpring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcn.sdk.springdemo.Dispense.CartListAdapter2025;
import com.tcn.sdk.springdemo.Dispense.countObj;
import com.tcn.sdk.springdemo.MainActivity;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.Pointsend;
import com.tcn.sdk.springdemo.Model.TransactionModel;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.ys.springboard.control.PayMethod;
import com.ys.springboard.control.TcnVendEventResultID;
import com.ys.springboard.control.TcnVendIF;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActDispenseM4 extends AppCompatActivity {

    private static final String TAG = "MainAct";
    private final LoadingDialog m_LoadingDialog = null;
    private final int userstatus = 0;
//    private VendListener m_vendListener = new VendListener();
private OutDialog m_OutDialog = null;
    private int userid = 0;
    private int pid = 0;
    private int countItem = 0;
    private int productId = 0;
    private int countProduct = 1;
    private String productsids = "", paytype = "", fid = "", mid = "", mtd = "", payid = "", paystatus = "", allstatuses = "";
    private double chargingprice = 0.00, points = 0.00, newpoints = 0.00;
    private Boolean isloggedin = false, checkPopUp = false;
    private List<CongifModel> congifModels;
    private ArrayList<CartListModel> cartListModels;
    private List<countObj> arr_count;
    private CartListAdapter2025 adapter;
    private RecyclerView recyclerViewCart;
    private SweetAlertDialog sdthankyou, sdError;
    private RequestQueue requestQueue;
    private Handler checkErrorHandler, handlerQuit;
    private String transactionNo;

    private TcnVendIF.VendEventListener listener = event -> {
        TcnVendIF.getInstance().LoggerInfoForce(TAG, "eventId : " + event.m_iEventID + " param1: " + event.m_lParam1 +
                " param2: " + event.m_lParam2 + " param3: " + event.m_lParam3 + " param4: " + event.m_lParam5);

        if (event.m_lParam3 == TcnVendEventResultID.SHIP_SHIPING) {
            checkErrorHandler = new Handler(Looper.getMainLooper());
            checkErrorHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start to update data to database
                    handleCallApiControlBoardError();

                    checkErrorHandler.removeCallbacksAndMessages(null);
                    sdError = new SweetAlertDialog(MainActDispenseM4.this, SweetAlertDialog.ERROR_TYPE);
                    sdError.setTitleText("Error.");
                    sdError.setContentText("Control Board Error. Please contact careline");
                    sdError.setCanceledOnTouchOutside(false);
                    sdError.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (sdError != null) {
                                if (sdError.isShowing()) {
                                    sdError.dismiss();
                                }
                            }
                            if (handlerQuit != null) {
                                handlerQuit.removeCallbacksAndMessages(null);
                            }
                            Intent intent = new Intent(MainActDispenseM4.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                    });
                    if (sdError != null && !sdError.isShowing()) {
                        sdError.show();
                    }


                    handlerQuit = new Handler(Looper.getMainLooper());
                    handlerQuit.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handlerQuit.removeCallbacksAndMessages(null);
                            Intent intent = new Intent(MainActDispenseM4.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }, 15000);
                }
            }, 12000);

        } else if (event.m_lParam3 == TcnVendEventResultID.SHIP_SUCCESS) {
            if (checkErrorHandler != null) {
                checkErrorHandler.removeCallbacksAndMessages(null);
            }
            arr_count.get(countItem).setCheckStatus(true);
            for (int i = 0; i < cartListModels.size(); i++) {
                if (arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())) {
                    if (productId != Integer.valueOf(arr_count.get(countItem).getproduct())) {
                        countProduct = 1;
                    } else {
                        countProduct = countProduct + 1;
                    }
                    productId = Integer.valueOf(arr_count.get(countItem).getproduct());
                    if (cartListModels.get(i).getItemStatus().isEmpty()) {
                        ArrayList<String> arrListing = new ArrayList<>();
                        arrListing.add(countProduct + ".Success");
                        cartListModels.get(i).setItemStatus(arrListing);
                    } else {
                        cartListModels.get(i).getItemStatus().add(countProduct + ".Success");
                    }
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.update(cartListModels);
                }
            });
            checkResponse(true);

        } else if (event.m_lParam3 == TcnVendEventResultID.SHIP_FAIL) {
            if (checkErrorHandler != null) {
                checkErrorHandler.removeCallbacksAndMessages(null);
            }
            arr_count.get(countItem).setCheckStatus(false);
            for (int i = 0; i < cartListModels.size(); i++) {
                if (arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())) {
                    if (productId != Integer.valueOf(arr_count.get(countItem).getproduct())) {
                        countProduct = 1;
                    } else {
                        countProduct = countProduct + 1;
                    }
                    productId = Integer.valueOf(arr_count.get(countItem).getproduct());
                    if (cartListModels.get(i).getItemStatus().isEmpty()) {
                        ArrayList<String> arrListing = new ArrayList<>();
                        arrListing.add(countProduct + ".Failed");
                        cartListModels.get(i).setItemStatus(arrListing);
                    } else {
                        cartListModels.get(i).getItemStatus().add(countProduct + ".Failed");
                    }
                    break;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.update(cartListModels);
                }
            });
            checkResponse(false);
        }
    };

    public static <T> ArrayList<T> listToArrayList(List<T> list) {
        return list != null ? new ArrayList<>(list) : null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispense_popup_2025);
        Log.i(TAG, "MainAct onCreate()");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TcnVendIF.getInstance().registerListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TcnVendIF.getInstance().unregisterListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        m_OutDialog = null;
        listener = null;
        if (checkErrorHandler != null) {
            checkErrorHandler.removeCallbacksAndMessages(null);
        }
        if (handlerQuit != null) {
            handlerQuit.removeCallbacksAndMessages(null);
        }
    }

    private void initView() {
        Intent intent = new Intent();
        intent = getIntent();
        String userObjStr = intent.getStringExtra("userObjStr");
        transactionNo = intent.getStringExtra("transactionNo");

        Gson gson = new Gson();
        UserObj userobj = gson.fromJson(userObjStr, new TypeToken<UserObj>() {
        }.getType());
        mtd = userobj.getMtd();
        isloggedin = userobj.getIsloggedin();
        paytype = userobj.getIpaytype();
        chargingprice = userobj.getChargingprice();
        this.payid = payid;
        points = userobj.getPoints();
        userid = userobj.getUserid();
        pid = userobj.getPid();
        this.paystatus = paystatus;
        congifModels = userobj.configModel;
        ArrayList<CartListModel> cartListModelList = listToArrayList(userobj.getCartModel());

        for (CongifModel cn : congifModels) {
            fid = cn.getFid();
            mid = cn.getMid();
        }

        cartListModels = cartListModelList;
        for (CartListModel cart : cartListModels) {
            int qty;
            qty = Integer.parseInt(cart.getItemqty());
            for (int x = 0; x < qty; x++) {
                productsids += cart.getProdid() + ",";
            }
        }
        arr_count = new ArrayList<>();
        int countTotal = 0;
        //set all position to default 0
        for (int i = 0; i < cartListModels.size(); i++) {
            cartListModels.get(i).setPostion("0");

            int size = Integer.parseInt(cartListModels.get(i).getItemqty());
            for (int j = 0; j < size; j++) {
                countObj obj = new countObj();
                obj.setproduct(cartListModels.get(i).getSerial_port());
                obj.setcount(countTotal);
                obj.setposition(0);
                obj.setqty(cartListModelList.get(i).getItemqty());
                arr_count.add(obj);
                countTotal = countTotal + 1;
            }

        }
        setTextViewTotalPrice();
        setupForDispense();
        startDispense();

    }

    private void setupForDispense() {
        recyclerViewCart = findViewById(R.id.mrecyclr);
        adapter = new CartListAdapter2025(cartListModels);
        recyclerViewCart.setAdapter(adapter);
    }

    private void setTextViewTotalPrice() {
        final TextView tvTotalPrice = findViewById(R.id.pricetext);
        tvTotalPrice.setText("TOTAL : RM " + String.format("%.2f", chargingprice));
    }

    private void startDispense() {
        String itemno = arr_count.get(countItem).getproduct();
        int slotNo = Integer.valueOf(itemno);//出货的货道号 dispensing slot number
        String shipMethod = PayMethod.PAYMETHED_WECHAT; //出货方法,微信支付出货，此处自己可以修改。 The shipping method is defined by the payment method, and the developer can replace WECHAT with the actual payment method.
        String amount = "0.1";    //支付的金额（元）,自己修改 This is a unit price, the developer can switch the unit price according to the country
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String tradeNo = timestamp;//支付订单号，每次出货，订单号不能一样，此处自己修改。 Transaction number, it cannot be the same number and should be different every time. you can modify it by yourself.
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacksAndMessages(null);
//                TcnVendIF.getInstance().reqShip(slotNo, shipMethod, amount, tradeNo);
                TcnVendIF.getInstance().reqShipTest(Integer.parseInt(itemno));
            }
        }, 1000);
    }

    private void checkResponse(boolean check) {
        for (int i = 0; i < arr_count.size(); i++) {
            if (countItem == arr_count.get(arr_count.size() - 1).getcount()) {
                arr_count.get(i).setposition(1);
                break;
            }
        }

        if (countItem == arr_count.get(arr_count.size() - 1).getcount()) {
            //quit
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.removeCallbacksAndMessages(null);
                    startDispense();
                }
            }, 3000);
        }

        arr_count.get(countItem).setposition(1);
        countItem++;

        if (check) {
            //have item drop
            updateStatus(Integer.valueOf(arr_count.get(countItem - 1).getproduct()), "1", Integer.valueOf(2));
        } else {
            updateStatus(Integer.valueOf(arr_count.get(countItem - 1).getproduct()), "2", Integer.valueOf(3));
        }
    }

    private void handleCallApiControlBoardError() {
        allstatuses = "";
        if (arr_count != null && !arr_count.isEmpty()) {
            for (int i = 0; i < arr_count.size(); i++) {
                int statusCheck = 7;
                if (Integer.parseInt(arr_count.get(i).getqty()) == 1) {
                    allstatuses += arr_count.get(i).getproduct() + "x" + arr_count.get(i).getqty() + ":" + statusCheck + ",";
                } else {
                    allstatuses += arr_count.get(i).getproduct() + "x" + "1" + ":" + statusCheck + ",";
                }
            }

            // Update to dashboard with error code 7
            updatetransactiondb(allstatuses, "Control Board Error");
        }
    }

//    private class VendListener implements TcnVendIF.VendEventListener {
//        @Override
//        public void VendEvent(VendEventInfo cEventInfo) {
//            if (null == cEventInfo) {
//                TcnVendIF.getInstance().LoggerError(TAG, "VendListener cEventInfo is null");
//                return;
//            }
//            Log.d("testid","testid-"+String.valueOf(cEventInfo.m_iEventID));
//            switch (cEventInfo.m_iEventID) {
//                case TcnVendEventID.CMD_TEST_SLOT:
//                    Log.d("testidparam","testidparam-"+String.valueOf(cEventInfo.m_lParam3));
//                    TcnVendIF.getInstance().LoggerDebug(TAG, "VendListener CMD_TEST_SLOT m_lParam3: " + cEventInfo.m_lParam3);
//                    if (cEventInfo.m_lParam3 == TcnVendEventResultID.SHIP_SHIPING) {
////                        showBusyDialog(cEventInfo.m_lParam1, 5, getString(R.string.background_drive_slot_testing));
//                    } else if (cEventInfo.m_lParam3 == TcnVendEventResultID.SHIP_FAIL) {
////                        showBusyDialog(cEventInfo.m_lParam1, 2, getString(R.string.background_notify_shipment_fail));
//                        arr_count.get(countItem).setCheckStatus(false);
//                        for(int i=0;i<cartListModels.size();i++){
//                            if(arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())){
//                                if(productId!=Integer.valueOf(arr_count.get(countItem).getproduct())){
//                                    countProduct=1;
//                                }else{
//                                    countProduct=countProduct+1;
//                                }
//                                productId=Integer.valueOf(arr_count.get(countItem).getproduct());
//                                if(cartListModels.get(i).getItemStatus().isEmpty()){
//                                    ArrayList<String>arrListing = new ArrayList<>();
//                                    arrListing.add(String.valueOf(countProduct)+".Failed");
//                                    cartListModels.get(i).setItemStatus(arrListing);
//                                }else{
//                                    cartListModels.get(i).getItemStatus().add(String.valueOf(countProduct)+".Failed");
//                                }
//                                break;
//                            }
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.update(cartListModels);
//                            }
//                        });
//                        checkResponse(false);
//                    } else if (cEventInfo.m_lParam3 == TcnVendEventResultID.SHIP_SUCCESS) {
////                        showBusyDialog(cEventInfo.m_lParam1, 2, getString(R.string.background_notify_shipment_success));
//                        arr_count.get(countItem).setCheckStatus(true);
//                        for(int i=0;i<cartListModels.size();i++){
//                            if(arr_count.get(countItem).getproduct().equalsIgnoreCase(cartListModels.get(i).getSerial_port())){
//                                if(productId!=Integer.valueOf(arr_count.get(countItem).getproduct())){
//                                    countProduct=1;
//                                }else{
//                                    countProduct=countProduct+1;
//                                }
//                                productId=Integer.valueOf(arr_count.get(countItem).getproduct());
//                                if(cartListModels.get(i).getItemStatus().isEmpty()){
//                                    ArrayList<String>arrListing = new ArrayList<>();
//                                    arrListing.add(String.valueOf(countProduct)+".Success");
//                                    cartListModels.get(i).setItemStatus(arrListing);
//                                }else{
//                                    cartListModels.get(i).getItemStatus().add(String.valueOf(countProduct)+".Success");
//                                }
//                                break;
//                            }
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                adapter.update(cartListModels);
//                            }
//                        });
//                        checkResponse(true);
//
//                    } else {
////                        cancelBusyDialog();
//                    }
//                    break;
//                case TcnVendEventID.CMD_QUERY_SLOT_STATUS:   //查询货道是否有故障  0：正常    255：货道号不存在 query whether the slot is faulty.  0: Normal 255: The slot number does not exist
////                    menu_spr_query_slot.setButtonDisplayText(cEventInfo.m_lParam4);
//                    break;
//                case TcnVendEventID.CMD_SELF_CHECK:
////                    menu_spr_self_check.setButtonDisplayText(cEventInfo.m_lParam4);
//                    break;
//                case TcnVendEventID.CMD_RESET:
////                    menu_spr_reset.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_SPRING:  //设置弹簧货道 Setting up a spring slot
////                    menu_spr_set_slot_spring.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_BELTS:  //设置履带货道 Set up belt slot
////                    menu_spr_set_slot_belts.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_ALL_SPRING:  //设置所有货道为弹簧货道 Set all slot as spring slots
////                    menu_spr_set_slot_spring_all.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_ALL_BELT: //设置所有货道为履带货道 Set all slot as belt slot
////                    menu_spr_set_slot_belts_all.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_SINGLE: //设置为单货道 Set as single slot
////                    menu_spr_set_single_slot.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_DOUBLE: //设置为双货道 Set as dual slot
////                    menu_spr_set_double_slot.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.SET_SLOTNO_ALL_SINGLE: //设置所有货道为单货道 Set all slots as single slot
////                    menu_spr_set_single_slot_all.setButtonDisplayText(cEventInfo.m_lParam4);
//                    if (m_OutDialog != null) {
//                        m_OutDialog.dismiss();
//                    }
//                    break;
//                case TcnVendEventID.COMMAND_SYSTEM_BUSY:
////                    TcnUtilityUI.getToast(MainActDispenseM4.this, cEventInfo.m_lParam4, 20).show();
//                    break;
//
//                case TcnVendEventID.SERIAL_PORT_CONFIG_ERROR:
//                    Log.i(TAG, "SERIAL_PORT_CONFIG_ERROR");
//                    //TcnUtilityUI.getToast(m_MainActivity, getString(R.string.error_seriport));
//                    //打开串口错误，一般是串口配置出错 Error opening the serial port, usually the serial port configuration error
//                    break;
//                case TcnVendEventID.SERIAL_PORT_SECURITY_ERROR:
//                    //打开串口错误，一般是串口配置出错 Error opening the serial port, usually the serial port configuration error
//                    break;
//                case TcnVendEventID.SERIAL_PORT_UNKNOWN_ERROR:
//                    //打开串口错误，一般是串口配置出错 Error opening the serial port, usually the serial port configuration error
//                    break;
//                case TcnVendEventID.COMMAND_SELECT_GOODS:  //选货成功  Select commodity successfully
////                    TcnUtilityUI.getToast(MainActDispenseM4.this, "选货成功"); //Select commodity successfully
//                    break;
//                case TcnVendEventID.COMMAND_INVALID_SLOTNO:
////                    TcnUtilityUI.getToast(MainActDispenseM4.this, getString(R.string.ui_base_notify_invalid_slot), 22).show();
//                    break;
//                case TcnVendEventID.COMMAND_SOLD_OUT:
//                    if (cEventInfo.m_lParam1 > 0) {
////                        TcnUtilityUI.getToast(MainActDispenseM4.this, getString(R.string.ui_base_aisle_name) + cEventInfo.m_lParam1 + getString(R.string.ui_base_notify_sold_out));
//                    } else {
////                        TcnUtilityUI.getToast(MainActDispenseM4.this, getString(R.string.ui_base_notify_sold_out));
//                    }
//                    break;
//                case TcnVendEventID.COMMAND_FAULT_SLOTNO:
////                    TcnUtilityUI.getToast(MainActDispenseM4.this, cEventInfo.m_lParam4);
//                    break;
//                case TcnVendEventID.COMMAND_SHIPPING:    //正在出货 commodity is dispensing
////                    if ((cEventInfo.m_lParam4 != null) && ((cEventInfo.m_lParam4).length() > 0)) {
////                        if (m_OutDialog == null) {
////                            m_OutDialog = new OutDialog(MainActDispenseM4.this, String.valueOf(cEventInfo.m_lParam1), cEventInfo.m_lParam4);
////                        } else {
////                            m_OutDialog.setText(cEventInfo.m_lParam4);
////                        }
////                        m_OutDialog.cleanData();
////                    } else {
////                        if (m_OutDialog == null) {
////                            m_OutDialog = new OutDialog(MainActDispenseM4.this, String.valueOf(cEventInfo.m_lParam1), getString(R.string.ui_base_notify_shipping));
////                        } else {
////                            m_OutDialog.setText(MainActDispenseM4.this.getString(R.string.ui_base_notify_shipping));
////                        }
////                    }
////                    m_OutDialog.setNumber(String.valueOf(cEventInfo.m_lParam1));
////                    m_OutDialog.show();
//                    break;
//
//                case TcnVendEventID.COMMAND_SHIPMENT_SUCCESS:    //出货成功 commodity is dispensed successfully
////                    if (null != m_OutDialog) {
////                        m_OutDialog.cancel();
////                    }
////                    if (m_LoadingDialog == null) {
////                        m_LoadingDialog = new LoadingDialog(MainActDispenseM4.this, getString(R.string.ui_base_notify_shipment_success), getString(R.string.ui_base_notify_receive_goods));
////                    } else {
////                        m_LoadingDialog.setLoadText(getString(R.string.ui_base_notify_shipment_success));
////                        m_LoadingDialog.setTitle(getString(R.string.ui_base_notify_receive_goods));
////                    }
////                    m_LoadingDialog.setShowTime(3);
////                    m_LoadingDialog.show();
//                    checkResponse(true);
//                    break;
//                case TcnVendEventID.COMMAND_SHIPMENT_FAILURE:    //出货失败 commodity delivery failed
////                    if (null != m_OutDialog) {
////                        m_OutDialog.cancel();
////                    }
////                    if (null == m_LoadingDialog) {
////                        m_LoadingDialog = new LoadingDialog(MainActDispenseM4.this, getString(R.string.ui_base_notify_shipment_fail), getString(R.string.ui_base_notify_contact_merchant));
////                    }
////                    m_LoadingDialog.setLoadText(getString(R.string.ui_base_notify_shipment_fail));
////                    m_LoadingDialog.setTitle(getString(R.string.ui_base_notify_contact_merchant));
////                    m_LoadingDialog.setShowTime(3);
////                    m_LoadingDialog.show();
//                    checkResponse(false);
//                    break;
//                case TcnVendEventID.CMD_READ_DOOR_STATUS:  //门动作上报 Door action report
//                    if (TcnVendEventResultID.DO_CLOSE == cEventInfo.m_lParam1) {   //关门 close the door
////                        TcnUtilityUI.getToast(MainActDispenseM4.this, "关门", 20).show();
//                    } else if (TcnVendEventResultID.DO_OPEN == cEventInfo.m_lParam1) {   //开门 Open the door
////                        TcnUtilityUI.getToast(MainActDispenseM4.this, "开门", 20).show();
//                    } else {
//
//                    }
//                    break;
//                case TcnVendEventID.PROMPT_INFO:
////                    TcnUtilityUI.getToast(MainActDispenseM4.this, cEventInfo.m_lParam4);
//                    break;
//                case TcnVendEventID.CMD_REQ_PERMISSION:     //授权访问文件夹 Authorize access to folders
//                    //请选择确定 Please select OK

    /// /                    TcnUtilityUI.getToast(MainActDispenseM4.this, "请选择确定");
//                    ActivityCompat.requestPermissions(MainActDispenseM4.this, TcnVendIF.getInstance().getPermission(cEventInfo.m_lParam4), 126);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
    private void updateStatus(int product, String pos, int status) {
        String productcode = String.valueOf(product);
        boolean checkhere = false;
        //Log.d("print", "print--"+productcode+"/"+pos+"/"+String.valueOf(status));

        int check = 0;
        for (int j = 0; j < arr_count.size(); j++) {
            System.out.println("loggings-count-" + arr_count.get(j).getposition() + "-product-" + arr_count.get(j).getproduct());
            if (arr_count.get(j).getproduct().equalsIgnoreCase(productcode) && arr_count.get(j).getposition() == 0) {
                check++;
            }
        }

        if (check == 0) {
            for (int i = 0; i < cartListModels.size(); i++) {
                System.out.println("loggings-countmodel-" + cartListModels.get(i).getSerial_port() + "-productcode-" + productcode + "-pos-" + cartListModels.get(i).getPosition());
                if (cartListModels.get(i).getSerial_port().equalsIgnoreCase(productcode)) {
                    cartListModels.get(i).setPostion(pos);
                    checkhere = true;

                    allstatuses += product + "x" + cartListModels.get(i).getItemqty() + ":" + status + ",";

                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateItemChange(finalI);
                        }
                    });
                }
            }
        }

        String text = "";
        if (checkhere) {
            int checkc = 0;
            for (int i = 0; i < arr_count.size(); i++) {
                if (arr_count.get(i).getposition() == 0) {
                    checkc++;
                }
            }


            if (checkc == 0) {
                allstatuses = "";
                for (int i = 0; i < arr_count.size(); i++) {
                    int statusCheck = 3;
                    if (arr_count.get(i).getCheckStatus()) {
                        statusCheck = 2;
                    }
                    if (Integer.valueOf(arr_count.get(i).getqty()) == 1) {
                        allstatuses += arr_count.get(i).getproduct() + "x" + arr_count.get(i).getqty() + ":" + statusCheck + ",";
                    } else {
                        allstatuses += arr_count.get(i).getproduct() + "x" + "1" + ":" + statusCheck + ",";
                    }
                }

                final Handler handlerDis = new Handler(Looper.getMainLooper());
                handlerDis.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        handlerDis.removeCallbacksAndMessages(null);

                        Handler handler13 = new Handler();
                        handler13.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkPopUp = true;
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (sdthankyou != null) {
                                                if (sdthankyou.isShowing()) {
                                                    sdthankyou.dismiss();
                                                }
                                            }
                                        }
                                    });

                                } catch (Exception ex) {
                                }
                                handler13.removeCallbacksAndMessages(null);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActDispenseM4.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                //}
                            }
                        }, 15000);

//                        Temporary Disable
//                                RollingLogger.i(TAG, "api call uptqty");
                        final Handler handlerprd = new Handler();
                        handlerprd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handlerprd.removeCallbacksAndMessages(null);
                                updateprodqty();
                            }
                        }, 1000);
                        if (isloggedin) {
                            updatemobiletransactiondb();
                            newpoints = points - chargingprice;
                            updatepointsmdb();
                        }
                        updatetransactiondb(allstatuses, "");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int successInt = 0, failInt = 0;
                                for (int i = 0; i < arr_count.size(); i++) {
                                    if (arr_count.get(i).getCheckStatus()) {
                                        successInt = successInt + 1;
                                    } else {
                                        failInt = failInt + 1;
                                    }
                                }
                                LayoutInflater inflater = LayoutInflater.from(MainActDispenseM4.this);
                                View dialogView = inflater.inflate(R.layout.dialog_message, null);
                                TextView messageTextView = dialogView.findViewById(R.id.dialogMessage);
                                messageTextView.setText("Your order is dispensed.\n\nSuccess: " + successInt + "\nFail: " + failInt);

                                sdthankyou = new SweetAlertDialog(MainActDispenseM4.this, SweetAlertDialog.SUCCESS_TYPE);
                                sdthankyou.setTitleText("Thank you.");
                                sdthankyou.setCustomView(dialogView);
                                sdthankyou.setCanceledOnTouchOutside(false);
                                sdthankyou.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Log.d("testin3", "testin3");
                                        handler13.removeCallbacksAndMessages(null);
                                        if (sdthankyou != null) {
                                            if (sdthankyou.isShowing()) {
                                                sdthankyou.dismiss();
                                            }
                                        }
                                        if (!checkPopUp) {
                                            Intent intent = new Intent(MainActDispenseM4.this, MainActivity.class);
                                            Log.d("testin1", "testin1");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                                try {
                                    if (!isFinishing()) {
                                        sdthankyou.show();
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        });

                    }
                }, 3000);
            }
        }
    }

    public void updatetransactiondb(String status, final String remarks) {
        RollingLogger.i(TAG, "updatetransactiondb api call");
        RollingLogger.i(TAG, "updatetransactiondb api call-amount-" + chargingprice);
        RollingLogger.i(TAG, "updatetransactiondb api call-fid-" + fid);
        RollingLogger.i(TAG, "updatetransactiondb api call-mid-" + mid);
        RollingLogger.i(TAG, "updatetransactiondb api call-productsids-" + productsids);
        RollingLogger.i(TAG, "updatetransactiondb api call-paytype-" + paytype);
        try {
            Date currentTime = Calendar.getInstance().getTime();

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            String tag_json_obj = "json_obj_req";
            JSONObject jsonParam = null;
            String url = "https://vendingappapi.azurewebsites.net/Api/Transaction";

            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setAmount(chargingprice);
            transactionModel.setmDate(currentTime);
            transactionModel.setUserID(userid);
            transactionModel.setFranchiseID(fid);
            transactionModel.setMachineID(mid);
            transactionModel.setProductsIdes(productsids);

            String versionName = "";
            try {
                versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            final String mtdUpdated = mtd + " (" + transactionNo + ") M4 " + versionName;
            transactionModel.setPaymentType(mtdUpdated);


            transactionModel.setPaymentMethod(paytype);
            transactionModel.setFreePoints("");
            transactionModel.setPromocode(status);
            transactionModel.setPromoamount("");
            transactionModel.setPaymentStatus(paystatus);
            transactionModel.setPaymentID(payid);
            transactionModel.setRemarks(remarks);

            try {
                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            final String requestBody = jsonParam.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                public void onResponse(String response) {
                    //updateprodqty();
                    System.out.println("Trans =" + response);

                    RollingLogger.i(TAG, "updatetransactiondb api call response-" + response);
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                    RollingLogger.i(TAG, "updatetransactiondb api call error-" + error);

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }


                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        Log.d("InputStream", String.valueOf(response));
                        //  responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            RollingLogger.i(TAG, "Dispense update transaction error - " + ex);
        }


    }

    private void updateprodqty() {

        int totalqty = 0;
        for (int i = 0; i < cartListModels.size(); i++) {
            totalqty = 0;

            System.out.println("total qty= " + totalqty + " prod id= " + cartListModels.get(i).getProdid());
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(this);
            }
            JSONObject jsonParam = null;
            String url = "https://vendingappapi.azurewebsites.net/api/Product/" + cartListModels.get(i).getFprodid();

            final String requestBody = String.valueOf(cartListModels.get(i).getQuantityMinus());

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }


                @Override
                public byte[] getBody() throws AuthFailureError {
                    return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        Log.d("InputStream", String.valueOf(response));
                        //  responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);

        }

    }

    private void updatemobiletransactiondb() {

        Date currentTime = Calendar.getInstance().getTime();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        JSONObject jsonParam = null;
        String url = "https://memberappapi.azurewebsites.net/Api/Transaction";

        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setAmount(chargingprice);
        transactionModel.setmDate(currentTime);
        transactionModel.setUserID(userid);
        transactionModel.setFranchiseID(fid);
        transactionModel.setMachineID(mid);
        transactionModel.setProductsIdes(productsids);

        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        final String mtdUpdated = mtd + " M4 " + versionName;
        transactionModel.setPaymentType(mtdUpdated);


        transactionModel.setPaymentMethod(paytype);
        transactionModel.setFreePoints("");
        transactionModel.setPromocode("");
        transactionModel.setPromoamount("");
        transactionModel.setVouchers("");


        try {
            jsonParam = new JSONObject(new Gson().toJson(transactionModel));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("test2", e.toString());
        }


        final String requestBody = jsonParam.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    Log.d("InputStream", String.valueOf(response));
                    //  responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);


    }

    private void updatepointsmdb() {

        // Date date = null;
        Date date = Calendar.getInstance().getTime();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        JSONObject jsonParam = null;
        String url = "https://memberappapi.azurewebsites.net/Api/Points";

        Pointsend pointModel = new Pointsend();
        pointModel.setID(pid);
        pointModel.setUserID(userid);
        pointModel.setPoints(newpoints);
        pointModel.setExpireDate(date);
        pointModel.setUserStatus(userstatus);

        try {
            jsonParam = new JSONObject(new Gson().toJson(pointModel));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonParam.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    Log.d("InputStream", String.valueOf(response));
                    //  responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);

    }
}
