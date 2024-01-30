package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.AuthApiService;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Profile.HomeActivity;
import com.android.tiki_taka.ui.activity.Profile.ProfileActivity6;
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

public class SigninActivity1 extends AppCompatActivity {
    AuthApiService service;
    ProfileApiService service2;
    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    TextInputLayout passInputLayout;
    TextInputEditText passEditText;
    ImageView signInButton;
    TextView forgotText;
    TextView deleteAccountText;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin1);

        emailInputLayout = findViewById(R.id.textInputLayout);
        emailEditText = findViewById(R.id.이메일);
        passInputLayout = findViewById(R.id.textInputLayout2);
        passEditText = findViewById(R.id.새비밀번호);
        signInButton = findViewById(R.id.imageView5);
        forgotText = findViewById(R.id.textView8);
        deleteAccountText = findViewById(R.id.textView9);

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(AuthApiService.class);
        service2 = retrofit.create(ProfileApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        //정상 연결 계정인 경우, '회원탈퇴' 뷰를 숨김
        checkConnectState();

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
        passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ValidatorSingleton.getInstance().isValidPassword(s.toString())) {
                    passInputLayout.setError("비밀번호는 8자 이상 20자 이하, 영문과 숫자를 혼합하여 사용해야 합니다.");
                } else {
                    passInputLayout.setError(null);
                }
            }


        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifySignIn();
            }
        });
        forgotText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인_2 화면으로 이동
                Intent intent = new Intent(SigninActivity1.this, SigninActivity2.class);
                startActivity(intent);

            }
        });
        deleteAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity1.this, ProfileActivity6.class);
                startActivity(intent);
            }
        });

    }

    public void verifySignIn(){
        String email = emailEditText.getText().toString();
        String password = passEditText.getText().toString();

        Call<ResponseBody> call = service.signIn(email, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                            // 쉐어드에 자동로그인 정보 저장
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isAutoLoginEnabled", true);
                            editor.apply();

                            // 로그인 성공
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            //홈화면으로 이동
                            Intent intent = new Intent(SigninActivity1.this, HomeActivity.class);
                            startActivity(intent);

                        } else {
                            // 로그인 실록
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
                }else{
                    // 서버 응답 오류
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkConnectState(){
        service2.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBodyString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBodyString);

                        if (jsonObject.getBoolean("success")) {
                            int userState = jsonObject.getInt("userState");

                            if (userState == 1) {
                                // connect가 1이면, '회원탈퇴' 버튼을 숨김
                                deleteAccountText.setVisibility(View.GONE);
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