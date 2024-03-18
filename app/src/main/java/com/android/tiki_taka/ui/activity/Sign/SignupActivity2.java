package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.ChatRoom;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.models.response.CodeResponse;
import com.android.tiki_taka.services.AuthApiService;
import com.android.tiki_taka.services.ChatApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity2 extends AppCompatActivity {
    AuthApiService service;
    ChatApiService chatService;
    int userId; // 유저 식별 정보
    TextView codeEffectiveDate;
    TextView code;
    TextInputEditText inputCode;
    ImageView connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        codeEffectiveDate = findViewById(R.id.textView5);
        code = findViewById(R.id.tvValidUntil);
        inputCode = findViewById(R.id.초대코드입력);
        connectButton = findViewById(R.id.imageView3);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(AuthApiService.class);
        chatService = retrofit.create(ChatApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        //1. 서버에 getInvitationCode 요청을 보냄
        Call<CodeResponse> call = service.getInvitationCode(userId);
        //2. 서버 응답에 대한 처리
        call.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if(response.isSuccessful()){
                    // HTTP 요청의 응답이 성공적이었는지 여부를 확인
                    // HTTP 응답 코드가 200-299 범위 내에 있을 때 true를 반환
                    CodeResponse codeResponse = response.body();

                    // 텍스트 교체 (유효시간, 초대코드)
                    // 1. 유효시간
                    startCount(codeEffectiveDate);

                    //2. 초대코드
                    code.setText(codeResponse.getInvitationCode());

                }

            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // 서버로 입력한 코드 전송
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = String.valueOf(inputCode.getText());
                sendCode(code);
            }
        });
    }

    // private : 같은 클래스안에 있는 멤버들만 접근
    private void startCount(TextView textView){

        // CountDownTimer는 화면을 나갔다가 다시 들어와도 계속해서 시간을 카운트 다운하게 됩니다.
        // 이것은 화면을 벗어나도 백그라운드에서 작동하며, 화면 상태에 영향을 받지 않습니다
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date currentDate = new Date(); // 현재 날짜 및 시간 가져오기
            long endTime = currentDate.getTime() + TimeUnit.HOURS.toMillis(24); // 현재 시간에 24시간을 더함

            // 카운트다운 타이머 설정
            // (만료시간 - 현재시간)이 얼마나 남았는지 보여줌
            long millisInFuture = endTime - System.currentTimeMillis();
            new CountDownTimer(millisInFuture, 1000) {
                public void onTick(long millisUntilFinished) {
                    // 남은 시간을 시:분:초 형태로 변환
                    String timeFormatted = String.format(Locale.getDefault(),
                            "%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    textView.setText(timeFormatted);
                }
                public void onFinish() {
                    textView.setText("시간 만료");
                }
            }.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void sendCode(String code){
        Call<ResponseBody> call = service.connect(userId,code);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                responseProcess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("\"네트워크 오류: \" + t.getMessage()");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void responseProcess(Response<ResponseBody> response){
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                handleSuccessfulResponse(response);

            } catch (JSONException | IOException e) {
                handleResponseParsingError(e);
            }

        } else {
            // 실패한 HTTP 요청 처리
            handleServerError(response.code());
        }
    }

    private void handleSuccessfulResponse(Response<ResponseBody> response) throws JSONException, IOException {
        String responseJson = response.body().string();
        JSONObject jsonObject = new JSONObject(responseJson);
        boolean success = jsonObject.getBoolean("success");
        String message = jsonObject.getString("message");
        int partnerId = jsonObject.getInt("partnerId");

        if (success) {
            // 초대번호 일치
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            // db에 채팅방 생성
            makeChatRoomInDB(partnerId);

            //회원가입_3으로 이동
            Intent intent = new Intent(SignupActivity2.this, SignupActivity3.class);
            startActivity(intent);

        } else {
            // 초대번호 불일치 or 만료됨
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void makeChatRoomInDB( int partnerId){
        ChatRoom chatRoom = new ChatRoom(userId, partnerId);
        chatService.makeChatRoom(chatRoom).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().isSuccess()){
                        // success가 true일 때의 처리

                    }
                }else {
                    // success가 false일 때의 처리
                    Log.e("ERROR", "댓글 업로드 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }

    private void handleResponseParsingError(Exception e){
        // JSON 파싱 오류 및 IOException 처리
        e.printStackTrace();
        Toast.makeText(getApplicationContext(), "오류 발생", Toast.LENGTH_LONG).show();
    }

    private void handleServerError(int errorCode) {
        // 서버에서 인증번호 전송 실패 처리
        Toast.makeText(getApplicationContext(), "서버 응답 오류: " + errorCode, Toast.LENGTH_LONG).show();
    }
}