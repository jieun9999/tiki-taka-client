package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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


    // 서버로부터 받은 LocalDateTime 객체를 "2023년 12월 25일 (월)"과 같은 형식의 문자열로 표시
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)");
            return dateTime.format(formatter);
        } else {
            // 안드로이드 O 미만의 경우 대체 구현
            // 예: SimpleDateFormat을 사용하거나 다른 라이브러리 활용
            return dateTime.toString(); // 기본 toString() 메서드를 사용하거나 다른 형식 지정
        }
    }
}


