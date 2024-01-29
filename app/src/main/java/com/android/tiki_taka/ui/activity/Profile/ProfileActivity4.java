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

public class ProfileActivity4 extends AppCompatActivity {
    Button saveBtn;
    TextInputEditText nameView;
    ProfileApiService service;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile4);

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("이름 변경"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        saveBtn = findViewById(R.id.button);
        nameView = findViewById(R.id.이름);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nameText = nameView.getText().toString();
                Call<ResponseBody> call = service.updateProfileName(userId, nameText);
                call.enqueue(new Callback<ResponseBody>() {
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
                                    // 저장 성공
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
                                    finish();
                                    //finish() 사용 후 이전 액티비티에서 데이터 갱신

                                } else {
                                    // 저장 실패
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