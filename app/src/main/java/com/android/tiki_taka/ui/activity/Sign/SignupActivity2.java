package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.responses.CodeResponse;
import com.android.tiki_taka.services.AuthApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity2 extends AppCompatActivity {
    AuthApiService service;
    int userId; // 유저 식별 정보
    TextView codeEffectiveDate;
    TextView code;
    TextInputEditText inputCode;
    ImageView connectButton;



    // protected : 같은 패키지안의 모든 클래스와, 다른 패키지의 자식 클래스에서 접근
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        codeEffectiveDate = findViewById(R.id.textView5);
        code = findViewById(R.id.tvValidUntil);
        inputCode = findViewById(R.id.초대코드입력);
        connectButton = findViewById(R.id.imageView3);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(AuthApiService.class);

        //1. 서버에 getInvitationCode 요청을 보냄
        Call<CodeResponse> call = service.getInvitationCode(userId);
        //2. 서버 응답에 대한 처리
        call.enqueue(new Callback<CodeResponse>() {
            @Override
            public void onResponse(Call<CodeResponse> call, Response<CodeResponse> response) {
                if(response.isSuccessful()){
                    // HTTP 요청의 응답이 성공적이었는지 여부를 확인
                    // HTTP 응답 코드가 200-299 범위 내에 있을 때 true를 반환

                    CodeResponse codeResponse = response.body();

                    // 텍스트 교체 (유효시간, 초대코드)
                    // 1. 유효시간
                    startCount(codeEffectiveDate);

                    //2. 초대코드
                    code.setText(codeResponse.getInvitationCode());

                }

            }

            @Override
            public void onFailure(Call<CodeResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // 서버로 입력한 코드 전송
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = inputCode.getText().toString();
                sendCode(code);
            }
        });
    }

    // private : 같은 클래스안에 있는 멤버들만 접근
    private void startCount(TextView textView){

        // CountDownTimer는 화면을 나갔다가 다시 들어와도 계속해서 시간을 카운트 다운하게 됩니다.
        // 이것은 화면을 벗어나도 백그라운드에서 작동하며, 화면 상태에 영향을 받지 않습니다
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date currentDate = new Date(); // 현재 날짜 및 시간 가져오기
            long endTime = currentDate.getTime() + TimeUnit.HOURS.toMillis(24); // 현재 시간에 24시간을 더함

            // 카운트다운 타이머 설정
            // (만료시간 - 현재시간)이 얼마나 남았는지 보여줌
            long millisInFuture = endTime - System.currentTimeMillis();
            new CountDownTimer(millisInFuture, 1000) {
                public void onTick(long millisUntilFinished) {
                    // 남은 시간을 시:분:초 형태로 변환
                    String timeFormatted = String.format(Locale.getDefault(),
                            "%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    textView.setText(timeFormatted);
                }
                public void onFinish() {
                    textView.setText("시간 만료");
                }
            }.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void sendCode(String code){
        Call<ResponseBody> call = service.sendInviteCode(userId,code);

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
                            // 초대번호 일치
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            //회원가입_3으로 이동
                            Intent intent = new Intent(SignupActivity2.this, SignupActivity3.class);
                            startActivity(intent);

                        } else {
                            // 초대번호 불일치 or 만료됨
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
}