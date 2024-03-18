package com.android.tiki_taka.ui.activity.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.ChatUser;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.models.request.StoryCardRequest;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private int userId;
    private int chatRoomId;
    private List<Message> messages = new ArrayList<>(); //메세지 목록을 저장하는 필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeUserAndRoomIds();
        setupUIListeners();

    }

    private void initializeUserAndRoomIds() {
        userId = SharedPreferencesHelper.getUserId(this);
        chatRoomId = SharedPreferencesHelper.getRoomId(this);
    }

    private void setupUIListeners(){
        ImageView cancelBtn = findViewById(R.id.imageView36);
        cancelBtn.setOnClickListener( v -> finish());
    }

    public void addMessage(Message message){
        this.messages.add(message);
    }
}