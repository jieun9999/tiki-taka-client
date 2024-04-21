package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtils {
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

    // 2024-01-31 12:24:40 형식을 몇 초전, 몇 시간전, 몇 일전으로 변환함
    // 서버와 클라이언트 간의 시간 계산을 일관된 시간대(예: UTC)를 사용하여 처리
    public static String toRelativeTimeFromDb(String dateTimeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // 시간대를 UTC로 설정

        try {
            Date commentDate = sdf.parse(dateTimeString);
            if (commentDate != null) {
                long now = System.currentTimeMillis();
                long difference = now - commentDate.getTime();

                // 1분 미만 차이일 경우 "방금 전" 반환
                if (difference < DateUtils.MINUTE_IN_MILLIS) {
                    return "방금 전";
                }

                // 그렇지 않으면 상대적 시간 문자열 반환
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                return DateUtils.getRelativeTimeSpanString(commentDate.getTime(), cal.getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    // "방금 전", "x분 전", "x시간 전", "x일 전" 등의 텍스트를 생성

    public static String getDateWithoutTime(String dateTimeStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(dateTimeStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // 적절한 예외 처리 또는 로깅 필요
        }
    }

    public static String convertToAmPm(String dateString) {
        // 입력된 날짜 문자열을 파싱하기 위한 형식
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        // 출력할 날짜 문자열의 형식 (예: 8:32PM, 1:00AM)
        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

        try {
            Date date = inputFormat.parse(dateString);

            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    public static String nowDateFormatter() {
        LocalDateTime currentTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return currentTime.format(formatter);
        }

        return "";
    }
    
}


