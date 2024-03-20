package com.android.tiki_taka.ui.activity.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.MessageAdapter;
import com.android.tiki_taka.listeners.MessageListener;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.services.ChatApiService;
import com.android.tiki_taka.services.ChatClient;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity implements MessageListener {
    ChatApiService service;
    ProfileApiService profileService;
    private int userId;
    private ChatClient chatClient;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private int chatRoomId;
    private String partnerImg;

    //네트워크 작업(채팅)을 수행할 때 주의해야 할 중요한 점 중 하나는 네트워크 작업을 메인 스레드에서 실행하지 않아야 한다는 것!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupNetworkAndRetrieveIds();
        setupToolBarListeners();

        // 1. db에서 먼저 과거 메세지 이력을 가져옴
        setupLayoutManager();
        setupAdapter();
        loadMessages();
        getPartnerProfile();

        //2. 서버 연결 및 수신 & 전송 준비
        connectToChatServer();
        setupSendButtonClickListener();
    }

    private void setupNetworkAndRetrieveIds() {
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ChatApiService.class);
        profileService =retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        chatRoomId = SharedPreferencesHelper.getRoomId(this);
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

    private void getPartnerProfile(){
        profileService.getPtnrModalData(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                processPtnModalDataResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processPtnModalDataResponse(Response<ResponseBody> response) {
        if (response.isSuccessful() && response.body() != null) {
            try {
                // 서버로부터 응답 본문을 문자열로 변환
                String responseString = response.body().string();
                //문자열을 JSON객체로 변환
                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getBoolean("success")) {
                    handleUserData(jsonObject);
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }

    }

    private void handleUserData(JSONObject jsonObject) throws JSONException {
        // 성공적으로 데이터를 가져온 경우
        JSONObject userData = jsonObject.getJSONObject("data");
        partnerImg = userData.optString("profile_image", "");
    }


    private void handleLoadingMessages(Response<List<Message>> response){
        List<Message> messages = response.body();
        if(messages != null){
            messageAdapter.setData(messages, userId);
        }else {
            Log.e("Error", "messages null 입니다");
        }

    }

    // new Thread()를 사용하여 connectToChatServer 메서드 내의 작업을 새로운 스레드에서 실행함으로써
    // NetworkOnMainThreadException 오류를 피하기
    private void connectToChatServer(){
        new Thread(()->{
            try {
                // 서버의 로컬 ip 주소로 접속함
                chatClient = new ChatClient("192.168.0.193", 1234, this);

                chatClient.sendUserId(userId);
                // 서버가 쉐어드에 저장되어 있는 userId에 접근하지 못하기 때문에, 서버에게 직접 id를 전송해야 함

                chatClient.listenMessage();
                // 서버로부터 오는 메시지를 지속적으로 수신
                // 수신받은 메세지를 'updateUI' 메세드를 통해 실시간으로 RecyclerView에 메세지를 추가함

            } catch (IOException e) {
                Log.e("ChatClient", "Initialization failed.", e);
                e.printStackTrace();
            }
        }).start();
        // 스레드를 실행시키면, 그 스레드를 통해서 단 한번 서버 연결을 시도하고, 서버에게 id를 보냄

    }

    private void setupSendButtonClickListener(){
        TextView sendButton = findViewById(R.id.send_view);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessage() throws JSONException {
        EditText inputChatView = findViewById(R.id.inputbox_chat);
        String inputText = inputChatView.getText().toString();

        JSONObject messageObject = new JSONObject();
        messageObject.put("senderId", userId);
        messageObject.put("message", inputText);
        messageObject.put("chatRoomId", chatRoomId);

        // JSON 객체를 문자열로 변환
        String message = messageObject.toString();

        // 안드로이드에서 네트워크 연결과 같은 입출력(I/O) 작업은 메인 스레드(또는 UI 스레드)에서 실행해서는 안 됩니다
        // 서버 소켓을 통해 메세지를 보낼 때, 메세지 객체가 아니라 문자열로 전송하는 것이 일반적
        // 백그라운드 스레드에서 소켓을 통해 메세지 전송
        new Thread(() -> chatClient.sendMessage(message)).start();

    }

    // 채팅 액티비티에서 나가게 되면 리소스를 정리
    // 백그라운드 스레드를 사용할 때는 해당 스레드에서 열린 네트워크 연결, 파일 핸들, 스트림 등을 적절하게 닫아주는 것이 중요
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(chatClient != null){
            try {
                chatClient.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(() -> updateUI(message));
    }

    private void updateUI(String message) {
        // message json 문자열 파싱

//        // 리사이클러뷰에 메세지 추가
//        Message newMessage = new Message(partnerImg, message, createdAt);
//        messageAdapter.addMessage(newMessage);
//        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
    }
}