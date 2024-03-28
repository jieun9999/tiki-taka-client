package com.android.tiki_taka.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.tiki_taka.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String CHANNEL_ID;
    String CHANNEL_NAME;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // 토큰을 클라우드 서버로 전송
        // 파라미터로 전달된 token이 앱을 구분하기 위한 고유한 키가 됨
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
        // 클라우드 서버에서 메세지를 전송하면 자동으로 호출
        // 이 메세드 내에서 메세지를 처리하여 사용자에게 알림을 보낸다
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // 메세지를 수신할때 호출되는 메세드
        // remoteMessage에 수신된 메세지가 전달된다.

        // 아래 코드는 메세지 정보를 받아 notification으로 등록하는 코드다
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);
        Notification notification = builder.build();


        // API level 33 이상일 경우, 권한 여부를 확인해야 한다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우, 권한 요청은 채팅 액티비티에서
            } else {
                // 권한이 있을 때의 알림 전송 로직
                notificationManager.notify(1, notification);
            }
        } else {
            // API level 33 미만일 경우, 권한 없이 알림 전송 로직
            notificationManager.notify(1, notification);
        }
    }

}
