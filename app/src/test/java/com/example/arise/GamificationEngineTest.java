package com.example.arise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.utils.GamificationEngine;

import org.junit.Before;
import org.junit.Test;

public class GamificationEngineTest {

    private UserStatsEntity stats;

    @Before
    public void setUp() {
        stats = new UserStatsEntity();
        stats.setCurrentXp(0);
        stats.setCurrentLevel(1);
        stats.setTotalDaysCompleted(0);
        stats.setLongestStreak(0);
    }

    @Test
    public void testXpCalculationAndLevelUpProgression() {
        // Level 1 requires 100 XP to level up
        assertEquals(100, GamificationEngine.getXpRequiredForLevel(1));
        assertEquals(200, GamificationEngine.getXpRequiredForLevel(2));

        HabitEntity studyHabit = new HabitEntity("Study Kotlin", "Study", "08:00 AM", "Mon,Wed,Fri", true, 20);

        // Complete study habit (base 20 XP)
        GamificationEngine.GamificationResult result1 = GamificationEngine.completeHabit(studyHabit, stats);
        assertEquals(20, result1.xpGained);
        assertFalse(result1.isLeveledUp);
        assertEquals(1, stats.getCurrentLevel());
        assertEquals(20, stats.getCurrentXp());

        // Complete fixed habit 3 times to get 30 + 30 + (30 + 50 streak bonus) = 140 XP added
        // 20 + 140 = 160 XP total -> level up to Level 2 with 60 leftover XP
        HabitEntity fixedHabit = new HabitEntity("Morning Water", "Daily Habits (Fixed)", "07:00 AM", "Mon,Tue,Wed", true, 30);

        GamificationEngine.completeHabit(fixedHabit, stats); // +30 -> 50 XP
        fixedHabit.setCompleted(false); // reset for test
        GamificationEngine.completeHabit(fixedHabit, stats); // +30 -> 80 XP
        fixedHabit.setCompleted(false); // reset for test
        GamificationEngine.GamificationResult resultStreak = GamificationEngine.completeHabit(fixedHabit, stats); // 3rd streak -> +30 + 50 = +80 XP -> total 160 XP

        assertTrue(resultStreak.isLeveledUp);
        assertEquals(2, stats.getCurrentLevel());
        assertEquals(60, stats.getCurrentXp());
    }

    @Test
    public void testStreakExclusiveToDailyHabitsFixed() {
        HabitEntity workHabit = new HabitEntity("Write Code", "Work", "09:00 AM", "Mon", true, 20);
        GamificationEngine.GamificationResult workResult = GamificationEngine.completeHabit(workHabit, stats);

        // Streak for non-fixed category should NOT increment (stays 0)
        assertEquals(0, workResult.newStreak);
        assertEquals(0, workHabit.getStreakCount());

        HabitEntity fixedHabit = new HabitEntity("Morning Stretch", "Daily Habits (Fixed)", "06:30 AM", "Mon,Tue", true, 30);
        GamificationEngine.GamificationResult fixedResult1 = GamificationEngine.completeHabit(fixedHabit, stats);

        // Streak for Daily Habits (Fixed) SHOULD increment to 1
        assertEquals(1, fixedResult1.newStreak);
        assertEquals(1, fixedHabit.getStreakCount());
    }

    @Test
    public void testStreakMilestoneBonusXp() {
        HabitEntity fixedHabit = new HabitEntity("Read Book", "Daily Habits (Fixed)", "09:00 PM", "Mon", true, 30);
        fixedHabit.setStreakCount(2); // Simulate 2 consecutive days already

        GamificationEngine.GamificationResult result = GamificationEngine.completeHabit(fixedHabit, stats);

        // 3-day streak grants 30 base XP + 50 bonus XP = 80 XP
        assertEquals(3, result.newStreak);
        assertEquals(80, result.xpGained);
    }
}
