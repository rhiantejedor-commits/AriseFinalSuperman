package com.example.arise.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.arise.data.local.AppDatabase;
import com.example.arise.data.local.HabitEntity;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                List<HabitEntity> habits = db.habitDao().getAllHabitsSync();
                if (habits != null) {
                    for (HabitEntity habit : habits) {
                        AlarmScheduler.scheduleAlarm(context, habit);
                    }
                }
            });
        }
    }
}
