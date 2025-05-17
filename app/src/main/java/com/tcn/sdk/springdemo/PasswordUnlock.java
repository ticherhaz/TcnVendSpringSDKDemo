package com.tcn.sdk.springdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tcn.sdk.springdemo.Utilities.RollingLogger;
import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.databinding.ActivityPasswordUnlockBinding;

public class PasswordUnlock extends AppCompatActivity {
    private final static String TAG = "PasswordUnlock";
    private final static String PASSWORD_FIRST_TIME = "dvs7889";
    private final static String PASSWORD = "dvss6899";
    private static final String INTENT_EXTRA_KEY = "keyName";
    private String type = "";
    private ActivityPasswordUnlockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntent();
        initView();

        setBtnConfirm();
        setBtnClose();
    }

    private void initView() {
        binding = ActivityPasswordUnlockBinding.inflate(getLayoutInflater());
        final View root = binding.getRoot();
        setContentView(root);
    }

    private void initIntent() {
        final Intent intent = getIntent();
        type = intent.getStringExtra("type");
    }

    private void setBtnConfirm() {
        binding.btnConfirm.setOnClickListener(view -> {

            SharedPref.init(PasswordUnlock.this);
            if (type.equalsIgnoreCase("1")) {
                handleType1();
            } else {
                handleOtherType();
            }
        });
    }

    private void handleType1() {
        final String adminPassword = SharedPref.read(SharedPref.adminpassword, "");
        final String enteredPassword = binding.tietPassword.getText().toString();

        RollingLogger.i(TAG, "password key in -" + enteredPassword);

        if (enteredPassword.equalsIgnoreCase(PASSWORD)) {
            navigateToConfigActivity();
            return;
        }

        if (adminPassword.isEmpty()) {
            if (enteredPassword.equals(PASSWORD_FIRST_TIME)) {
                navigateToConfigActivity();
            } else {
                showToastWrongPassword();
            }
        } else {
            if (enteredPassword.equalsIgnoreCase(adminPassword)) {
                navigateToConfigActivity();
            } else {
                showToastWrongPassword();
            }
        }
    }

    private void navigateToConfigActivity() {
        final Intent intent = new Intent(PasswordUnlock.this, configact.class); // Renamed class
        startActivity(intent);
        finish();
    }

    private void handleOtherType() {
        final String testDispensePassword = SharedPref.read(SharedPref.TestDispensePassword, "");
        final String enteredPassword = binding.tietPassword.getText().toString();

        if (enteredPassword.equalsIgnoreCase(testDispensePassword)) {
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(INTENT_EXTRA_KEY, "done");
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            showToastWrongPassword();
        }
    }

    private void showToastWrongPassword() {
        Toast.makeText(PasswordUnlock.this, "Wrong password", Toast.LENGTH_SHORT).show();
    }

    private void setBtnClose() {
        binding.btnClose.setOnClickListener(view -> finish());
    }
}