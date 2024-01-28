package com.android.tiki_taka.ui.activity.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.android.tiki_taka.R;
import com.android.tiki_taka.ui.activity.Profile.HomeActivity;

@SuppressLint("CustomSplashScreen")
//개발자가 코드의 특정 부분에서 발생할 수 있는 경고나 오류 메시지를 의도적으로 무시하고자 할 때 사용
//이전 방식의 커스텀 스플래시 스크린을 사용할 경우 경고 메시지가 표시될 수 있기 때문
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // 'Handler'를 이용하여 1초간 대기한 후에 적절한 화면으로 이동시키는 로직
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        },1000); //1초 대기

    }

    private void navigateToNextScreen(){
        SharedPreferences prefs = getSharedPreferences("YourPrefName", MODE_PRIVATE);

        Boolean isAutoLoginEnabled = null;
        // boolean의 래퍼클래스(wrapper class)인 Boolean을 사용하면, null 값을 가질 수 있음
        if(prefs.contains("isAutoLoginEnabled")) {
            isAutoLoginEnabled = prefs.getBoolean("isAutoLoginEnabled", false);
        }

        Intent intent;
        if(isAutoLoginEnabled == null){
            // 회원가입_1 화면으로 이동
            intent = new Intent(SplashScreenActivity.this, SignupActivity1.class);

        } else if (!isAutoLoginEnabled) {
            // 로그인_1 화면으로 이동
            intent = new Intent(SplashScreenActivity.this, SigninActivity1.class);

        }else {
            // 홈 화면으로 이동
            intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        }

        startActivity(intent);
        finish(); // 현재 스플래시 화면 종료
    }
}