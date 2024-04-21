package com.android.tiki_taka.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {

    private static final String PREFS_NAME = "notification_prefs";
    private static final String NOTIFICATIONS_KEY = "notifications";

    // 알림 리스트를 SharedPreferences에 저장
    public static void saveNotificationList(Context context, List<String> notifications) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // List를 JSON 배열로 변환
        JSONArray jsonArray = new JSONArray();
        for (String notification : notifications) {
            jsonArray.put(notification);
        }

        // JSON 배열을 문자열로 변환하여 저장
        if (!notifications.isEmpty()) {
            editor.putString(NOTIFICATIONS_KEY, jsonArray.toString());
        } else {
            editor.putString(NOTIFICATIONS_KEY, null);
        }
        editor.apply();
    }

    // SharedPreferences에서 알림 리스트 불러오기
    public static List<String> loadNotificationList(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        List<String> notifications = new ArrayList<>();

        try {
            // 저장된 문자열을 JSON 배열로 변환
            String json = prefs.getString(NOTIFICATIONS_KEY, null);
            if (json != null) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    notifications.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // 기존 알림 리스트에 새로운 알림 추가
    public static void addNotification(Context context, String newNotification) {
        List<String> notifications = loadNotificationList(context); // 기존 알림 리스트 불러오기
        notifications.add(newNotification); // 새 알림 추가
        saveNotificationList(context, notifications); // 변경된 리스트 저장
    }



    //쉐어드 초기화
    // 알림 리스트를 클리어하는 메서드
    public static void clearNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(NOTIFICATIONS_KEY); // 알림 리스트 키에 해당하는 데이터를 삭제
        editor.apply(); // 변경사항 적용
    }

    // 클라이언트 시간이 서버 시간보다 약 4초 정도 빠르므로, 시간 보정으로 +5초를 더한다
    public static String getAdjustedCurrentDateTime(){
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
            LocalDateTime adjustedTime = now.plusSeconds(5); // 현재 시간에 5초 추가
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return adjustedTime.format(formatter);
        }
        return null;
    }


}
