package com.example.arise.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.arise.data.local.HabitEntity;

import java.util.Calendar;

public class AlarmScheduler {
    public static final String EXTRA_HABIT_ID = "extra_habit_id";
    public static final String EXTRA_HABIT_TITLE = "extra_habit_title";
    public static final String EXTRA_HABIT_CATEGORY = "extra_habit_category";

    public static void scheduleAlarm(Context context, HabitEntity habit) {
        if (habit == null || habit.getTime() == null || habit.getTime().isEmpty()) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        try {
            String[] parts = habit.getTime().split(":");
            if (parts.length < 2) return;
            int hour = Integer.parseInt(parts[0].trim());
            String[] minParts = parts[1].trim().split(" ");
            int minute = Integer.parseInt(minParts[0].trim());
            if (minParts.length > 1 && minParts[1].equalsIgnoreCase("PM") && hour < 12) {
                hour += 12;
            } else if (minParts.length > 1 && minParts[1].equalsIgnoreCase("AM") && hour == 12) {
                hour = 0;
            }

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);

            if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(context, HabitAlarmReceiver.class);
            intent.putExtra(EXTRA_HABIT_ID, habit.getId());
            intent.putExtra(EXTRA_HABIT_TITLE, habit.getTitle());
            intent.putExtra(EXTRA_HABIT_CATEGORY, habit.getCategory());

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    (int) habit.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }
        } catch (Exception ignored) {
        }
    }

    public static void cancelAlarm(Context context, long habitId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(context, HabitAlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    (int) habitId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pi);
        }
    }
}
