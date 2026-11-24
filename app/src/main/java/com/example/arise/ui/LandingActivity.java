package com.example.arise.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.databinding.ActivityLandingBinding;

public class LandingActivity extends AppCompatActivity {

    private ActivityLandingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnGetStarted.setOnClickListener(v -> {
            FirebaseManager fm = FirebaseManager.getInstance(this);
            if (fm.isLoggedIn()) {
                startActivity(new Intent(LandingActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(LandingActivity.this, AuthActivity.class));
            }
            finish();
        });
    }
}
