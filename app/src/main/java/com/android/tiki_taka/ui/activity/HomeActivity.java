package com.android.tiki_taka.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.PartnerProfile;
import com.android.tiki_taka.models.UserProfile;
import com.android.tiki_taka.services.ApiService;
import com.android.tiki_taka.ui.fragment.AlarmFragment;
import com.android.tiki_taka.ui.fragment.AlbumFragment;
import com.android.tiki_taka.ui.fragment.ChatFragment;
import com.android.tiki_taka.ui.fragment.HomeFragment;
import com.android.tiki_taka.ui.fragment.VideochatFragment;
import com.android.tiki_taka.utils.DateUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.ValidatorSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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



}