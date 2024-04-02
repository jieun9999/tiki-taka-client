package com.android.tiki_taka.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.FcmToken;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String CHANNEL_ID;
    String CHANNEL_NAME;
    int userId;
    ChatApiService service;

    // 서비스가 생성될 때 호출되며, 여기서 FCM 토큰을 요청하는 것이 좋습니다.
    @Override
    public void onCreate() {
        super.onCreate();
        userId = SharedPreferencesHelper.getUserId(getApplicationContext());
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ChatApiService.class);

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String TAG = "새 토큰 발급";
        Log.d(TAG, "Refreshed token: " + token);
        // fcm에서 각 기기에 발급해준 토큰
        // 새 토큰이 생성될 때마다 onNewToken 콜백이 호출됨

        // 토큰을 앱과 연결된 서버로 전송
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

        FcmToken fcmToken = new FcmToken(userId, token);
        service.saveToken(fcmToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().isSuccess()){
                         // success가 true일 때의 처리

                    }
                }else {
                    // success가 false일 때의 처리
                    Log.e("ERROR", "토큰 저장 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
        // 메시지가 FCM 서버로 성공적으로 전송된 후 호출
        // 일반적으로 이 메서드를 사용하여 사용자에게 직접적인 알림을 표시하는 경우는 드뭅니다. 대신, 메시지 전송 과정에서의 성공 여부를 추적하거나, 추가적인 후속 조치가 필요한 경우 사용합니다.
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("remoteMessage", String.valueOf(remoteMessage));

        // 이 메서드는 FCM으로부터 메시지를 수신할 때 호출됩니다
        CHANNEL_ID = "message_notifications";
        CHANNEL_NAME ="메세지 알림";

        // 1. 알림 채널 설정
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        // 2. 알림 생성 및 표시
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

        // 3. 알림 권한 확인
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
