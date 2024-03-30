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
