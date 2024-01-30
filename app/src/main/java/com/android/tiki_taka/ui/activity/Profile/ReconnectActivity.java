package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Sign.SigninActivity1;
import com.android.tiki_taka.utils.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReconnectActivity extends AppCompatActivity {

    Button button;
    Button button2;
    ProfileApiService service;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);

        button = findViewById(R.id.btn_confirm);
        button2 = findViewById(R.id.btn_cancel);

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("상대방과 연결 끊기"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1. db의 connect 칼럼 업데이트
                service.reconnectAccount(userId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        // 요청 성공 처리
                        if (response.isSuccessful()) {
                            // http 요청 성공시
                            try {
                                String responseJson = response.body().string();
                                //response.body().string() 메서드를 사용하여 ResponseBody를 문자열로 읽어오는 것
                                //.toString() 과 다름
                                JSONObject jsonObject = new JSONObject(responseJson);
                                boolean success = jsonObject.getBoolean("success");
                                String message = jsonObject.getString("message");

                                if (success) {
                                    //db 업데이트 성공 성공
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                    // 상대방의 connect 칼럼 확인하기
                                    checkConnectState();

                                } else {
                                    // 실패
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                // JSON 파싱 오류 처리
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "JSON 파싱 오류", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                // IOException 처리
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "IO 오류", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            //서버 응답 오류
                            Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 요청 실패 처리
                        Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                    }
                });


            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 뒤로가기 버튼 클릭 시의 동작
                onBackPressed(); // 이전 액티비티로 돌아가기
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkConnectState(){
        service.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBodyString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBodyString);

                        if (jsonObject.getBoolean("success")) {
                            int partnerState = jsonObject.getInt("partnerState");

                            // 여기에 connectStatus를 기반으로 한 로직을 구현합니다.
                            if (partnerState == 1) {
                                //파트너 1
                                Toast.makeText(getApplicationContext(), "재연결에 성공하셨습니다!",Toast.LENGTH_LONG).show();

                                // 스택 초기화 후 홈화면 으로 이동
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // 현재 액티비티 종료

                            } else if (partnerState == 0) {
                                //파트너 0
                                Toast.makeText(getApplicationContext(), "상대방은 재연결을 원하지 않습니다.",Toast.LENGTH_LONG).show();

                                // 스택 초기화 후 로그인_1 화면으로 이동
                                Intent intent = new Intent(getApplicationContext(), SigninActivity1.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // 현재 액티비티 종료

                            } else {
                                // 유저 1, 상대방 1
                                // 정상 연결
                                Toast.makeText(getApplicationContext(), "연결에 성공하였습니다.",Toast.LENGTH_LONG).show();

                            }
                        } else {
                            // 프로필이 존재하지 않는 경우
                            String errorMessage = jsonObject.getString("message");
                            // 에러 메시지 처리
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // 서버 응답 실패 처리
                        Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException | JSONException e) {
                    // JSON 파싱 오류 처리
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }
}
