package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity3 extends AppCompatActivity {
    Button button;
    Button button2;
    ProfileApiService service;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile3);

        button = findViewById(R.id.btn_confirm);
        button2 = findViewById(R.id.btn_cancel);

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("상대방과 연결 끊기"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.disconnectAccount(userId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        responseProcess(response);

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
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

    private void responseProcess(Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful()) {
            handleSuccessfulResponse(response);

        } else {
            showToast("서버 응답 오류");
        }
    }

    private void handleSuccessfulResponse(Response<ResponseBody> response){
        // http 요청 성공시
        try {
            String responseJson = response.body().string();
            JSONObject jsonObject = new JSONObject(responseJson);
            boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");

            if (success) {
                handleSuccess(message);

            } else {
                showToast(message);
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            showToast("오류 발생");
        }

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleSuccess( String message){
        showToast(message);
        navigateToProfileActivity1();
    }

    private void navigateToProfileActivity1() {
        // 프로필_1 화면으로 이동하면서 스택 초기화
        Intent intent = new Intent(getApplicationContext(), ProfileActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //액티비티 또는 프래그먼트의 생명주기와 밀접하게 연결됨
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
}