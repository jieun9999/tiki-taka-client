package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.app.ActionBar;
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
import com.android.tiki_taka.services.AuthApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.ValidationUtils;
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
    AuthApiService service;
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

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(AuthApiService.class);

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
                if (!ValidationUtils.isValidPassword(s.toString())) {
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
                responseProcess(response);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void responseProcess(Response<ResponseBody> response){
        if (response.isSuccessful()) {
            // 서버에서 응답이 올때

            try {
                String responseJson = response.body().string(); //response.body().string() 메서드를 사용하여 ResponseBody를 문자열로 읽어오는 것
                JSONObject jsonObject = new JSONObject(responseJson);
                boolean success = jsonObject.getBoolean("success");

                if (success) {
                    handleSuccessResponse(jsonObject);

                } else {
                    handleErrorResponse(jsonObject);
                }
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 서버 응답 오류
            handleServerFailureResponse(response.code());
        }
    }

    private void handleSuccessResponse(JSONObject jsonObject) throws JSONException {
        String message = jsonObject.getString("message");
        // 비번 변경 성공
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        //로그인_1 화면으로 이동
        Intent intent = new Intent(SigninActivity3.this, SigninActivity1.class);
        startActivity(intent);
    }

    private void handleErrorResponse(JSONObject jsonObject) throws JSONException {
        // 비번 변경 실패
        String message = jsonObject.getString("message");
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleServerFailureResponse(int errorCode) {
        showToast("서버 응답 오류: " + errorCode);
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