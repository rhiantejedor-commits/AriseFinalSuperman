package com.example.arise.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class HabitEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String category; // e.g. "Daily Habits (Fixed)", "Study", etc.
    private String time;     // e.g. "07:00 AM"
    private String repeatDays; // Comma separated day names e.g. "Mon,Tue,Wed,Thu,Fri,Sat,Sun"
    private boolean isRecurring;
    private int xpReward;
    private boolean isCompleted;
    private String lastCompletedDate; // e.g. "YYYY-MM-DD"
    private int streakCount;
    private long createdAt;

    public HabitEntity() {
    }

    public HabitEntity(String title, String category, String time, String repeatDays, boolean isRecurring, int xpReward) {
        this.title = title;
        this.category = category;
        this.time = time;
        this.repeatDays = repeatDays;
        this.isRecurring = isRecurring;
        this.xpReward = xpReward;
        this.isCompleted = false;
        this.lastCompletedDate = "";
        this.streakCount = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public int getXpReward() {
        return xpReward;
    }

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void setLastCompletedDate(String lastCompletedDate) {
        this.lastCompletedDate = lastCompletedDate;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
