package com.example.arise.model;

public enum HabitCategory {
    DAILY_FIXED("Daily Habits (Fixed)", 30),
    STUDY("Study", 20),
    WORK("Work", 20),
    EXERCISE("Exercise", 20),
    PERSONAL("Personal", 20);

    private final String displayName;
    private final int defaultXp;

    HabitCategory(String displayName, int defaultXp) {
        this.displayName = displayName;
        this.defaultXp = defaultXp;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultXp() {
        return defaultXp;
    }

    public static HabitCategory fromDisplayName(String name) {
        if (name == null) return PERSONAL;
        for (HabitCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(name)) {
                return category;
            }
        }
        return PERSONAL;
    }
}
