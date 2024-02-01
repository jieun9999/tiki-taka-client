package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

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


    // 서버로부터 받은 2024-01-31 12:24:40" 를 "2023년 12월 25일 (월)"과 같은 형식의 문자열로 표시
    // 문자열 형태의 날짜를 파싱하여 Date 객체로 변환
    public static Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return inputFormat.parse(dateString);
    }

    // Date 객체를 "2023년 12월 25일 (월)" 형식의 문자열로 변환
    public static String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (E)", Locale.KOREAN);
        return outputFormat.format(date);
    }

    // 위의 두 메소드를 사용하여 날짜 문자열을 변환
    public static String convertDateString(String inputDateString) throws ParseException {
        Date date = parseDate(inputDateString);
        return formatDate(date);
    }


}


