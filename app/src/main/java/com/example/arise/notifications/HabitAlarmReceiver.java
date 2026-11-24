package com.example.arise.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.arise.data.local.AppDatabase;
import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.utils.GamificationEngine;
import com.example.arise.utils.SoundHapticHelper;

public class HabitAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String action = intent.getAction();
        if (NotificationHelper.ACTION_MARK_COMPLETE.equals(action)) {
            long habitId = intent.getLongExtra(NotificationHelper.EXTRA_HABIT_ID, -1);
            if (habitId != -1) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    AppDatabase db = AppDatabase.getInstance(context);
                    HabitEntity habit = db.habitDao().getHabitById(habitId);
                    if (habit != null && !habit.isCompleted()) {
                        UserStatsEntity stats = db.userStatsDao().getUserStatsSync();
                        if (stats == null) {
                            stats = new UserStatsEntity();
                        }
                        GamificationEngine.completeHabit(habit, stats);
                        db.habitDao().update(habit);
                        db.userStatsDao().insertOrUpdate(stats);
                        SoundHapticHelper.playCompletionFeedback(context);
                    }
                });
            }
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancel((int) habitId);
            }
        } else {
            long habitId = intent.getLongExtra(AlarmScheduler.EXTRA_HABIT_ID, -1);
            String title = intent.getStringExtra(AlarmScheduler.EXTRA_HABIT_TITLE);
            String category = intent.getStringExtra(AlarmScheduler.EXTRA_HABIT_CATEGORY);

            if (title == null) title = "Habit Quest";
            if (category == null) category = "Daily Habits (Fixed)";

            NotificationHelper.showHabitReminder(context, habitId, title, category);
        }
    }
}
