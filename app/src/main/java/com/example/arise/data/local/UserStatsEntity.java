package com.example.arise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_stats")
public class UserStatsEntity {
    @PrimaryKey
    private int id = 1;

    private String displayName;
    private String email;
    private int currentXp;
    private int currentLevel;
    private int totalDaysCompleted;
    private int longestStreak;
    private int totalMissed;

    public UserStatsEntity() {
        this.displayName = "User";
        this.email = "";
        this.currentXp = 0;
        this.currentLevel = 1;
        this.totalDaysCompleted = 0;
        this.longestStreak = 0;
        this.totalMissed = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getTotalDaysCompleted() {
        return totalDaysCompleted;
    }

    public void setTotalDaysCompleted(int totalDaysCompleted) {
        this.totalDaysCompleted = totalDaysCompleted;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getTotalMissed() {
        return totalMissed;
    }

    public void setTotalMissed(int totalMissed) {
        this.totalMissed = totalMissed;
    }
}
