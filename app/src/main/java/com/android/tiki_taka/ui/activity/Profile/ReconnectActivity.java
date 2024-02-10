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
import com.android.tiki_taka.utils.SharedPreferencesHelper;

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("상대방과 연결 끊기");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.reconnectAccount(userId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        reconnectAccountResponseProcess(response);
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
    private void reconnectAccountResponseProcess(Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                String responseJson = response.body().string();
                JSONObject jsonObject = new JSONObject(responseJson);
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");

                if (success) {
                    //db 업데이트 성공 성공
                    showToast(message);
                    // 상대방의 connect 칼럼 확인하기
                    checkConnectState();

                } else {
                    //실패
                    showToast(message);
                }

            } catch (JSONException |IOException e) {
                e.printStackTrace();
                showToast("오류 생성");
            }

        } else {
            showToast("서버 응답 오류");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                checkConnectStateResponseProcess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void checkConnectStateResponseProcess( Response<ResponseBody> response){
        try {
            if (response.isSuccessful() && response.body() != null) {
                String responseBodyString = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBodyString);
                int partnerState = jsonObject.getInt("partnerState");

                if (jsonObject.getBoolean("success")) {
                    handleConnectionStatus(partnerState);

                } else {
                    handleErrorMessage(jsonObject);
                }
            } else {
                showToast("서버 응답 오류");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleConnectionStatus(int partnerState) throws JSONException {
        // 여기에 connectStatus를 기반으로 한 로직을 구현합니다.

        if (partnerState == 1) {
            //파트너 1
            showToast("재연결에 성공하셨습니다!");
            navigateToHomeScreen();

        } else if (partnerState == 0) {
            //파트너 0
            showToast("상대방은 재연결을 원하지 않습니다.");
            navigateToLoginScreen();

        } else {
            // 유저 1, 상대방 1
            showToast("연결 성공!");

        }
    }

    private void handleErrorMessage(JSONObject jsonObject) throws JSONException {
        // 프로필이 존재하지 않는 경우
        String errorMessage = jsonObject.getString("message");
        // 에러 메시지 처리
        showToast(errorMessage);
    }

    private void navigateToLoginScreen() {
        // 스택 초기화 후 로그인_1 화면으로 이동
        Intent intent = new Intent(getApplicationContext(), SigninActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    private void navigateToHomeScreen() {
        // 스택 초기화 후 홈화면으로 이동
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }





}
