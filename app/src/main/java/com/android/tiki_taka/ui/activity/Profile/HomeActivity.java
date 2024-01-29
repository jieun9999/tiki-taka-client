package com.android.tiki_taka.ui.activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Sign.SigninActivity1;
import com.android.tiki_taka.ui.fragment.AlarmFragment;
import com.android.tiki_taka.ui.fragment.AlbumFragment;
import com.android.tiki_taka.ui.fragment.ChatFragment;
import com.android.tiki_taka.ui.fragment.HomeFragment;
import com.android.tiki_taka.ui.fragment.VideochatFragment;
import com.android.tiki_taka.utils.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private AlbumFragment albumFragment;
    private ChatFragment chatFragment;
    private AlarmFragment alarmFragment;
    private VideochatFragment videochatFragment;
    ProfileApiService service;
    int userId; // 유저 식별 정보


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        // 서로 연결상태 확인
        checkConnectState();

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        albumFragment = new AlbumFragment();
        chatFragment = new ChatFragment();
        alarmFragment = new AlarmFragment();
        videochatFragment = new VideochatFragment();

        //초기 프래그먼트 설정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, homeFragment).commitAllowingStateLoss();

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener()); // ItemSelectedListener 클래스를 이용해 탭 선택 시의 동작을 정의합니다.
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        //BottomNavigationView.OnNavigationItemSelectedListener 인터페이스를 구현하는 내부 클래스입니다.
        //탭 선택 시 호출되는 onNavigationItemSelected 메서드를 정의합니다.
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            //선택된 탭에 따라 selectedFragment를 해당 프래그먼트(homeFragment, albumFragment, chatFragment)로 설정합니다.
            if (menuItem.getItemId() == R.id.home_menu) {
                selectedFragment = homeFragment;
            } else if (menuItem.getItemId() == R.id.album_menu) {
                selectedFragment = albumFragment;
            } else if (menuItem.getItemId() == R.id.chat_menu) {
                selectedFragment = chatFragment;
            } else if (menuItem.getItemId() == R.id.alarm_menu) {
                selectedFragment = alarmFragment;
            }else if (menuItem.getItemId() == R.id.video_menu){
                selectedFragment = videochatFragment;
            }

            //선택된 프래그먼트(selectedFragment)로 실제 프래그먼트 전환을 수행하는 코드
            if (selectedFragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_layout, selectedFragment)
                        .commitAllowingStateLoss();
            }

            return true;
            //true를 반환하여 이벤트 처리가 완료됨을 알림
        }
    }

    // 모델 클래스를 만들지 않고, ResponseBody를 사용하여 서버의 응답을 처리하려면, 응답을 직접 파싱해야 합니다.
    //  Gson 라이브러리 또는 기본적인 JSONObject를 사용하여 JSON 응답을 분석할 수 있습니다.
    private void checkConnectState(){
        service.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBodyString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBodyString);

                        if (jsonObject.getBoolean("success")) {
                            int userState = jsonObject.getInt("userState");
                            int partnerState = jsonObject.getInt("partnerState");

                            // 여기에 connectStatus를 기반으로 한 로직을 구현합니다.
                            if (userState == 0) {
                                //유저 0

                                // 재연결 액티비티로 이동
                                Intent intent = new Intent(HomeActivity.this, ReconnectActivity.class);
                                startActivity(intent);

                            } else if (userState == 1 && partnerState == 0) {
                                //유저 1, 상대방 0
                                Toast.makeText(getApplicationContext(), "상대방은 재연결을 원하지 않습니다.",Toast.LENGTH_LONG).show();

                                // 로그인_1 화면으로 이동하면서 스택 초기화
                                Intent intent = new Intent(getApplicationContext(), SigninActivity1.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // 현재 액티비티 종료

                            } else {
                                // 유저 1, 상대방 1
                                // 정상 연결
                                Toast.makeText(getApplicationContext(), "연결에 성공하였습니다.",Toast.LENGTH_LONG).show();

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