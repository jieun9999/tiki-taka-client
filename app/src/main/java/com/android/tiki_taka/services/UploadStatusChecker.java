package com.android.tiki_taka.services;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.android.tiki_taka.ui.activity.Album.WithCommentStoryCard3;
import com.android.tiki_taka.utils.NotificationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UploadStatusChecker{
    private DatabaseReference databaseReference;
    //WorkManager에서 관리하는 백그라운드 스레드들은 Looper를 가지고 있지 않아서, 새롭게 스레드를 생성한 다음 핸들러를 초기화해줌!
    private HandlerThread handlerThread;
    private Handler handler;
    private final int INTERVAL = 1000; // 1초마다 실행
    String parentKey;
    private Context context; // UploadVideoWorker에게 전달받은 컨텍스트
    public int NOTIFICATION_ID;

    public UploadStatusChecker(String parentKey, Context context, int NOTIFICATION_ID) {
        // Firebase Realtime Database 초기화
        this.databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(parentKey);
        this.parentKey = parentKey;
        this.context = context;
        this.NOTIFICATION_ID = NOTIFICATION_ID;

        //HandlerThread 초기화
        this.handlerThread = new HandlerThread("StatusCheckerThread");
        handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
    }

    private Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            checkUploadStatus(); // 진행 상태 체크 메소드 실행
            // 예약된 다음 실행을 위해 핸들러에 Runnable 재등록
            handler.postDelayed(this, INTERVAL);
        }
    };

    public void startChecking() {
        statusChecker.run(); // 최초 실행
    }

    public void stopChecking() {
        handler.removeCallbacks(statusChecker); // 실행 취소
        handlerThread.quit(); // HandlerThread 종료
    }

    public void checkUploadStatus(){
        // Realtime Database에서 해당 key에 대한 데이터 가져오기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 여기서 progress 값 가져오기

                    Long updatedAt = dataSnapshot.child("updated_at").getValue(Long.class);
                    updatedAt *= 1000; // 초 단위를 밀리초 단위로 변환
                    Long currentTime = System.currentTimeMillis();
                    long timeDifference = currentTime - updatedAt;
//                    Log.d("updatedAt", String.valueOf(updatedAt));
//                    Log.d("currentTime", String.valueOf(currentTime));
//                    Log.d("timeDifference", String.valueOf(timeDifference));

                    if(timeDifference > 10000){
                        // 특히 같은 이름의 파일을 여러 번 업로드하는 경우에도 각 업로드의 상태를 개별적으로 확인할 수 있게 한다
                        NotificationUtils.initProgressNotification(context, NOTIFICATION_ID);

                    }else {
                        Integer progress = dataSnapshot.child("progress").getValue(Integer.class);
                        if (progress == 100) {
                           // 알림은 UploadVideoWorker 클래스에서

                        }else {
                            // 진행률을 UI에 업데이트 (UI 스레드에서 실행해야 함)
                            NotificationUtils.updateProgressNotification(context, progress, NOTIFICATION_ID);
                        }
                    }

                }else {
                    NotificationUtils.initProgressNotification(context, NOTIFICATION_ID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
                Log.e("databaseError", "Failed to read from database: " + databaseError.getMessage());
            }
        });
    }

}
