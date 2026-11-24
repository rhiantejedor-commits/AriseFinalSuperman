package com.example.arise.model;

public class Achievement {
    private final String id;
    private final String title;
    private final String description;
    private final int iconResId;
    private final boolean isUnlocked;

    public Achievement(String id, String title, String description, int iconResId, boolean isUnlocked) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.isUnlocked = isUnlocked;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }
}
