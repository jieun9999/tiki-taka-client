package com.android.tiki_taka.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.FcmToken;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.ui.activity.Chat.ChatActivity;
import com.android.tiki_taka.utils.NotificationUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

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
        // fcm 에서 각 기기에 발급해준 토큰
        // 새 토큰이 생성될 때마다 onNewToken 콜백이 호출됨

        // 토큰을 앱과 연결된 서버로 전송
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // 자동 로그인이 되어 있는 경우만
        if(userId != -1){

            // 토큰이 호출될 수 있도록 함
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

    }

    // 메시지가 FCM 서버로 성공적으로 전송된 후 호출
    // 일반적으로 이 메서드를 사용하여 사용자에게 직접적인 알림을 표시하는 경우는 드뭅니다. 대신, 메시지 전송 과정에서의 성공 여부를 추적하거나, 추가적인 후속 조치가 필요한 경우 사용합니다.
    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);

    }

    // 이 메서드는 FCM으로부터 메시지를 수신할 때 호출됩니다
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String CHANNEL_ID = "message_notifications";
        String CHANNEL_NAME ="메세지 알림";
        // InboxStyle 알림용 고정된 NOTIFICATION_ID
        final int INBOX_STYLE_NOTIFICATION_ID = 1000;

        // 데이터 페이로드
        Map<String, String> data = remoteMessage.getData();
        Log.d("data", data.toString());
        String title = data.get("title");
        String userProfile = data.get("userProfile");
        String body = data.get("body");
        int messageId = Integer.parseInt(data.get("messageId"));
        int roomId = Integer.parseInt(data.get("roomId"));

        // Glide를 사용해 비동기적으로 이미지 로드 후 알림에 설정
        new Thread(() -> {
            try {
                // Glide를 사용하여 비트맵 동기적으로 로드
                Bitmap bitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(userProfile)
                        .submit()
                        .get();

                // 인텐트 생성 및 대상 액티비티 지정
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("messageId", messageId); // 알림에 메시지 정보 포함하기
                intent.putExtra("roomId", roomId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택 상에서 대상 액티비티 위에 있는 모든 액티비티들을 스택에서 제거한 뒤에 대상 액티비티를 시작
                // 클릭 시 데이터 전달 등 각각의 일을 처리하게하고 싶다면 pendingIntent의 request code(NOTIFICATION_ID)도 다르게 주어야함.
                PendingIntent pendingIntent = PendingIntent.getActivity(this, INBOX_STYLE_NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                // 이렇게 하면 사용자가 실수로 알림을 여러 번 클릭하더라도, 액티비티는 한 번만 열립니다.

                // 1. 알림 채널 설정
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(getApplicationContext());

                // 2. 알림 생성 및 표시
                NotificationCompat.Builder builder = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH); //긴급 (알림음o, 헤드업)
                        notificationManager.createNotificationChannel(channel);
                    }
                    builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                } else {
                    builder = new NotificationCompat.Builder(getApplicationContext());
                }

                // 쉐어드에서 저장한 알림 리스트 가져와서 새로운 알림 추가하고 쉐어드에 저장
                NotificationUtils.addNotification(getApplicationContext(), body);

                // InboxStyle 초기화
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                List<String> notificationMessages = NotificationUtils.loadNotificationList(getApplicationContext());
                // 제목 설정 (예: "5개의 새 메시지")
                inboxStyle.setBigContentTitle(notificationMessages.size() + "개의 새 메시지");
                // 모든 메시지를 InboxStyle에 추가
                for (String message : notificationMessages) {
                    inboxStyle.addLine(message);
                }

                // 단 하나의 inbox style ui
                // 새로운 알림이 도착할 때마다 이전의 개별 알림을 제거하고, 대신에 모든 메시지를 포함하는 InboxStyle 알림을 생성하여 표시합니다.

                // 개별 알림용 그룹 키
                final String NOTIFICATION_GROUP_KEY = "NOTIFICATION_GROUP_KEY";
                // 그룹 기반 알림의 경우, 모든 이전 알림 사라짐
                notificationManager.cancelAll();

                builder.setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.fluent_emoji_bell)// 알림의 작은 아이콘 설정
                        .setLargeIcon(bitmap) // 로드된 비트맵을 대형 아이콘으로 설정
                        .setStyle(inboxStyle)
                        .setAutoCancel(true) //사용자가 해당 알림을 클릭했을 때 알림 사라짐
                        .setContentIntent(pendingIntent)
                        .setGroup(NOTIFICATION_GROUP_KEY) // 모든 알림을 같은 그룹에 속하게 설정
                        .setGroupSummary(true);

                Notification notification = builder.build();

                // 3. 알림 권한 확인
                // API level 33 이상일 경우, 권한 여부를 확인해야 한다
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // 권한이 없을 경우, 권한 요청은 채팅 액티비티에서
                    } else {
                        // 권한이 있을 때의 알림 전송 로직
                        notificationManager.notify(INBOX_STYLE_NOTIFICATION_ID, notification);
                    }
                } else {
                    // API level 33 미만일 경우, 권한 없이 알림 전송 로직
                    notificationManager.notify(INBOX_STYLE_NOTIFICATION_ID, notification);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
