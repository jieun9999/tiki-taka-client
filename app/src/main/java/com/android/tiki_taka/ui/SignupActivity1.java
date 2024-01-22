package com.android.tiki_taka.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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


public class SignupActivity1 extends AppCompatActivity {
    ApiService service;
    TextInputLayout emailTextLayout;
    TextInputEditText emailTextInput; // 클래스 멤버 변수로 선언
    ImageView verifyButton;
    TextInputEditText codeTextInput;
    ImageView confirmButton;
    String email;
    TextInputLayout textInputLayoutPassword;
    TextInputEditText editTextPassword;
    TextInputLayout textInputLayoutPasswordConfirm;
    TextInputEditText editTextPasswordConfirm;
    ImageView signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        emailTextLayout = findViewById(R.id.textInputLayout);
        emailTextInput = findViewById(R.id.이메일); // 여기서 초기화
        verifyButton = findViewById(R.id.imageView6);
        codeTextInput = findViewById(R.id.인증번호);
        confirmButton = findViewById(R.id.imageView9);
        textInputLayoutPassword = findViewById(R.id.textInputLayout3);
        editTextPassword = findViewById(R.id.비밀번호);
        textInputLayoutPasswordConfirm = findViewById(R.id.textInputLayout4);
        editTextPasswordConfirm = findViewById(R.id.비밀번호확인);
        signupButton = findViewById(R.id.imageView8);

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ApiService.class);

        emailTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //1. 이메일 주소가 올바른 형식인지 확인
                // 이메일 형식이 올바르지 않으면 TextInputLayout에 오류 메시지를 표시하고 함수를 종료
                if (ValidatorSingleton.getInstance().isValidEmail(s.toString())) {
                    emailTextLayout.setError("이메일 형식이 아닙니다");
                } else {
                    emailTextLayout.setError(null); // 오류 메시지 제거
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // 인증버튼을 클릭하면 이메일 인증번호를 보낼지 말지 결정하는 함수(onVerifyButtonClicked()) 실행
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVerifyButtonClicked();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmButtonClicked();
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
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
                    textInputLayoutPassword.setError("비밀번호는 8자 이상 20자 이하, 영문과 숫자를 혼합하여 사용해야 합니다.");
                } else {
                    textInputLayoutPassword.setError(null);
                }
            }

        });

        editTextPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String textPw = editTextPassword.getText().toString();
                String textPwConfirm = editTextPasswordConfirm.getText().toString();

                if (!textPw.equals(textPwConfirm)) {
                    textInputLayoutPasswordConfirm.setError("비밀번호가 일치하지 않습니다");
                } else {
                    textInputLayoutPasswordConfirm.setError(null);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호를 가져옵니다.
                String password = editTextPassword.getText().toString();
                // 비밀번호 해시 처리는 서버 측에서 수행하는 것이 권장됨

                // 데이터베이스에 저장
                sendPw(password);
            }
        });
    }

    public void onVerifyButtonClicked() {
        email = emailTextInput.getText().toString();


        // 이메일 형식이 올바른 경우, 가입된 이메일 확인 요청을 보냄
        Call<Boolean> call = service.checkUserEmail(email);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    // 서버에서 응답을 받았을 때
                    boolean isRegistered = response.body();
                    if (isRegistered) {
                        // 이미 가입된 이메일인 경우
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity1.this);
                        builder.setMessage("이미 가입된 이메일입니다.")
                                .setPositiveButton("확인", null)
                                .show();
                    } else {
                        // 가입되지 않은 이메일인 경우
                        // 이메일 인증 요청 프로세스를 진행합니다.
                        Log.e("사용가능한 이메일 입니다", "HTTP 코드: " + response.code());

                        // 서버측에서 인증번호 이메일을 보내줌
                        sendEmail(email);
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


    public void sendEmail(String email) {
        // 1. 서버에 사용자가 입력한 이메일 주소 전송
        Call<Boolean> call = service.sendEmail(email);

        //2. 서버의 인증번호전송 성공여부에 따라 토스트 출력
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 서버에서 응답도 성공하고, 응답이 빈 값이 아닐때
                    Boolean result = response.body();
                    // 서버에서 인증번호 전송에 성공했을 때의 처리
                    if (result) {
                        // 인증번호 전송 성공
                        Toast.makeText(getApplicationContext(), "인증 메일이 성공적으로 발송되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        // 인증번호 전송 실패
                        Toast.makeText(getApplicationContext(), "인증 메일 발송에 실패하였습니다.'", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 서버에서 인증번호 전송 실패 처리
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    public void onConfirmButtonClicked() {
        String code = codeTextInput.getText().toString();

        //1. 서버에 사용자가 입력한 인증번호 전송
        Call<ResponseBody> call = service.sendAuthCode(email, code);

        //2. 서버 응답을 Boolean 대신 JSON 객체로 받아서 처리
        //버에서는 json 형식으로 받고 이것을 클라이언트가 처리할 수 있는 jsonObject로 파싱
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
                        int userId = jsonObject.getInt("userId");

                        //사용자아이디(user_id)를 db에서 가져와서 기기 내에 저장함
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("userId", userId);
                        editor.apply();

                        if (success) {
                            // 인증번호 일치
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else {
                            // 인증번호 불일치 or 만료됨
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // 서버에서 인증번호 전송 실패 처리
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


    public void sendPw(String pw){
        // 1. 서버에 사용자가 입력한 비번 전송
        Call<Boolean> call = service.sendPass(email, pw);

        //2. 서버의 성공여부에 따라 다른 처리
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 서버에서 응답도 성공하고, 응답이 빈 값이 아닐때
                    Boolean result = response.body();
                    // 서버에서 인증번호 전송에 성공했을 때의 처리
                    if (result) {
                        // 비밀번호 db에 저장 성공

                        // 회원가입_2로 이동
                        Intent intent = new Intent(SignupActivity1.this, SignupActivity2.class);
                        startActivity(intent);

                    } else {
                        // 비밀번호 저장 실패
                        Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.'", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 서버에서 인증번호 전송 실패 처리
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

}