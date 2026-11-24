package com.example.arise.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.arise.databinding.FragmentStatsBinding;
import com.example.arise.model.Achievement;
import com.example.arise.viewmodel.StatsViewModel;

import java.util.List;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private StatsViewModel statsViewModel;
    private AchievementAdapter achievementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statsViewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        achievementAdapter = new AchievementAdapter();
        binding.rvAchievements.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAchievements.setAdapter(achievementAdapter);
    }

    private void observeViewModel() {
        statsViewModel.getUserStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvStatCompletedVal.setText(String.valueOf(stats.getTotalDaysCompleted()));
                binding.tvStatStreakVal.setText(String.valueOf(stats.getLongestStreak()));
                binding.tvStatMissedVal.setText(String.valueOf(stats.getTotalMissed()));

                int totalTracked = stats.getTotalDaysCompleted() + stats.getTotalMissed();
                int rate = totalTracked > 0 ? (stats.getTotalDaysCompleted() * 100 / totalTracked) : 100;
                binding.tvStatRateVal.setText(rate + "%");

                List<Achievement> achievements = statsViewModel.calculateAchievements(stats);
                achievementAdapter.setAchievements(achievements);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
