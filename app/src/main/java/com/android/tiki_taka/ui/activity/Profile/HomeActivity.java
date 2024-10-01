package com.android.tiki_taka.ui.activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Chat.ChatActivity;
import com.android.tiki_taka.ui.fragment.AlbumFragment;
import com.android.tiki_taka.ui.fragment.HomeFragment;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

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
    ProfileApiService service;
    int userId; // 유저 식별 정보


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        // 연결끊기가 된 상태이면 바로 프로필_1 화면으로 이동(홈화면 진입 불가)
        checkConnectStateAndNavigateToProfileActivity1();

        ImageView chatIcon =findViewById(R.id.chat_icon);
        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        albumFragment = new AlbumFragment();

        // 인텐트에서 추가 정보 확인
        String openFragment = getIntent().getStringExtra("OPEN_FRAGMENT");
        if ("ALBUM_FRAGMENT".equals(openFragment)) {
            // AlbumFragment 열기
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, albumFragment).commitAllowingStateLoss();
        } else {
            // 초기 프래그먼트 설정
            // 기본 homeFragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, homeFragment).commitAllowingStateLoss();
        }

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener()); // ItemSelectedListener 클래스를 이용해 탭 선택 시의 동작을 정의합니다.


        // //네트워크 작업(채팅)을 수행할 때 주의해야 할 중요한 점 중 하나는 네트워크 작업을 메인 스레드에서 실행하지 않아야 한다는 것!!!
        String TAG = "FCM Service";
        // 현재 토큰을 가져오기
        FirebaseMessaging.getInstance().getToken().
                addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "토큰 생성 실패", task.getException());
                            return;
                        }
                        // 새로운 토큰 생성 성공 시
                        String token = task.getResult();
                        Log.d(TAG,token);
                    }
                });
    }


    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        //BottomNavigationView.OnNavigationItemSelectedListener 인터페이스를 구현하는 내부 클래스입니다.
        //탭 선택 시 호출되는 onNavigationItemSelected 메서드를 정의합니다.
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            //선택된 탭에 따라 selectedFragment를 해당 프래그먼트(homeFragment, albumFragment, chatFragment)로 설정합니다.
            if (menuItem.getItemId() == R.id.home_menu) {
                selectedFragment = homeFragment;
            } else if (menuItem.getItemId() == R.id.album_menu) {
                selectedFragment = albumFragment;
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

    public void checkConnectStateAndNavigateToProfileActivity1(){
        service.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleConnectStateResponse(response);
                } else {
                    // 서버 응답 실패 처리
                    showToast("서버 응답 오류: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void handleConnectStateResponse(Response<ResponseBody> response) {
        try {
            String responseBodyString = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBodyString);
            getUserState(jsonObject);

        } catch (IOException | JSONException e) {
            handleResponseError(e);
        }
    }

    private void getUserState(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getBoolean("success")) {
            int userState = jsonObject.getInt("userState");
            if (userState == 0) {
                navigateToProfileActivity1();
                return;
            }

        } else {
            // 프로필이 존재하지 않는 경우의 처리 로직
            String errorMessage = jsonObject.getString("message");
            showToast(errorMessage);
        }
    }

    private void navigateToProfileActivity1() {
        // 프로필_1 화면으로 이동하면서 스택 초기화
        Intent intent = new Intent(getApplicationContext(), ProfileActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void handleResponseError(Exception e) {
        e.printStackTrace();
        showToast("데이터 처리 오류");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}