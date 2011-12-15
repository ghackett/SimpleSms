package com.episode6.android.simplesms.app.util;

import java.util.Calendar;

public class DateUtil {
    public static boolean isToday(long timeInMillis) {
        Calendar today = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        d.setTimeInMillis(timeInMillis);
        
        if (d.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) && d.get(Calendar.MONTH) == today.get(Calendar.MONTH) && d.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }
}
