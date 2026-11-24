package com.example.arise.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.arise.R;
import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.data.repository.UserRepository;
import com.example.arise.model.Achievement;

import java.util.ArrayList;
import java.util.List;

public class StatsViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final LiveData<UserStatsEntity> userStats;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userStats = userRepository.getUserStats();
    }

    public LiveData<UserStatsEntity> getUserStats() {
        return userStats;
    }

    public List<Achievement> calculateAchievements(UserStatsEntity stats) {
        List<Achievement> list = new ArrayList<>();
        if (stats == null) return list;

        int completed = stats.getTotalDaysCompleted();
        int streak = stats.getLongestStreak();
        int level = stats.getCurrentLevel();

        list.add(new Achievement("1", "First Step", "Complete your very first habit", R.drawable.ic_check_circle_filled, completed >= 1));
        list.add(new Achievement("2", "3-Day Flame", "Maintain a 3-day streak in Daily Habits (Fixed)", R.drawable.ic_flame, streak >= 3));
        list.add(new Achievement("3", "7-Day Warrior", "Maintain a 7-day streak in Daily Habits (Fixed)", R.drawable.ic_flame, streak >= 7));
        list.add(new Achievement("4", "Level 5 Achiever", "Reach Level 5 in Arise", R.drawable.ic_star, level >= 5));
        list.add(new Achievement("5", "Quest Master", "Complete 20 total daily quests", R.drawable.ic_award, completed >= 20));

        return list;
    }
}
