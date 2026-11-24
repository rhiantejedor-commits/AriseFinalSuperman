package com.example.arise.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.arise.R;
import com.example.arise.databinding.ActivityMainBinding;
import com.example.arise.notifications.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NotificationHelper.createNotificationChannel(this);

        setupBottomNavigation();
        setupFab();

        // Default to HomeFragment
        if (savedInstanceState == null) {
            selectTab(0);
        }

        binding.ivTopAvatar.setOnClickListener(v -> selectTab(3)); // Profile
    }

    private void setupBottomNavigation() {
        binding.tabHome.setOnClickListener(v -> selectTab(0));
        binding.tabHabits.setOnClickListener(v -> selectTab(1));
        binding.tabStats.setOnClickListener(v -> selectTab(2));
        binding.tabProfile.setOnClickListener(v -> selectTab(3));
    }

    private void selectTab(int index) {
        Fragment fragment;
        switch (index) {
            case 1:
                fragment = new HabitsFragment();
                updateNavUI(false, true, false, false);
                break;
            case 2:
                fragment = new StatsFragment();
                updateNavUI(false, false, true, false);
                break;
            case 3:
                fragment = new ProfileFragment();
                updateNavUI(false, false, false, true);
                break;
            case 0:
            default:
                fragment = new HomeFragment();
                updateNavUI(true, false, false, false);
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void updateNavUI(boolean homeActive, boolean habitsActive, boolean statsActive, boolean profileActive) {
        binding.tvNavHome.setVisibility(homeActive ? View.VISIBLE : View.GONE);
        binding.tvNavHabits.setVisibility(habitsActive ? View.VISIBLE : View.GONE);
        binding.tvNavStats.setVisibility(statsActive ? View.VISIBLE : View.GONE);
        binding.tvNavProfile.setVisibility(profileActive ? View.VISIBLE : View.GONE);

        binding.ivNavHome.setAlpha(homeActive ? 1.0f : 0.5f);
        binding.ivNavHabits.setAlpha(habitsActive ? 1.0f : 0.5f);
        binding.ivNavStats.setAlpha(statsActive ? 1.0f : 0.5f);
        binding.ivNavProfile.setAlpha(profileActive ? 1.0f : 0.5f);
    }

    private void setupFab() {
        binding.fabAddHabit.setOnClickListener(v -> {
            AddEditHabitBottomSheet.newInstance(null).show(getSupportFragmentManager(), "AddEditHabitBottomSheet");
        });
    }
}
