package com.tcn.sdk.springdemo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.twin.DesiredPropertiesCallback;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodPayload;
import com.microsoft.azure.sdk.iot.device.twin.DirectMethodResponse;
import com.microsoft.azure.sdk.iot.device.twin.MethodCallback;
import com.microsoft.azure.sdk.iot.device.twin.Twin;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Model.SliderItem;
import com.tcn.sdk.springdemo.Model.UserObj;
import com.tcn.sdk.springdemo.Recycler.SliderAdapterExample;
import com.tcn.sdk.springdemo.Utilities.PortsandVeriables;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.Utilities.Uti;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements DesiredPropertiesCallback, MethodCallback {
    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_NOT_DEFINED = 404;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static MainActivity instance;
    private final String TAG = "MainActivity";
    private final int oldtransid = 0;
    private final ArrayList<String> vidoes = new ArrayList<>();
    public boolean onlinestatus = false;
    PortsandVeriables portsandVeriables;
    IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private ImageButton startbutton;
    private VideoView videoView;
    private ImageView logo;
    private configdata dbconfig;
    private List<CongifModel> congifModels;
    private String fid = "0";
    private String mid = "0";
    private String lastvideoindex = "0";
    private SharedPreferences sharedpreferences;
    private SliderView sliderView;
    private List<SliderItem> sliderItems;
    private int curentvideocout = 1;
    private int transid = 0;
    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "On Manual Dispense start");
                    JSONObject data = (JSONObject) args[0];

                    String sp, CreatedBy;
                    String Fid = "", Pid;
                    int temp, Dispid = 0;
                    double price;
                    try {

                        transid = data.getInt("Dispid");
//                        if(transid != oldtransid) {
//                            oldtransid = transid;
                        System.out.println("response from socket 1 = " + data);

                        price = data.getDouble("Price");
                        sp = data.getString("Serail_Port");
                        temp = data.getInt("Temp");
                        Fid = data.getString("Fid");
                        CreatedBy = data.getString("CreatedBy");
                        Pid = data.getString("Pid");
                        Dispid = data.getInt("Dispid");

                        ArrayList<CartListModel> cartListModels = new ArrayList<CartListModel>();
                        CartListModel obj = new CartListModel();
                        obj.setSerial_port(sp);
                        obj.setItemqty("1");
                        String pricedb = String.format("%.2f", price);
                        obj.setItemprice(pricedb);
                        cartListModels.add(obj);
                        UserObj userObj = new UserObj();
                        userObj.setMtd("remotely by " + CreatedBy);
                        userObj.setIsloggedin(false);
                        userObj.setIpaytype("remotely by " + CreatedBy);
                        userObj.setChargingprice(price);
                        userObj.setPoints(0);
                        userObj.setUserid(0);
                        userObj.setPid(Integer.valueOf(Pid));
                        userObj.setExpiredate("");
                        userObj.setUserstatus(0);
                        userObj.setCartModel(cartListModels);
                        userObj.setConfigModel(congifModels);

                        if (fid.equalsIgnoreCase(mid)) {
//                            DispenseDirectM5 dispensePopUpM5 = new DispenseDirectM5();
//                            dispensePopUpM5.DispensePopUp(MainActivity.this, userObj, "Success", Pid, null);
//                            Log.d("testinhere", "testinhere");
                        }
//                            DispenseDirect df = new DispenseDirect();
//                            df.DispenseDirect(MainActivity.this, sp, Fid, price, CreatedBy, temp, Pid, Dispid);
//                        }
                    } catch (JSONException e) {
                    }
                    // add the message to view
                    //addMessage(username, message);
                }
            });
        }
    };
    private String connString = "";
    private DeviceClient client;
    //    private Socket mSocket;
//    {
//        try {
//            portsandVeriables = new PortsandVeriables();
//            mSocket = IO.socket(portsandVeriables.com2);
//        } catch (URISyntaxException e) {
//        }
//    }
    private Twin twin;

    public static MainActivity getInstance() {
        return instance;
    }

    private void checkStoragePermission() {
        // Check if we have write permission
        int writePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            // Request both permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE);
        } else {
            // Permissions already granted
            // Proceed with storage operations
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both permissions granted
                // Proceed with storage operations
                proceedAfterPermissionGrant();
            } else {
                // Permission denied
                // Disable functionality that depends on this permission
                handlePermissionDenied();
            }
        }
    }

    private void proceedAfterPermissionGrant() {
        // All required permissions granted
        // Proceed with your storage operations
        RollingLogger.i(TAG, "App started");
    }

    private void handlePermissionDenied() {
        // Permission denied
        // Show explanation or disable functionality
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RollingLogger.init(getApplicationContext());

        checkStoragePermission();

        loggingCheck();

        storagePermission();

        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textView34 = findViewById(R.id.textView34);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        textView34.setText("M4 v" + version + " -- DVENDS TECH SDN BHD ©" + year + ". All rights reserved");
        textView34.setOnClickListener(view -> handleOnStartButtonClicked());

        try {
            RollingLogger.i(TAG, "version name-" + version);
            RollingLogger.i(TAG, "oncreate");
        } catch (Exception ignored) {

        }


        SharedPref.init(this);
        dbconfig = new configdata(this);
        congifModels = new ArrayList<CongifModel>();
        congifModels = dbconfig.getAllItems();

        if (!congifModels.isEmpty()) {
            for (CongifModel config : congifModels) {
                fid = config.getFid();
                mid = config.getMid();
            }
        }
        if (!fid.equalsIgnoreCase("0")) {
            TextView tv_name = findViewById(R.id.tv_name);
            tv_name.setText(fid);
        }


        logo = findViewById(R.id.imageView27);
        startbutton = findViewById(R.id.imageButton);
        videoView = findViewById(R.id.addvideo);
        sliderItems = new ArrayList<SliderItem>();
        sharedpreferences = getSharedPreferences("MyPrefs", 0);

        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "VMLocalfiles");
        if (!file.exists()) file.mkdir();
        setlocalslider(file);
        sliderView = findViewById(R.id.imageSlider);


        slideCheck();

        CustomLongClickListener.OnLongClickListener longClickListener = v -> {
            RollingLogger.i(TAG, "logo clicked");
            Intent intent = new Intent(MainActivity.this, PasswordUnlock.class);
            intent.putExtra("type", "1");
            startActivity(intent);
        };

        String timerStr = SharedPref.read(SharedPref.TIMER, "5");
        long timerLong = 0;
        try {
            timerLong = Long.valueOf(timerStr) * 1000;
        } catch (Exception ex) {

        }
        logo.setOnTouchListener(new CustomLongClickListener(longClickListener, timerLong));

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOnStartButtonClicked();
            }
        });
    }

    private void handleOnStartButtonClicked() {
        RollingLogger.i(TAG, "start button clicked");
        if (!Uti.chkinternet(MainActivity.this)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("No internet")
                    .setContentText("Please contact careline")
                    .show();
        } else {
            if (!congifModels.isEmpty()) {

                startbutton.setEnabled(false);
                Intent typepage = new Intent(MainActivity.this, TypeProfuctActivity.class);
                startActivity(typepage);
                finish();

            } else {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("Register the device").show();
            }
        }
    }

    private void slideCheck() {
        SliderAdapterExample adapter = new SliderAdapterExample(this);
        adapter.renewItems(sliderItems);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.startAutoCycle();
    }

    private void marqueenText() {
        MarqueeView marqueeView = findViewById(R.id.marqueeView);
        List<String> info = new ArrayList<>();

        SharedPref.init(this);
        String martext = SharedPref.read(SharedPref.martext, "");
        if (martext.equalsIgnoreCase("")) {
            info.add("** Welcome to DVends Tech **");
            info.add("** Become a member to get bonus points on your every purchase **");
            info.add("** Bonus points can be use to purchase **");
            info.add("** Member will be able to get voucher like buy 1 get 1 free any many more **");
        } else {
            String[] list = martext.split("\\n");
            Collections.addAll(info, list);
        }
        marqueeView.startWithList(info);
    }

    private void storagePermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void loggingCheck() {
//        try {
//            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//            loggerContext.reset();
//            ContextInitializer ci = new ContextInitializer(loggerContext);
//            ci.autoConfig();
//        } catch (JoranException ex) {
//            Log.d("error", "error");
//        }
    }

    private void setvidefrmlocal(String url) {
        try {
            Uri video = Uri.parse(url);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(null);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    videoView.stopPlayback();
                    if (curentvideocout < vidoes.size()) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("videoindex", String.valueOf(curentvideocout));
                        editor.apply();

                        videoView.setVideoURI(Uri.parse(vidoes.get(curentvideocout)));
                        videoView.start();
                        curentvideocout++;
                    } else {
                        curentvideocout = 0;
                        videoView.setVideoURI(Uri.parse(vidoes.get(curentvideocout)));
                        videoView.start();
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        RollingLogger.i(TAG, "onstop");
    }

    private void setlocalslider(File dir) {
        File[] listFile = dir.listFiles();
        ArrayList<String> mafal = new ArrayList<>();
        if (listFile != null) {
            for (File file : listFile) {
                if (file.getName().endsWith("pic.jpg")) {
                    String temp = file.getPath();
                    if (!mafal.contains(temp))
                        mafal.add(temp);
                } else if (file.getName().endsWith(".mp4")) {
                    String temmp = file.getPath();
                    if (!vidoes.contains(temmp))
                        vidoes.add(file.getPath());

                    // setvidefrmlocal(file.getPath());

                }
            }
        }
        ArrayList<String> finallist = new ArrayList<>();


        if (!vidoes.isEmpty()) {

            lastvideoindex = sharedpreferences.getString("videoindex", "0");

            try {
                setvidefrmlocal(vidoes.get(Integer.parseInt(lastvideoindex)));
            } catch (IndexOutOfBoundsException ex) {
                setvidefrmlocal(vidoes.get(0));
            }
        }

        for (String filepath : mafal) {
            finallist.add((filepath));
            sliderItems.add(new SliderItem(filepath));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        RollingLogger.i(TAG, "onresume");
        congifModels = dbconfig.getAllItems();

        String LiframeEnable = SharedPref.read(SharedPref.LiframeEnable, "");
        ImageView button4 = findViewById(R.id.button4);
        if (LiframeEnable.equalsIgnoreCase("true")) {
            button4.setVisibility(View.VISIBLE);
            String iframe_path = SharedPref.read(SharedPref.logoiframepath, "");
            if(!iframe_path.equalsIgnoreCase("")){
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(Uri.parse(iframe_path));
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    button4.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, IframeWebViewActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            button4.setVisibility(View.INVISIBLE);
        }

        try {
            String mqttStr = SharedPref.read(SharedPref.MQTT, "");
            if (!mqttStr.equalsIgnoreCase("")) {
                if (!mid.equalsIgnoreCase("0")) {
                    connString = "HostName=DVendsVending.azure-devices.net;DeviceId=" + mid + ";SharedAccessKey=k56rm60MxRpCcd7d9fnEd2SeqzAx4RV/7AIoTEEfDdY=";
                    int currentSdkVersion = android.os.Build.VERSION.SDK_INT;
                    int minSdkVersion = android.os.Build.VERSION_CODES.LOLLIPOP; // Example: API 21

                    if (currentSdkVersion >= 26) {
                        Log.d("SDK Check", "Device meets the minimum SDK version.");
                        InitClient();
                    } else {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("OS Too Low")
                                .setContentText("MQTT not support min os ver 8.0")
                                .show();
                    }
                }
            }
        } catch (Exception e2) {
            System.out.println("Exception while opening IoTHub connection");
            e2.printStackTrace();
        }

        marqueenText();
        if (!congifModels.isEmpty()) {
            for (CongifModel config : congifModels) {
                fid = config.getFid();
                mid = config.getMid();
                RollingLogger.i(TAG, "onresume-fid-" + fid);
                RollingLogger.i(TAG, "onresume-mid-" + mid);
            }
        }

        SharedPref.init(this);
        String logopath = SharedPref.read(SharedPref.logopath, "");
        if (logopath.length() > 0) {
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(Uri.parse(logopath));
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                logo.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RollingLogger.i(TAG, "onpause");

        stopClient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RollingLogger.i(TAG, "ondestroy");
//        if(mSocket!=null){
//            mSocket.close();
//        }
    }

    @Override
    public DirectMethodResponse onMethodInvoked(String methodName, DirectMethodPayload directMethodPayload, Object payload) {
        if (methodName.equals("dispenseProduct")) {
            String payloadStr = directMethodPayload.getPayloadAsJsonString();
//            try
//            {
            String price = "", createdBy = "", productId = "", serialPort = "";
            try {
                JSONObject jsonObject = new JSONObject(payloadStr);
                price = jsonObject.getString("price");
                createdBy = jsonObject.getString("createdBy");
                productId = jsonObject.getString("productId");
                serialPort = jsonObject.getString("serialPort");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<CartListModel> cartListModels = new ArrayList<CartListModel>();
            CartListModel obj = new CartListModel();
            obj.setSerial_port(serialPort);
            obj.setItemqty("1");
//                String pricedb = String.format("%.2f", 0.01);
            obj.setItemprice(price);
            cartListModels.add(obj);
            UserObj userObj = new UserObj();
            userObj.setMtd("remotely by " + createdBy);
            userObj.setIsloggedin(false);
            userObj.setIpaytype("remotely by " + createdBy);
            userObj.setChargingprice(Double.parseDouble(price));
            userObj.setPoints(0);
            userObj.setUserid(0);
            userObj.setPid(Integer.valueOf(0));
            userObj.setExpiredate("");
            userObj.setUserstatus(0);
            userObj.setCartModel(cartListModels);
            userObj.setConfigModel(congifModels);

            runOnUiThread(new Runnable() {
                public void run() {
//                    DispenseDirectM5 dispensePopUpM5 = new DispenseDirectM5();
//                    dispensePopUpM5.DispensePopUp(MainActivity.this, userObj, "Success", "0", null);
                    Log.d("testinhere", "testinhere");
                }
            });

            return new DirectMethodResponse(METHOD_SUCCESS, null);
        }

        // if the command was unrecognized, return a status code to signal that to the client that invoked the method.
        return new DirectMethodResponse(METHOD_NOT_DEFINED, null);
    }

    @Override
    public void onDesiredPropertiesUpdated(Twin desiredPropertiesUpdate, Object o) {
        System.out.println("Received desired property update:");
        System.out.println(desiredPropertiesUpdate);
        twin.getDesiredProperties().putAll(desiredPropertiesUpdate.getDesiredProperties());
        twin.getDesiredProperties().setVersion(desiredPropertiesUpdate.getDesiredProperties().getVersion());
    }

    private void stopClient() {
        if (client != null) {
            String OPERATING_SYSTEM = System.getProperty("os.name");
            client.close();
            System.out.println("Shutting down..." + OPERATING_SYSTEM);
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void InitClient() {
        client = new DeviceClient(connString, protocol);

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        client.open(false);

                        if (protocol == IotHubClientProtocol.MQTT) {
                            MessageCallbackMqtt callback = new MessageCallbackMqtt();
                            Counter counter = new Counter(0);
                            client.setMessageCallback(callback, counter);
                        } else {
                            MessageCallback callback = new MessageCallback();
                            Counter counter = new Counter(0);
                            client.setMessageCallback(callback, counter);
                        }
                        client.subscribeToMethods(MainActivity.this, null);
                        client.subscribeToDesiredProperties(MainActivity.this, null);
                        twin = client.getTwin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e2) {
            System.err.println("Exception while opening IoTHub connection: " + e2.getMessage());
            client.close();
            System.out.println("Shutting down...");
        }
    }

    static class MessageCallbackMqtt implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult onCloudToDeviceMessageReceived(Message msg, Object context) {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();
//            tv_msg_receive.setText("Received message " + counter.toString()
//                    + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            return IotHubMessageResult.COMPLETE;
        }
    }

    static class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult onCloudToDeviceMessageReceived(Message msg, Object context) {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }
    }

    /**
     * Used as a counter in the message callback.
     */
    static class Counter {
        int num;

        Counter(int num) {
            this.num = num;
        }

        int get() {
            return this.num;
        }

        void increment() {
            this.num++;
        }

        @Override
        public String toString() {
            return Integer.toString(this.num);
        }
    }
}