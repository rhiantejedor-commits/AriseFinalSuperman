package com.example.arise.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arise.data.local.HabitEntity;
import com.example.arise.databinding.FragmentHomeBinding;
import com.example.arise.utils.DateUtils;
import com.example.arise.utils.GamificationEngine;
import com.example.arise.utils.SoundHapticHelper;
import com.example.arise.viewmodel.HabitViewModel;
import com.example.arise.viewmodel.UserViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HabitAdapter.OnHabitActionListener {

    private FragmentHomeBinding binding;
    private HabitViewModel habitViewModel;
    private UserViewModel userViewModel;
    private HabitAdapter habitAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        habitAdapter = new HabitAdapter(this);
        binding.rvDailyQuests.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDailyQuests.setAdapter(habitAdapter);
    }

    private void observeViewModel() {
        // Observe User Stats (Greeting Name, Level, XP)
        userViewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null && binding != null) {
                String vmName = stats.getDisplayName();
                String shortGreeting = com.example.arise.data.remote.FirebaseManager.toShortGreetingName(vmName);
                binding.tvGreetingName.setText(shortGreeting + "!");

                binding.tvLevelBadge.setText("LVL " + stats.getCurrentLevel());

                int reqXp = GamificationEngine.getXpRequiredForLevel(stats.getCurrentLevel());
                binding.progressLevelXp.setMax(reqXp);
                binding.progressLevelXp.setProgress(stats.getCurrentXp());
                binding.tvXpText.setText(stats.getCurrentXp() + "/" + reqXp + " XP");
            }
        });

        // Observe Habits for Daily Quest
        habitViewModel.getAllHabits().observe(getViewLifecycleOwner(), habits -> {
            List<HabitEntity> dailyQuests = new ArrayList<>();
            if (habits != null) {
                for (HabitEntity h : habits) {
                    if (DateUtils.isTodayScheduled(h.getRepeatDays())) {
                        dailyQuests.add(h);
                    }
                }
            }

            habitAdapter.setHabits(dailyQuests);
            if (dailyQuests.isEmpty()) {
                binding.llEmptyState.setVisibility(View.VISIBLE);
                binding.rvDailyQuests.setVisibility(View.GONE);
            } else {
                binding.llEmptyState.setVisibility(View.GONE);
                binding.rvDailyQuests.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onToggleComplete(HabitEntity habit) {
        habitViewModel.toggleCompleteHabit(habit, result -> {
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    SoundHapticHelper.playCompletionFeedback(requireContext());
                    if (result.xpGained > 0) {
                        String msg = "+" + result.xpGained + " XP Earned!";
                        if (result.isFixedHabit && result.newStreak > 1) {
                            msg += " Flame Streak: " + result.newStreak + " days!";
                        }
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    if (result.isLeveledUp) {
                        showLevelUpDialog(result.newLevel);
                    }
                });
            }
        });
    }

    private void showLevelUpDialog(int newLevel) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("LEVEL UP!")
                .setMessage("Congratulations! You reached Level " + newLevel + "! Keep up the amazing momentum.")
                .setPositiveButton("Awesome!", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onEdit(HabitEntity habit) {
        AddEditHabitBottomSheet.newInstance(habit).show(getParentFragmentManager(), "AddEditHabitBottomSheet");
    }

    @Override
    public void onDelete(HabitEntity habit) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete '" + habit.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> habitViewModel.delete(habit))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}