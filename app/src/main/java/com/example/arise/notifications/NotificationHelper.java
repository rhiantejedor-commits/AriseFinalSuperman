package com.example.arise.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.arise.R;
import com.example.arise.ui.MainActivity;

public class NotificationHelper {
    public static final String CHANNEL_ID = "arise_habit_reminders";
    public static final String CHANNEL_NAME = "Habit Reminders";
    public static final String ACTION_MARK_COMPLETE = "com.example.arise.ACTION_MARK_COMPLETE";
    public static final String EXTRA_HABIT_ID = "extra_habit_id";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for daily habit quests and reminders");
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    public static void showHabitReminder(Context context, long habitId, String habitTitle, String category) {
        createNotificationChannel(context);

        // Open App Intent
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPI = PendingIntent.getActivity(
                context,
                (int) habitId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Mark Complete Action Intent
        Intent completeIntent = new Intent(context, HabitAlarmReceiver.class);
        completeIntent.setAction(ACTION_MARK_COMPLETE);
        completeIntent.putExtra(EXTRA_HABIT_ID, habitId);
        PendingIntent completePI = PendingIntent.getBroadcast(
                context,
                (int) habitId + 10000,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arise_badge_small)
                .setContentTitle("Time for: " + habitTitle)
                .setContentText("Category: " + category + " • Complete now to earn XP!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(openPI)
                .addAction(R.drawable.ic_check, "Mark Complete", completePI);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) habitId, builder.build());
        }
    }
}
