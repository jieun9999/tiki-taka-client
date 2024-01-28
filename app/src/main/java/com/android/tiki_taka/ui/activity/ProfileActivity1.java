package com.android.tiki_taka.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.tiki_taka.R;

import java.util.Objects;

public class ProfileActivity1 extends AppCompatActivity {
    private ListView listView;
    private String[] options = {"로그아웃", "비밀번호 변경하기", "알림 동의 설정", "상대방과 연결끊기", "회원 탈퇴"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("내 정보 수정");
        // 뒤로 가기 버튼 활성화
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        listView = (ListView) findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.profile_list_item, R.id.text_view_item, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // "로그아웃" 옵션을 클릭했을 때

                if (options[position].equals("로그아웃")) {



                } else if (options[position].equals("비밀번호 변경하기")) {

                    Intent intent = new Intent(ProfileActivity1.this, SigninActivity2.class);
                    startActivity(intent);

                } else if (options[position].equals("알림 동의 설정")) {


                } else if (options[position].equals("상대방과 연결끊기")) {
                    Intent intent = new Intent(ProfileActivity1.this, ProfileActivity3.class);
                    startActivity(intent);

                } else if (options[position].equals("회원 탈퇴")){

                    Intent intent = new Intent(ProfileActivity1.this, ProfileActivity2.class);
                    startActivity(intent);
                }
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