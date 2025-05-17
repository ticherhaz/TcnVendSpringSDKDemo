package com.tcn.sdk.springdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tcn.sdk.springdemo.Utilities.SharedPref;
import com.tcn.sdk.springdemo.databinding.ActivityPasswordChangeBinding;

public class PasswordChange extends AppCompatActivity {

    private ActivityPasswordChangeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordChangeBinding.inflate(getLayoutInflater());
        final View root = binding.getRoot();
        setContentView(root);

        setButtonPassword();
        setButtonClose();
    }

    private void setButtonClose() {
        binding.btnClose.setOnClickListener(view -> finish());
    }

    private void setButtonPassword() {
        binding.btnConfirm.setOnClickListener(view -> {
            if (!isValidPasswordInput()) {
                return;
            }

            SharedPref.write(SharedPref.ADMIN_PASSWORD, binding.tietPassword.getText().toString());
            Toast.makeText(PasswordChange.this, "New Password save successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean isValidPasswordInput() {
        final String password = binding.tietPassword.getText().toString();
        final String confirmPassword = binding.tietConfirmPassword.getText().toString();

        if (!password.equalsIgnoreCase(confirmPassword)) {
            Toast.makeText(PasswordChange.this, "Password not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(PasswordChange.this, "Password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(PasswordChange.this, "Confirm Password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}