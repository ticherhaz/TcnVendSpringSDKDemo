package com.tcn.sdk.springdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.tcn.sdk.springdemo.DBUtils.configdata;
import com.tcn.sdk.springdemo.DBUtils.dbutis;
import com.tcn.sdk.springdemo.Model.CongifModel;
import com.tcn.sdk.springdemo.Note.MainNoteActivity;
import com.tcn.sdk.springdemo.PayWave.PayWaveMainActivity;
import com.tcn.sdk.springdemo.SarawakPay.SarawakMainActivity;
import com.tcn.sdk.springdemo.Utilities.DialogUtils;
import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.Utilities.Tools;
import com.tcn.sdk.springdemo.tcnSpring.MainAct;

import net.ticherhaz.firelog.FireLog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class configact extends AppCompatActivity {

    private final int SELECT_PICTURE = 200;
    private final String TAG = "configact";
    private configdata configdb;
    private Boolean isuserpaying = false;
    private Boolean threadintrupt = false;
    private Boolean oncreate = false;
    private SweetAlertDialog sweetAlertDialog;
    private wait30 w30;
    private MaterialSwitch chkGPaywave, chkGWallet, chkIWallet, chkLdispense, chkLPayment, chkDispenseTest, chkSarawak, chkToken, chkDuitnowonlyNew,
            chkLcart, chkIframe;
    private List<CongifModel> congifModels;
    private Uri selectedImageUri;
    private EditText etText;
    private boolean checkOnOffTop = false;

    private Button buttonGeneral, buttonPayment, buttonOthers, test_tcn;
    private LinearLayout llGeneral, llPayment, llOthers;
    private int logoInt=0;

    private void showsweetalerttimeout(final CountDownTimer[] ct) {
        sweetAlertDialog = new SweetAlertDialog(configact.this, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press anywhere on screen to continue");
        sweetAlertDialog.setContentText("This session will end in 10");

        sweetAlertDialog.setConfirmButton("Continue", sweetAlertDialog -> {
            ct[0].cancel();
            sweetAlertDialog.dismissWithAnimation();
        });

        sweetAlertDialog.setCancelButton("Close", sweetAlertDialog -> {

            threadintrupt = true;
            ct[0].cancel();
            sweetAlertDialog.dismissWithAnimation();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        });

        sweetAlertDialog.setOnDismissListener(dialog -> {
            sweetAlertDialog.dismissWithAnimation();
            ct[0].cancel();
        });

        sweetAlertDialog.show();
    }

    private void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);
//        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // pass the constant to compare it
        // with the returned requestCode
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                getContentResolver().takePersistableUriPermission(selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);

                String path = selectedImageUri.toString();
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(Uri.parse(path));
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    if(logoInt==0) {
                        ImageView iv_logo = findViewById(R.id.iv_logo);
                        iv_logo.setImageBitmap(bitmap);
                        iv_logo.setVisibility(View.VISIBLE);
                        SharedPref.write(SharedPref.logopath, path);
                    }else{
                        ImageView iv_iframe_logo = findViewById(R.id.iv_iframe_logo);
                        iv_iframe_logo.setImageBitmap(bitmap);
                        iv_iframe_logo.setVisibility(View.VISIBLE);
                        SharedPref.write(SharedPref.logoiframepath, path);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configact);

        RollingLogger.i(TAG, "Config Page Start");

        SharedPref.init(this);
        final EditText fid = findViewById(R.id.fid);
        final EditText mid = findViewById(R.id.mid);
        final EditText etip = findViewById(R.id.etip);
        final EditText etmec = findViewById(R.id.etmec);
        final EditText etmk = findViewById(R.id.etmk);
        final EditText et_timer = findViewById(R.id.et_timer);
        final EditText etTextIFrame = findViewById(R.id.etTextIFrame);
        test_tcn = findViewById(R.id.test_tcn);
        test_tcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(configact.this, MainAct.class);
                startActivity(intent);
            }
        });

        buttonGeneral = findViewById(R.id.button_general);
        buttonPayment = findViewById(R.id.button_payment);
        buttonOthers = findViewById(R.id.button_others);

        llGeneral = findViewById(R.id.ll_general);
        llPayment = findViewById(R.id.ll_payment);
        llOthers = findViewById(R.id.ll_others);

        setButtonDisplayMain(buttonGeneral, LayoutToDisplayType.GENERAL);
        setButtonDisplayMain(buttonPayment, LayoutToDisplayType.PAYMENT);
        setButtonDisplayMain(buttonOthers, LayoutToDisplayType.OTHERS);

        String timerStr = SharedPref.read(SharedPref.TIMER, "5");
        if (timerStr.equalsIgnoreCase("5")) {
            et_timer.setText("5");
        } else {
            et_timer.setText(timerStr);
        }

        etText = findViewById(R.id.etText);
        String marText = SharedPref.read(SharedPref.martext, "");
        if (!marText.equalsIgnoreCase("")) {
            etText.setText(marText);
        }

        Button btn_savetext = findViewById(R.id.btn_savetext);
        btn_savetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "save text button clicked");
                SharedPref.write(SharedPref.martext, etText.getText().toString());
                finish();
                RollingLogger.i(TAG, "Config Page End");
            }
        });
        Button btnqr = findViewById(R.id.btnqr);
        btnqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "qr button clicked");
                Intent intent = new Intent(configact.this, QrActivity.class);
                startActivity(intent);
            }
        });

        Button btntestnote = findViewById(R.id.btntestnote);
        btntestnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "Note Float button clicked");
                Intent intent = new Intent(configact.this, MainNoteActivity.class);
                startActivity(intent);
            }
        });

        Button btnmachinesettinginfo = findViewById(R.id.btnmachinesettinginfo);
        btnmachinesettinginfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "machine setting info button clicked");
//                Intent intent = new Intent(configact.this, MachineSettingInfo.class);
//                startActivity(intent);
//                finish();
                RollingLogger.i(TAG, "Config Page End");
            }
        });

        Button btn_dlogo = findViewById(R.id.btn_dlogo);
        btn_dlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "default logo button clicked");
                SharedPref.write(SharedPref.logopath, "");
                Toast.makeText(configact.this, "Reset to Default Logo Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        Button btn_logo = findViewById(R.id.btn_logo);
        btn_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "logo button clicked");
                logoInt=0;
                imageChooser();
            }
        });

        Button btn_iframe_logo = findViewById(R.id.btn_iframe_logo);
        btn_iframe_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "iframe logo button clicked");
                logoInt=1;
                imageChooser();
            }
        });

        Button btnPassword = findViewById(R.id.btnPassword);
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "change admin password button clicked");
                Intent intent = new Intent(configact.this, PasswordChange.class);
                startActivity(intent);
            }
        });

        Button btntestdispense = findViewById(R.id.btntestdispense);
        btntestdispense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "test dispense button clicked");
//                Intent intent = new Intent(configact.this, ComAssistantActivity.class);
//                startActivity(intent);
            }
        });

        Button sarawakpay = findViewById(R.id.sarawakpay);
        sarawakpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RollingLogger.i(TAG, "Sarawakpay button clicked");
                Intent intent = new Intent(configact.this, SarawakMainActivity.class);
                startActivity(intent);
            }
        });
        Button btnsave = findViewById(R.id.btnsav);
        Button btnclose = findViewById(R.id.btnclose);
        Button btnsettings = findViewById(R.id.setting);
        Button gkash = findViewById(R.id.gkash);
        EditText sarawakpay_mid = findViewById(R.id.sarawakpay_mid);
        EditText sarawakpay_mname = findViewById(R.id.sarawakpay_mname);
        SharedPref.init(this);
        String mid1 = SharedPref.read(SharedPref.SarawakPayMid, "");
        String mname1 = SharedPref.read(SharedPref.SarawakPayMname, "");
        sarawakpay_mid.setText(mid1);
        sarawakpay_mname.setText(mname1);

        MaterialSwitch chkMqtt = findViewById(R.id.chkMqtt);
        String chkMqttStr = SharedPref.read(SharedPref.MQTT, "");
        chkMqtt.setChecked(!chkMqttStr.equalsIgnoreCase(""));
        chkMqtt.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.MQTT, "true");
                RollingLogger.i(TAG, "Mqtt Ticked");
            } else {
                SharedPref.write(SharedPref.MQTT, "");
                RollingLogger.i(TAG, "Mqtt UnTicked");
            }
        });

        MaterialSwitch chkFasspayOnly = findViewById(R.id.chkFasspayOnly);
        String chkFasspayStrOnly = SharedPref.read(SharedPref.FasspayOnly, "");
        chkFasspayOnly.setChecked(!chkFasspayStrOnly.equalsIgnoreCase(""));
        chkFasspayOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.FasspayOnly, "true");
                RollingLogger.i(TAG, "Fasspay + Ewallet Ticked");
            } else {
                SharedPref.write(SharedPref.FasspayOnly, "");
                RollingLogger.i(TAG, "Fasspay + Ewallet UnTicked");
            }
        });

        MaterialSwitch chkCashOnly = findViewById(R.id.chkCashOnly);
        String cashStrOnly = SharedPref.read(SharedPref.CashOnly, "");
        chkCashOnly.setChecked(!cashStrOnly.equalsIgnoreCase(""));
        chkCashOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Cash + Ewallet Ticked");
                SharedPref.write(SharedPref.CashOnly, "true");
                RollingLogger.i(TAG, "Cash Ticked");
            } else {
                RollingLogger.i(TAG, "Cash + Ewallet UnTicked");
                SharedPref.write(SharedPref.CashOnly, "");
                RollingLogger.i(TAG, "Cash UnTicked");
            }
        });

        MaterialSwitch chkCashCoinOnly = findViewById(R.id.chkCashCoinOnly);
        String cashCoinOnly = SharedPref.read(SharedPref.CashCoinPay, "");
        chkCashCoinOnly.setChecked(!cashCoinOnly.equalsIgnoreCase(""));
        chkCashCoinOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.CashCoinPay, "true");
                RollingLogger.i(TAG, "Cash+coin Ticked");
            } else {
                SharedPref.write(SharedPref.CashCoinPay, "");
                RollingLogger.i(TAG, "Cash+coin UnTicked");
            }
        });

        MaterialSwitch chkDuitNowOnly = findViewById(R.id.chkDuitNowOnly);
        String DuitNowOnly = SharedPref.read(SharedPref.DuitNowOnly, "");
        chkDuitNowOnly.setChecked(!DuitNowOnly.equalsIgnoreCase(""));
        chkDuitNowOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.DuitNowOnly, "true");
                RollingLogger.i(TAG, "Duitnow + Ewallet Ticked");
            } else {
                SharedPref.write(SharedPref.DuitNowOnly, "");
                RollingLogger.i(TAG, "Duitnow + Ewallet UnTicked");
            }
        });

        MaterialSwitch chkSarawakOnly = findViewById(R.id.chkSarawakOnly);
        chkSarawakOnly.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.SarawakPayOnly, "true");
                RollingLogger.i(TAG, "Sarawak payment only Ticked");
            } else {
                SharedPref.write(SharedPref.SarawakPayOnly, "");
                RollingLogger.i(TAG, "Sarawak payment only UnTicked");
            }
        });
        String sarawakStrOnly = SharedPref.read(SharedPref.SarawakPayOnly, "");
        chkSarawakOnly.setChecked(!sarawakStrOnly.equalsIgnoreCase(""));
        chkGPaywave = findViewById(R.id.chkGPaywave);
        chkLdispense = findViewById(R.id.chkLdispense);
        chkLcart = findViewById(R.id.chkLcart);
        chkIframe = findViewById(R.id.chkIframe);
        chkLPayment = findViewById(R.id.chkLPayment);
        chkGWallet = findViewById(R.id.chkGWallet);
        chkIWallet = findViewById(R.id.chkIWallet);
        chkSarawak = findViewById(R.id.chkSarawak);
        chkDuitnowonlyNew = findViewById(R.id.chkDuitnowonlyNew);
        chkDuitnowonlyNew.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.DuitNowOnlyNew, "true");
                RollingLogger.i(TAG, "Duitnow only new Ticked");
            } else {
                SharedPref.write(SharedPref.DuitNowOnlyNew, "");
                RollingLogger.i(TAG, "Duitnow only new UnTicked");
            }
        });
        String duitnowonlynewStr = SharedPref.read(SharedPref.DuitNowOnlyNew, "");
        chkDuitnowonlyNew.setChecked(!duitnowonlynewStr.equalsIgnoreCase(""));
        chkToken = findViewById(R.id.chkToken);
        chkToken.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.Token, "true");
                RollingLogger.i(TAG, "Token Ticked");
            } else {
                SharedPref.write(SharedPref.Token, "");
                RollingLogger.i(TAG, "Token UnTicked");
            }
        });
        String tokenStr = SharedPref.read(SharedPref.Token, "");
        chkToken.setChecked(!tokenStr.equalsIgnoreCase(""));
        chkSarawak.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.SarawakPay, "true");
                RollingLogger.i(TAG, "Sarawak payment Ticked");
            } else {
                SharedPref.write(SharedPref.SarawakPay, "");
                RollingLogger.i(TAG, "Sarawak payment UnTicked");
            }
        });
        String sarawakStr = SharedPref.read(SharedPref.SarawakPay, "");
        chkSarawak.setChecked(!sarawakStr.equalsIgnoreCase(""));
        EditText et_password = findViewById(R.id.et_password);
        String TestDispensePassword = SharedPref.read(SharedPref.TestDispensePassword, "");
        et_password.setText(TestDispensePassword);
        chkDispenseTest = findViewById(R.id.chkDispenseTest);
        LinearLayout ll_top = findViewById(R.id.ll_top);
        ll_top.setOnLongClickListener(view -> {
            if (!checkOnOffTop) {
                chkDispenseTest.setVisibility(View.VISIBLE);
                et_password.setVisibility(View.VISIBLE);
                checkOnOffTop = true;
            } else {
                chkDispenseTest.setVisibility(View.GONE);
                et_password.setVisibility(View.GONE);
                checkOnOffTop = false;
            }
            return true;
        });
        Button btn_saveiframe = findViewById(R.id.btn_saveiframe);
        btn_saveiframe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref.write(SharedPref.webviewURL, etTextIFrame.getText().toString());
            }
        });
        etTextIFrame.setText(SharedPref.read(SharedPref.webviewURL, ""));
        String dispensetest = SharedPref.read(SharedPref.dispensetest, "");
        chkDispenseTest.setChecked(!dispensetest.equalsIgnoreCase(""));
        chkDispenseTest.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                SharedPref.write(SharedPref.dispensetest, "true");
                RollingLogger.i(TAG, "DispenseTest Ticked");
            } else {
                SharedPref.write(SharedPref.dispensetest, "");
                RollingLogger.i(TAG, "DispenseTest UnTicked");
            }
        });
        chkIframe.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "iFrame Enable Ticked");
                SharedPref.write(SharedPref.LiframeEnable, "true");
                etTextIFrame.setVisibility(View.VISIBLE);
                btn_saveiframe.setVisibility(View.VISIBLE);
            } else {
                RollingLogger.i(TAG, "Iframe Disable UnTicked");
                SharedPref.write(SharedPref.LiframeEnable, "");
                etTextIFrame.setVisibility(View.GONE);
                btn_saveiframe.setVisibility(View.GONE);
            }
        });
        String LiframeEnable = SharedPref.read(SharedPref.LiframeEnable, "");
        chkIframe.setChecked(!LiframeEnable.equalsIgnoreCase(""));
        chkLcart.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Cart Disable Ticked");
                SharedPref.write(SharedPref.LcartEnable, "true");
            } else {
                RollingLogger.i(TAG, "Cart Disable UnTicked");
                SharedPref.write(SharedPref.LcartEnable, "");
            }
        });
        String LcartEnable = SharedPref.read(SharedPref.LcartEnable, "true");
        chkLcart.setChecked(LcartEnable.equalsIgnoreCase("true"));
        chkLdispense.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Logs for dispense activity Ticked");
                SharedPref.write(SharedPref.Ldispense, "true");
            } else {
                RollingLogger.i(TAG, "Logs for dispense activity UnTicked");
                SharedPref.write(SharedPref.Ldispense, "");
            }
        });
        String Ldispense = SharedPref.read(SharedPref.Ldispense, "");
        chkLdispense.setChecked(!Ldispense.equalsIgnoreCase(""));
        chkLPayment.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Logs for Payment activity Ticked");
                SharedPref.write(SharedPref.Lpayment, "true");
            } else {
                RollingLogger.i(TAG, "Logs for Payment activity UnTicked");
                SharedPref.write(SharedPref.Lpayment, "");
            }
        });
        String Lpayment = SharedPref.read(SharedPref.Lpayment, "");
        chkLPayment.setChecked(!Lpayment.equalsIgnoreCase(""));
        String type = SharedPref.read(SharedPref.type, "");
        String gtypepaywave = SharedPref.read(SharedPref.gtypepaywave, "");
        String gtypewallet = SharedPref.read(SharedPref.gtypewallet, "");
        if (type.equalsIgnoreCase("") && gtypepaywave.equalsIgnoreCase("")
                && gtypewallet.equalsIgnoreCase("")) {
            chkIWallet.setChecked(true);
            chkGPaywave.setChecked(false);
            chkGWallet.setChecked(false);
        } else if (type.equalsIgnoreCase("iwallet") && gtypepaywave.equalsIgnoreCase("")
                && gtypewallet.equalsIgnoreCase("")) {
            chkIWallet.setChecked(true);
            chkGPaywave.setChecked(false);
            chkGWallet.setChecked(false);
        } else if (type.equalsIgnoreCase("") && gtypepaywave.equalsIgnoreCase("gpaywave")
                && gtypewallet.equalsIgnoreCase("gwallet")) {
            chkIWallet.setChecked(false);
            chkGPaywave.setChecked(true);
            chkGWallet.setChecked(true);
        } else if (type.equalsIgnoreCase("") && gtypepaywave.equalsIgnoreCase("gpaywave")
                && gtypewallet.equalsIgnoreCase("")) {
            chkIWallet.setChecked(false);
            chkGPaywave.setChecked(true);
            chkGWallet.setChecked(false);
        } else if (type.equalsIgnoreCase("") && gtypewallet.equalsIgnoreCase("gwallet")
                && gtypepaywave.equalsIgnoreCase("")) {
            chkIWallet.setChecked(false);
            chkGPaywave.setChecked(false);
            chkGWallet.setChecked(true);
        }

        chkGPaywave.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Gkash Paywave Payment + Gkash Wallet Payment Ticked");
                chkIWallet.setChecked(false);
                chkGWallet.setChecked(false);
                SharedPref.write(SharedPref.gtypepaywave, "gpaywave");
                SharedPref.write(SharedPref.type, "");
                Toast.makeText(configact.this, "Save success for g Payment", Toast.LENGTH_SHORT).show();
            } else {
                RollingLogger.i(TAG, "Gkash Paywave Payment + Gkash Wallet Payment UnTicked");
                SharedPref.write(SharedPref.gtypepaywave, "");
                String gtypewallet1 = SharedPref.read(SharedPref.gtypewallet, "");
                if (gtypewallet1.equalsIgnoreCase("")) {
                    SharedPref.write(SharedPref.type, "");
                }
            }
        });

        chkGWallet.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Gkash Paywave Payment + Ipay Wallet payment Ticked");
                chkIWallet.setChecked(false);
                chkGPaywave.setChecked(false);
                SharedPref.write(SharedPref.gtypewallet, "gwallet");
                SharedPref.write(SharedPref.type, "");
                Toast.makeText(configact.this, "Save success for g wallet payment", Toast.LENGTH_SHORT).show();
            } else {
                RollingLogger.i(TAG, "Gkash Paywave Payment + Ipay Wallet payment UnTicked");
                SharedPref.write(SharedPref.gtypewallet, "");
                String gtypepaywave1 = SharedPref.read(SharedPref.gtypepaywave, "");
                if (gtypepaywave1.equalsIgnoreCase("")) {
                    SharedPref.write(SharedPref.type, "");
                }
            }
        });

        chkIWallet.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                RollingLogger.i(TAG, "Ipay Wallet Payment Ticked");
                chkGWallet.setChecked(false);
                chkGPaywave.setChecked(false);
                SharedPref.write(SharedPref.type, "iwallet");
                SharedPref.write(SharedPref.gtypewallet, "");
                SharedPref.write(SharedPref.gtypepaywave, "");
                Toast.makeText(configact.this, "Save success for i wallet payment", Toast.LENGTH_SHORT).show();
            } else {
                RollingLogger.i(TAG, "Ipay Wallet Payment UnTicked");
                SharedPref.write(SharedPref.type, "");
            }
        });

        gkash.setOnClickListener(view -> {
            Intent intent = new Intent(configact.this, PayWaveMainActivity.class);
            startActivity(intent);
        });

        configdb = new configdata(this);
        btnclose.setOnClickListener(view -> {
            finish();
            RollingLogger.i(TAG, "Config Page End");
        });

        w30 = new wait30();
        w30.start();
        oncreate = true;

        congifModels = configdb.getAllItems();
        btnsettings.setOnClickListener(view -> {
            RollingLogger.i(TAG, "Open Setting button clicked");
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        });

        if (!congifModels.isEmpty()) {
            try {

                for (CongifModel config : congifModels) {
                    fid.setText(config.getFid());
                    mid.setText(config.getMid());
                    etip.setText(config.getIpforpaywave());
                    etmec.setText(config.getMerchantcode());
                    etmk.setText(config.getMerchantkey());
                }
            } catch (Exception ex) {
            }
        }

        btnsave.setOnClickListener(view -> {

            try {

                if ((!fid.getText().toString().isEmpty() && !mid.getText().toString().isEmpty() && !etip.getText().toString().isEmpty() && !etmec.getText().toString().isEmpty() && !etmk.getText().toString().isEmpty())) {

                    if (et_timer.getText().toString().equalsIgnoreCase("0")) {
                        Toast.makeText(configact.this, "Timer cannot input 0", Toast.LENGTH_LONG).show();
                        return;
                    }
                    configdb.deleteallitems();
                    configdb.addItem(new CongifModel(
                            etmec.getText().toString(),
                            etmk.getText().toString(),
                            etip.getText().toString(),
                            fid.getText().toString(),
                            mid.getText().toString(),
                            "1",  //toopid.getText().toString(), //old one, make it default
                            "1"  // tooppass.getText().toString() //old one, make it default
                    ));

                    SharedPref.init(configact.this);
                    SharedPref.write(SharedPref.TestDispensePassword, et_password.getText().toString());
                    SharedPref.write(SharedPref.SarawakPayMid, sarawakpay_mid.getText().toString());
                    SharedPref.write(SharedPref.SarawakPayMname, sarawakpay_mname.getText().toString());
                    SharedPref.write(SharedPref.TIMER, et_timer.getText().toString());
                    dbutis db = new dbutis(configact.this);

                    db.saveipforpaywave(etip.getText().toString());
                    db.setmerchantcode(etmec.getText().toString());
                    db.setmerchantkey(etmk.getText().toString());

                    final String merchantCode = etmec.getText().toString();
                    final String merchantKey = etmk.getText().toString();
                    final String franchiseId = fid.getText().toString();
                    final String machineId = mid.getText().toString();

                    FireLog.INSTANCE.updateMerchantCode(merchantCode);
                    FireLog.INSTANCE.updateMerchantKey(merchantKey);
                    FireLog.INSTANCE.updateFranchiseId(franchiseId);
                    FireLog.INSTANCE.updateMachineId(machineId);

                    SharedPref.write(SharedPref.MERCHANT_CODE, merchantCode);
                    SharedPref.write(SharedPref.MERCHANT_KEY, merchantKey);
                    SharedPref.write(SharedPref.FRANCHISE_ID, franchiseId);
                    SharedPref.write(SharedPref.MACHINE_ID, machineId);

                    RollingLogger.i(TAG, "Config Page End");

                    finish();

                } else
                    new SweetAlertDialog(configact.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Must fill all fields").show();

            } catch (Exception ex) {
                new SweetAlertDialog(configact.this, SweetAlertDialog.ERROR_TYPE).setTitleText("Sorry error found").show();
            }


        });

        setButtonChangeVendingVersion();
    }

    private void setButtonChangeVendingVersion() {
        final String vendingVersion = SharedPref.read(SharedPref.VENDING_VERSION, "");
        Tools.INSTANCE.logSimple("vendingVersion: " + vendingVersion);
        final TextView tvVendingVersion = findViewById(R.id.tv_vending_version);
        if (!vendingVersion.isEmpty()) {
            tvVendingVersion.setText(vendingVersion);
        }
        findViewById(R.id.btn_change_vending_version).setOnClickListener(view -> showDialogVendingVersion(tvVendingVersion));
    }

    private void showDialogVendingVersion(@NonNull final TextView tvVendingVersion) {
        DialogUtils.INSTANCE.showDialogVendingVersion(this, version -> {
            SharedPref.write(SharedPref.VENDING_VERSION, version);
            tvVendingVersion.setText(version);
        });
    }

    private void setButtonDisplayMain(@NonNull final Button button, final LayoutToDisplayType layoutToDisplayType) {
        button.setOnClickListener(view -> updateLayoutAndButtonStyle(layoutToDisplayType));
    }

    private void updateLayoutAndButtonStyle(LayoutToDisplayType layoutToDisplayType) {
        llGeneral.setVisibility(View.GONE);
        llPayment.setVisibility(View.GONE);
        llOthers.setVisibility(View.GONE);

        buttonGeneral.invalidate();
        buttonPayment.invalidate();
        buttonOthers.invalidate();

        TextViewCompat.setTextAppearance(buttonGeneral, R.style.PrimaryButtonOutlineStyle);// Default style
        TextViewCompat.setTextAppearance(buttonPayment, R.style.PrimaryButtonOutlineStyle);// Default style
        TextViewCompat.setTextAppearance(buttonOthers, R.style.PrimaryButtonOutlineStyle);// Default style

        buttonGeneral.setTextColor(getResources().getColor(R.color.colorBlack));
        buttonPayment.setTextColor(getResources().getColor(R.color.colorBlack));
        buttonOthers.setTextColor(getResources().getColor(R.color.colorBlack));

        buttonGeneral.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_outline_rounded_primary));
        buttonPayment.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_outline_rounded_primary));
        buttonOthers.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_outline_rounded_primary));


        switch (layoutToDisplayType) {
            case GENERAL:
                llGeneral.setVisibility(View.VISIBLE);
                TextViewCompat.setTextAppearance(buttonGeneral, R.style.PrimaryButtonStyle);
                buttonGeneral.setTextColor(getResources().getColor(R.color.colorWhite));
                buttonGeneral.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_background_rounded_primary));
                break;
            case PAYMENT:
                llPayment.setVisibility(View.VISIBLE);
                TextViewCompat.setTextAppearance(buttonPayment, R.style.PrimaryButtonStyle);
                buttonPayment.setTextColor(getResources().getColor(R.color.colorWhite));
                buttonPayment.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_background_rounded_primary));
                break;
            case OTHERS:
                llOthers.setVisibility(View.VISIBLE);
                TextViewCompat.setTextAppearance(buttonOthers, R.style.PrimaryButtonStyle);
                buttonOthers.setTextColor(getResources().getColor(R.color.colorWhite));
                buttonOthers.setBackground(AppCompatResources.getDrawable(this, R.drawable.btn_background_rounded_primary));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        threadintrupt = false;
        isuserpaying = false;
        if (!oncreate) {
            new wait30().start();
        } else {

            oncreate = false;

        }
        //  badgeint = 0;
    }

    private enum LayoutToDisplayType {
        GENERAL, PAYMENT, OTHERS
    }

    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt) {

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final CountDownTimer[] ct = new CountDownTimer[1];

                runOnUiThread(() -> {
                    try {
                        ct[0] = new CountDownTimer(10000, 1000) {
                            public void onTick(long millisUntilFinished) {

                                if (!isuserpaying) {
                                    if (millisUntilFinished > 0) {
                                        sweetAlertDialog.setContentText("This session will end in " + millisUntilFinished / 1000);
                                    } else {
                                        threadintrupt = true;
                                        try {
                                            sweetAlertDialog.dismissWithAnimation();
                                        } catch (Exception ex) {
                                        }
                                        configact.this.finish();
                                        ct[0].cancel();
                                    }
                                }
                            }

                            public void onFinish() {

                                try {
                                    sweetAlertDialog.dismissWithAnimation();
                                } catch (Exception ex) {
                                }
                                threadintrupt = true;
                                ct[0].cancel();
                                configact.this.finish();
                            }
                        };

                        if (!isuserpaying) {
                            showsweetalerttimeout(ct);
                            ct[0].start();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }
}