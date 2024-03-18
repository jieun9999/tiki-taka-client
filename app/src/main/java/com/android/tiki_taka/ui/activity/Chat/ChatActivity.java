package com.android.tiki_taka.ui.activity.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ChatClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    private int userId;
    private int chatRoomId;
    private ChatClient chatClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupUIListeners();
        initializeUserAndRoomIds();

        try {
            chatClient = new ChatClient("52.79.41.79", 1234);
            chatClient.sendUserId(userId);
            // 서버가 쉐어드에 저장되어 있는 userId에 접근하지 못하기 때문에, 서버에게 직접 id를 전송해야 함
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initializeUserAndRoomIds() {
        userId = SharedPreferencesHelper.getUserId(this);
        chatRoomId = SharedPreferencesHelper.getRoomId(this);
    }

    private void setupUIListeners(){
        ImageView cancelBtn = findViewById(R.id.imageView36);
        cancelBtn.setOnClickListener( v -> finish());
    }

}