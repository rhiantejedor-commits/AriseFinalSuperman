package com.example.arise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.arise.R;
import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.databinding.ActivityAuthBinding;
import com.example.arise.viewmodel.UserViewModel;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private UserViewModel userViewModel;
    private boolean isRegisterMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.toggleAuthMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == binding.btnModeRegister.getId()) {
                    isRegisterMode = true;
                    binding.tilName.setVisibility(View.VISIBLE);
                    binding.btnSubmitAuth.setText(R.string.register);
                } else {
                    isRegisterMode = false;
                    binding.tilName.setVisibility(View.GONE);
                    binding.btnSubmitAuth.setText(R.string.login);
                }
            }
        });

        binding.btnSubmitAuth.setOnClickListener(v -> performAuth());

        binding.btnGuestMode.setOnClickListener(v -> {
            userViewModel.updateUserProfile("Guest User", "");
            navigateToMain();
        });
    }

    private void performAuth() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isRegisterMode && name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseManager.AuthCallback callback = new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String uid, String userEmail, String displayName) {
                String finalName = (displayName != null && !displayName.trim().isEmpty())
                        ? displayName.trim()
                        : FirebaseManager.extractNameFromEmail(userEmail);
                userViewModel.updateUserProfile(finalName, userEmail);
                Toast.makeText(AuthActivity.this, "Welcome, " + finalName + "!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(AuthActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        };

        if (isRegisterMode) {
            userViewModel.register(email, password, name, callback);
        } else {
            userViewModel.login(email, password, callback);
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }
}