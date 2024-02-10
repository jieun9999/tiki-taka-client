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
import com.android.tiki_taka.ui.activity.Sign.SigninActivity2;
import com.android.tiki_taka.ui.activity.Sign.SignupActivity1;
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

public class ProfileActivity6 extends AppCompatActivity {
    Button button;
    Button button2;
    ProfileApiService service;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile6);

        button = findViewById(R.id.btn_confirm);
        button2 = findViewById(R.id.btn_cancel);

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("회원 탈퇴"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.계정 삭제를 누르면, db의 pk를 제외한 모든 칼럼들이 null로 업데이트 됨.
                dropAccount();

                //2.자동 로그인 쉐어드 모두 초기화
                SharedPreferencesHelper.clearShared(ProfileActivity6.this);

                //3.회원가입_1화면 으로 이동하면서 스택 초기화
                clearStack();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소 선택시, 뒤로가기로 이동
                finish();
            }
        });
    }

    private void dropAccount(){
        service.dropAccount(userId).enqueue(new Callback<ResponseBody>() {
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

    private void responseProcess(Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                String responseJson = response.body().string();
                JSONObject jsonObject = new JSONObject(responseJson);
                String message = jsonObject.getString("message");

                //성공, 실패 관계 없이 모두 메세지를 보여줌
                showToast(message);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
               showToast("오류 생성");
            }

        } else {
            showToast("서버 응답 오류");
        }
    }

    private void clearStack(){
        //회원가입_1화면 으로 이동하면서 스택 초기화
        Intent intent = new Intent(ProfileActivity6.this, SignupActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}