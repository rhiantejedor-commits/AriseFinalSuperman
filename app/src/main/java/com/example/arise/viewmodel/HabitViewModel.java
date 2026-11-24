package com.example.arise.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.arise.data.local.HabitEntity;
import com.example.arise.data.repository.HabitRepository;
import com.example.arise.notifications.AlarmScheduler;
import com.example.arise.utils.GamificationEngine;

import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final HabitRepository repository;
    private final LiveData<List<HabitEntity>> allHabits;

    public HabitViewModel(@NonNull Application application) {
        super(application);
        repository = new HabitRepository(application);
        allHabits = repository.getAllHabits();
    }

    public LiveData<List<HabitEntity>> getAllHabits() {
        return allHabits;
    }

    public LiveData<List<HabitEntity>> getHabitsByCategory(String category) {
        return repository.getHabitsByCategory(category);
    }

    public void insert(HabitEntity habit) {
        repository.insert(habit, insertedHabit -> {
            AlarmScheduler.scheduleAlarm(getApplication(), insertedHabit);
        });
    }

    public void update(HabitEntity habit) {
        repository.update(habit);
        AlarmScheduler.scheduleAlarm(getApplication(), habit);
    }

    public void delete(HabitEntity habit) {
        repository.delete(habit);
        AlarmScheduler.cancelAlarm(getApplication(), habit.getId());
    }

    public void toggleCompleteHabit(HabitEntity habit, HabitRepository.OnHabitCompletedCallback callback) {
        repository.toggleCompleteHabit(habit, callback);
    }
}
