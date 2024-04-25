package com.android.tiki_taka.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.response.SuccessAndMessageResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

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

    public static void NotificationOnSuccess(Context context, Class<?> targetActivity, int Id, int NOTIFICATION_ID){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O 이상에서는 채널이 필요합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("upload_status", "Upload Status", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        //  여러 알림이 있을 때, 각각의 알림에 대해 다른 행동(예: 다른 메시지 보여주기)을 하고 싶다면, 각각의 PendingIntent에 대해 고유한 REQUEST_CODE를 할당해야 합니다.
        int REQUEST_CODE =  (int) System.currentTimeMillis();

        // 성공 알림
        // 인텐트 생성 및 대상 액티비티 지정
        Intent intent = new Intent(context, targetActivity);
        intent.putExtra("storyNotification", true);
        intent.putExtra("Id", Id); // 알림에 메시지 정보 포함하기
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택 상에서 대상 액티비티 위에 있는 모든 액티비티들을 스택에서 제거한 뒤에 대상 액티비티를 시작
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, "upload_status")
                .setContentTitle("업로드 완료")
                .setContentText("업로드한 동영상 카드를 확인해보세요!")
                .setSmallIcon(R.drawable.baseline_message_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void NotificationOnFailure(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O 이상에서는 채널이 필요합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("upload_status", "Upload Status", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // 여러 개의 개별 알림 생성
        int NOTIFICATION_ID = (int) System.currentTimeMillis();

        // 실패 알림
        Notification notification = new NotificationCompat.Builder(context, "upload_status")
                .setContentTitle("Upload Failed")
                .setContentText("동영상 카드 업로드에 실패했습니다.")
                .setSmallIcon(R.drawable.baseline_sms_failed_24)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // 업로드 진행률과 관련된 알림
    public static void initProgressNotification(Context context, int NOTIFICATION_ID){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("upload_channel", "Upload Progress", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "upload_channel")
                .setContentTitle("업로드 준비")
                .setContentText("잠시만 기다려주세요.")
                .setSmallIcon(R.drawable.baseline_upload_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(100, 0, false);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void updateProgressNotification(Context context, int progress, int NOTIFICATION_ID){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("upload_channel", "Upload Progress", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "upload_channel")
                .setContentTitle("업로드 중")
                .setSmallIcon(R.drawable.baseline_upload_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(100, progress, false);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


}
