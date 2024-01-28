package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.ValidatorSingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SigninActivity2 extends AppCompatActivity {
    ApiService service;
    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    ImageView confirmButton;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin2);

        emailInputLayout = findViewById(R.id.textInputLayout);
        emailEditText = findViewById(R.id.이메일);
        confirmButton = findViewById(R.id.imageView16);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 뒤로 가기 버튼 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // 타이틀 표시하지 않음
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ApiService.class);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValidatorSingleton.getInstance().isValidEmail(s.toString())) {
                    emailInputLayout.setError("이메일 형식이 아닙니다");
                } else {
                    emailInputLayout.setError(null); // 오류 메시지 제거
                }

            }

        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();

                // 이메일 형식이 올바른 경우, 가입된 이메일 확인 요청을 보냄
                Call<Boolean> call = service.checkUserEmail(email);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            // 서버에서 응답을 받았을 때
                            boolean isRegistered = response.body();
                            if (!isRegistered) {
                                // 미가입인 경우
                                Toast.makeText(getApplicationContext(), "가입된 이메일이 아닙니다.", Toast.LENGTH_LONG).show();
                            } else {
                                // 가입된 경우, 서버에서 임시 비번과 날짜 생성후 이메일 전송
                                sendTemporaryPassword();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        //네트워크 오류 처리
                        Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void sendTemporaryPassword(){

        Call<ResponseBody> call = service.sendTemporaryPassword(email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 서버에서 응답이 올때

                    try {
                        String responseJson = response.body().string();
                        //response.body().string() 메서드를 사용하여 ResponseBody를 문자열로 읽어오는 것
                        //.toString() 과 다름
                        JSONObject jsonObject = new JSONObject(responseJson);
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            // 임시 비밀번호 전송 성공
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            //로그인_3 화면으로 이동
                            Intent intent = new Intent(SigninActivity2.this, SigninActivity3.class);
                            intent.putExtra("email", email); // 인텐트에 이메일 추가
                            startActivity(intent);
                        } else {
                            // 임시 비밀번호 전송 실패
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // 서버 응답 오류
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    // 뒤로 가기 버튼 클릭 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}