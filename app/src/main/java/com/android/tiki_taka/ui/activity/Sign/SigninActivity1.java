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
import com.android.tiki_taka.ui.activity.Profile.ReconnectActivity;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
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

public class SigninActivity1 extends AppCompatActivity {
    AuthApiService service;
    ProfileApiService service2;
    TextInputLayout emailInputLayout;
    TextInputEditText emailEditText;
    TextInputLayout passInputLayout;
    TextInputEditText passEditText;
    ImageView signInButton;
    TextView forgotText;
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

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(AuthApiService.class);
        // ProfileApiService 초기화
        service2 = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (ValidationUtils.isValidEmail(s.toString())) {
                    emailInputLayout.setError("이메일 형식이 아닙니다");
                } else {
                    emailInputLayout.setError(null); // 오류 메시지 제거
                }
            }

        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자와 파트너의 연결상태 확인 후, 로그인 허가 여부 결정
                checkConnectState();
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

    }
    @Override
    protected void onResume() {
        super.onResume();
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
                    handleSignInResponse(response);
                }else{
                    // 서버 응답 오류
                    showToast("서버 응답 오류: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSignInResponse(Response<ResponseBody> response) {
        try {
            String responseJson = response.body().string();
            JSONObject jsonObject = new JSONObject(responseJson);
            processLoginResult(jsonObject);
        } catch (JSONException e) {
            showToast("JSON 파싱 오류");
            e.printStackTrace();
        } catch (IOException e) {
            showToast("IO 오류");
            e.printStackTrace();
        }
    }

    private void processLoginResult(JSONObject jsonObject) throws JSONException {
        boolean success = jsonObject.getBoolean("success");
        String message = jsonObject.getString("message");

        if (success) {
            saveAutoLoginPreference();
            showToast(message);
            navigateToHome();
        } else {
            showToast(message);
        }
    }

    private void saveAutoLoginPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAutoLoginEnabled", true);
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(SigninActivity1.this, HomeActivity.class);
        startActivity(intent);
    }



    public void checkConnectState(){
        Log.e("userId", String.valueOf(userId));
        service2.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                processResponse(response); // 여기에서 새로운 함수를 호출합니다.
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processResponse(Response<ResponseBody> response){
        try {
            if (!response.isSuccessful() && response.body() == null) {
                handleServerFailureResponse(response.code());
                return;
            }
                String responseBodyString = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBodyString);

                if (jsonObject.getBoolean("success")) {
                    handleSuccessResponse(jsonObject);
                } else {
                   handleErrorResponse(jsonObject);
                }

        } catch (IOException | JSONException e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();
        }
    }

    private void handleServerFailureResponse(int errorCode) {
        showToast("서버 응답 오류: " + errorCode);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleSuccessResponse(JSONObject jsonObject) throws JSONException {
        int userState = jsonObject.getInt("userState");
        int partnerState = jsonObject.getInt("partnerState");

        if (userState == 0) {
            navigateToReconnectActivity();
        } else if (userState == 1 && partnerState == 0) {
            showPartnerNotInterestedToast();
        } else {
            verifySignIn();
        }
    }

    private void navigateToReconnectActivity() {
        Intent intent = new Intent(SigninActivity1.this, ReconnectActivity.class);
        startActivity(intent);
    }

    private void showPartnerNotInterestedToast() {
        showToast("상대방은 재연결을 원하지 않습니다.");
    }

    private void handleErrorResponse(JSONObject jsonObject) throws JSONException {
        String errorMessage = jsonObject.getString("message");
        showToast(errorMessage);
    }

}