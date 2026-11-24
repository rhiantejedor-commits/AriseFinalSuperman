package com.example.arise.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.arise.data.local.AppDatabase;
import com.example.arise.data.local.UserStatsDao;
import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.data.remote.FirebaseManager;

public class UserRepository {
    private final UserStatsDao userStatsDao;
    private final FirebaseManager firebaseManager;
    private final LiveData<UserStatsEntity> userStats;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.userStatsDao = db.userStatsDao();
        this.firebaseManager = FirebaseManager.getInstance(context);
        this.userStats = userStatsDao.getUserStats();
    }

    public LiveData<UserStatsEntity> getUserStats() {
        return userStats;
    }

    public void updateUserProfile(String displayName, String email) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserStatsEntity stats = userStatsDao.getUserStatsSync();
            if (stats == null) {
                stats = new UserStatsEntity();
            }
            stats.setDisplayName(displayName);
            stats.setEmail(email);
            userStatsDao.insertOrUpdate(stats);
            firebaseManager.syncUserStatsToCloud(stats, null);
        });
    }

    public void syncNow(FirebaseManager.SyncCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserStatsEntity stats = userStatsDao.getUserStatsSync();
            if (stats != null) {
                firebaseManager.syncUserStatsToCloud(stats, callback);
            } else if (callback != null) {
                callback.onSuccess();
            }
        });
    }

    public void login(String email, String password, FirebaseManager.AuthCallback callback) {
        firebaseManager.login(email, password, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String uid, String userEmail, String displayName) {
                updateUserProfile(displayName, userEmail);
                if (callback != null) {
                    callback.onSuccess(uid, userEmail, displayName);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (callback != null) {
                    callback.onError(errorMessage);
                }
            }
        });
    }

    public void register(String email, String password, String displayName, FirebaseManager.AuthCallback callback) {
        firebaseManager.register(email, password, displayName, new FirebaseManager.AuthCallback() {
            @Override
            public void onSuccess(String uid, String userEmail, String name) {
                updateUserProfile(name, userEmail);
                if (callback != null) {
                    callback.onSuccess(uid, userEmail, name);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (callback != null) {
                    callback.onError(errorMessage);
                }
            }
        });
    }

    public boolean isLoggedIn() {
        return firebaseManager.isLoggedIn();
    }

    public void logout() {
        firebaseManager.signOut();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserStatsEntity stats = userStatsDao.getUserStatsSync();
            if (stats != null) {
                stats.setDisplayName("User");
                stats.setEmail("");
                userStatsDao.insertOrUpdate(stats);
            }
        });
    }
}
