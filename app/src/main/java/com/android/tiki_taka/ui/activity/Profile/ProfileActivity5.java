package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity5 extends AppCompatActivity {
    Button saveBtn;
    TextInputEditText messageView;
    ProfileApiService service;
    int userId; // 유저 식별 정보


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile5);
        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("상태 메세지 변경"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        saveBtn = findViewById(R.id.button);
        messageView = findViewById(R.id.상태메세지);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String messageText = messageView.getText().toString();
                Call<ResponseBody> call = service.updateProfileMessage(userId, messageText);
                call.enqueue(new Callback<ResponseBody>() {
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
    }

    private void responseProcess(Response<ResponseBody> response) {
        // 요청 성공 처리
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                String responseJson = response.body().string();
                JSONObject jsonObject = new JSONObject(responseJson);
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");

                    if (success) {
                        // 저장 성공
                        showToast(message);
                        finish();

                    } else {
                        // 저장 실패
                    showToast(message);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                showToast("오류 발생");
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

}