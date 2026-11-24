package com.example.arise.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a", Locale.US);

    public static String getTodayDateString() {
        return DATE_FORMAT.format(new Date());
    }

    public static String formatTime(int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        return TIME_FORMAT.format(cal.getTime());
    }

    public static String getTodayDayName() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        return dayFormat.format(new Date()); // Mon, Tue, etc.
    }

    public static boolean isTodayScheduled(String repeatDays) {
        if (repeatDays == null || repeatDays.isEmpty()) return true;
        String today = getTodayDayName();
        return repeatDays.toLowerCase(Locale.US).contains(today.toLowerCase(Locale.US));
    }
}
