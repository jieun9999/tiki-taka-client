package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static long calculateDaysSince(String startDateStr) {
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            return -1;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(startDateStr);
            Calendar calendar = Calendar.getInstance();
            long startTime = startDate.getTime();
            long endTime = calendar.getTimeInMillis();
            long diffTime = endTime - startTime;
            return diffTime / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            return -1;
        }
    }
}


