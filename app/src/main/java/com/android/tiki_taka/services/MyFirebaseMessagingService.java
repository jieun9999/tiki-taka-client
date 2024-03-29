package com.android.tiki_taka.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.tiki_taka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String CHANNEL_ID;
    String CHANNEL_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        // 서비스가 생성될 때 호출되며, 여기서 FCM 토큰을 요청하는 것이 좋습니다.
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String TAG = "토큰 발급";
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log
                        String msg = "FCM Token: " + token;
                        Log.d(TAG, msg);
                    }

                });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String TAG = "새 토큰 발급";
        Log.d(TAG, "Refreshed token: " + token);
        // 새 토큰이 생성될 때마다 onNewToken 콜백이 호출됨

        // 토큰을 앱 서버로 전송
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // 여기에 서버로 토큰을 보내는 코드를 추가하세요.
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
