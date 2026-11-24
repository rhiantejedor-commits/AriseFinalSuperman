package com.example.arise.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HabitEntity habit);

    @Update
    void update(HabitEntity habit);

    @Delete
    void delete(HabitEntity habit);

    @Query("SELECT * FROM habits ORDER BY id DESC")
    LiveData<List<HabitEntity>> getAllHabits();

    @Query("SELECT * FROM habits ORDER BY id DESC")
    List<HabitEntity> getAllHabitsSync();

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    HabitEntity getHabitById(long id);

    @Query("SELECT * FROM habits WHERE category = :category ORDER BY id DESC")
    LiveData<List<HabitEntity>> getHabitsByCategory(String category);

    @Query("DELETE FROM habits")
    void deleteAll();
}
