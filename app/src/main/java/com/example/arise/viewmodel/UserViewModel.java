package com.example.arise.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.arise.data.local.UserStatsEntity;
import com.example.arise.data.remote.FirebaseManager;
import com.example.arise.data.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<UserStatsEntity> userStats;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
        userStats = repository.getUserStats();
    }

    public LiveData<UserStatsEntity> getUserStats() {
        return userStats;
    }

    public void updateUserProfile(String displayName, String email) {
        repository.updateUserProfile(displayName, email);
    }

    public void syncNow(FirebaseManager.SyncCallback callback) {
        repository.syncNow(callback);
    }

    public void login(String email, String password, FirebaseManager.AuthCallback callback) {
        repository.login(email, password, callback);
    }

    public void register(String email, String password, String displayName, FirebaseManager.AuthCallback callback) {
        repository.register(email, password, displayName, callback);
    }

    public boolean isLoggedIn() {
        return repository.isLoggedIn();
    }

    public void logout() {
        repository.logout();
    }
}
