package com.example.arise.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.arise.data.local.AppDatabase;
import com.example.arise.data.local.HabitDao;
import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.local.UserStatsDao;
import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.utils.GamificationEngine;

import java.util.List;

public class HabitRepository {
    private final HabitDao habitDao;
    private final UserStatsDao userStatsDao;
    private final FirebaseManager firebaseManager;
    private final LiveData<List<HabitEntity>> allHabits;

    public HabitRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.habitDao = db.habitDao();
        this.userStatsDao = db.userStatsDao();
        this.firebaseManager = FirebaseManager.getInstance(context);
        this.allHabits = habitDao.getAllHabits();
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return allHabits;
    }

    public LiveData<List<HabitEntity>> getHabitsByCategory(String category) {
        if ("All".equalsIgnoreCase(category) || category == null) {
            return allHabits;
        }
        return habitDao.getHabitsByCategory(category);
    }

    public void insert(HabitEntity habit, OnHabitInsertedListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = habitDao.insert(habit);
            habit.setId(id);
            if (listener != null) {
                listener.onInserted(habit);
            }
        });
    }

    public interface OnHabitInsertedListener {
        void onInserted(HabitEntity habit);
    }

    public void update(HabitEntity habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> habitDao.update(habit));
    }

    public void delete(HabitEntity habit) {
        AppDatabase.databaseWriteExecutor.execute(() -> habitDao.delete(habit));
    }

    public void toggleCompleteHabit(HabitEntity habit, OnHabitCompletedCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserStatsEntity stats = userStatsDao.getUserStatsSync();
            if (stats == null) {
                stats = new UserStatsEntity();
            }

            GamificationEngine.GamificationResult result = GamificationEngine.completeHabit(habit, stats);
            habitDao.update(habit);
            userStatsDao.insertOrUpdate(stats);

            firebaseManager.syncUserStatsToCloud(stats, null);

            if (callback != null) {
                callback.onComplete(result);
            }
        });
    }

    public interface OnHabitCompletedCallback {
        void onComplete(GamificationEngine.GamificationResult result);
    }
}
