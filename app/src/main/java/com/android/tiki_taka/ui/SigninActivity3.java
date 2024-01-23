package com.android.tiki_taka.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class SigninActivity3 extends AppCompatActivity {
    ApiService service;
    TextInputLayout tempPassInputLayout;
    TextInputEditText tempPassInputText;
    TextInputLayout passInputLayout;
    TextInputEditText passInputText;
    TextInputLayout passConfirmInputLayout;
    TextInputEditText passConfirmInputText;
    ImageView changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin3);

        tempPassInputLayout = findViewById(R.id.textInputLayout);
        passInputLayout = findViewById(R.id.textInputLayout2);
        passConfirmInputLayout = findViewById(R.id.textInputLayout3);
        tempPassInputText = findViewById(R.id.임시비밀번호);
        passInputText = findViewById(R.id.새비밀번호);
        passConfirmInputText = findViewById(R.id.비밀번호확인);
        changeButton = findViewById(R.id.imageView18);
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

        passInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //만약 비밀번호가 조건을 만족하지 않으면 TextInputLayout의 setError 메서드를 사용하여 오류 메시지를 표시
            @Override
            public void afterTextChanged(Editable s) {
                if (ValidatorSingleton.getInstance().isValidPassword(s.toString())) {
                    passInputLayout.setError("비밀번호는 8자 이상 20자 이하, 영문과 숫자를 혼합하여 사용해야 합니다.");
                } else {
                    passInputLayout.setError(null);
                }
            }

        });
        passConfirmInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textPw = passInputText.getText().toString();
                String textPwConfirm = passConfirmInputText.getText().toString();

                if (!textPw.equals(textPwConfirm)) {
                    passConfirmInputLayout.setError("비밀번호가 일치하지 않습니다");
                } else {
                    passConfirmInputLayout.setError(null);
                }
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

    }

    private void changePassword(){
        // SigninActivity2에서 이메일 데이터 받기
        String email = getIntent().getStringExtra("email");
        String tempPassword = tempPassInputText.getText().toString();
        String newPassword = passInputText.getText().toString();

        Call<ResponseBody> call = service.saveNewPassword(email, tempPassword, newPassword);
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
                            // 비번 변경 성공
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            //로그인_1 화면으로 이동
                            Intent intent = new Intent(SigninActivity3.this, SigninActivity1.class);
                            startActivity(intent);

                        } else {
                            // 비번 변경 실패
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