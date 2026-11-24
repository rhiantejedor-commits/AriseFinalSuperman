package com.example.arise.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserStatsEntity stats);

    @Update
    void update(UserStatsEntity stats);

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    LiveData<UserStatsEntity> getUserStats();

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    UserStatsEntity getUserStatsSync();
}
