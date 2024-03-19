package com.android.tiki_taka.ui.activity.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.MessageAdapter;
import com.android.tiki_taka.models.dto.CommentItem;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.services.ChatApiService;
import com.android.tiki_taka.services.ChatClient;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {
    ChatApiService service;
    private int userId;
    private ChatClient chatClient;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private int chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupNetworkAndRetrieveIds();
        connectToChatServer();

        setupToolBarListeners();
        setupLayoutManager();
        setupAdapter();
        loadMessages();
    }

    private void setupNetworkAndRetrieveIds() {
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ChatApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        chatRoomId = SharedPreferencesHelper.getRoomId(this);
    }

    // new Thread()를 사용하여 connectToChatServer 메서드 내의 작업을 새로운 스레드에서 실행함으로써
    // NetworkOnMainThreadException 오류를 피하기
    private void connectToChatServer(){
        new Thread(()->{
            try {
                chatClient = new ChatClient("52.79.41.79", 1234);
                chatClient.sendUserId(userId);
                // 서버가 쉐어드에 저장되어 있는 userId에 접근하지 못하기 때문에, 서버에게 직접 id를 전송해야 함
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        // 스레드를 실행시키면, 그 스레드를 통해서 지속적으로 클라이언트가 서버로 메세지를 보낼 수 있음

    }

    private void setupToolBarListeners(){
        ImageView cancelBtn = findViewById(R.id.imageView36);
        cancelBtn.setOnClickListener( v -> finish());
    }

    private void setupLayoutManager(){
        recyclerView = findViewById(R.id.chatRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupAdapter(){
        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);
    }

    private void loadMessages(){
        service.getMessages(chatRoomId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {   // 요청 성공 + 응답 존재
                    handleLoadingMessages(response);
                }else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void handleLoadingMessages(Response<List<Message>> response){
        List<Message> messages = response.body();
        if(messages != null){
            messageAdapter.setData(messages, userId);
        }else {
            Log.e("Error", "messages null 입니다");
        }

    }

}