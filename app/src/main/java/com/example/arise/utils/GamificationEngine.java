package com.example.arise.utils;

import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.local.UserStatsEntity;

public class GamificationEngine {

    public static final String CAT_DAILY_FIXED = "Daily Habits (Fixed)";

    public static class GamificationResult {
        public int xpGained;
        public boolean isLeveledUp;
        public int newLevel;
        public int newStreak;
        public boolean isFixedHabit;

        public GamificationResult(int xpGained, boolean isLeveledUp, int newLevel, int newStreak, boolean isFixedHabit) {
            this.xpGained = xpGained;
            this.isLeveledUp = isLeveledUp;
            this.newLevel = newLevel;
            this.newStreak = newStreak;
            this.isFixedHabit = isFixedHabit;
        }
    }

    public static int getXpRequiredForLevel(int level) {
        return Math.max(100, level * 100);
    }

    public static GamificationResult completeHabit(HabitEntity habit, UserStatsEntity stats) {
        boolean isFixed = CAT_DAILY_FIXED.equalsIgnoreCase(habit.getCategory());
        int baseXp = isFixed ? 30 : 20;
        int bonusXp = 0;
        int newStreak = habit.getStreakCount();

        if (habit.isCompleted()) {
            // Undo completion
            habit.setCompleted(false);
            stats.setCurrentXp(Math.max(0, stats.getCurrentXp() - baseXp));
            stats.setTotalDaysCompleted(Math.max(0, stats.getTotalDaysCompleted() - 1));
            return new GamificationResult(-baseXp, false, stats.getCurrentLevel(), habit.getStreakCount(), isFixed);
        }

        // Complete habit
        habit.setCompleted(true);
        String todayStr = DateUtils.getTodayDateString();
        habit.setLastCompletedDate(todayStr);
        stats.setTotalDaysCompleted(stats.getTotalDaysCompleted() + 1);

        // STREAK LOGIC: Strictly exclusive to Daily Habits (Fixed)
        if (isFixed) {
            newStreak = habit.getStreakCount() + 1;
            habit.setStreakCount(newStreak);

            if (newStreak > stats.getLongestStreak()) {
                stats.setLongestStreak(newStreak);
            }

            // Milestone streak bonus XP
            if (newStreak == 3) {
                bonusXp = 50;
            } else if (newStreak == 7) {
                bonusXp = 150;
            }
        }

        int totalEarnedXp = baseXp + bonusXp;
        int updatedXp = stats.getCurrentXp() + totalEarnedXp;
        int currentLevel = stats.getCurrentLevel();
        boolean leveledUp = false;

        // Check auto level-up
        int requiredXp = getXpRequiredForLevel(currentLevel);
        while (updatedXp >= requiredXp) {
            updatedXp -= requiredXp;
            currentLevel++;
            leveledUp = true;
            requiredXp = getXpRequiredForLevel(currentLevel);
        }

        stats.setCurrentXp(updatedXp);
        stats.setCurrentLevel(currentLevel);

        return new GamificationResult(totalEarnedXp, leveledUp, currentLevel, newStreak, isFixed);
    }
}
