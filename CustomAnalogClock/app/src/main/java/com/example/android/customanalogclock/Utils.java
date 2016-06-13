package com.example.android.customanalogclock;

/**
 * Created by lucky_luke on 6/9/2016.
 */
public class Utils {
    public static String formatTime(float hour, float minute) {
        int formatHour = Math.round(hour);
        int formatMinute = Math.round(minute);
        return formatHour + ":" + formatMinute;
    }
}
