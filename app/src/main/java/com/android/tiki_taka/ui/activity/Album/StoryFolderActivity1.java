package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryCardAdapter;
import com.android.tiki_taka.models.dtos.StoryCardDto;
import com.android.tiki_taka.models.dtos.StoryFolderDto;
import com.android.tiki_taka.models.responses.StoryCardsResponse;
import com.android.tiki_taka.models.responses.StoryFolderResponse;
import com.android.tiki_taka.models.responses.StoryFoldersResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.DateUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoryFolderActivity1 extends AppCompatActivity {
    int folderId; //폴더 식별 정보
    StoryApiService service;
    int userId; // 유저 식별 정보
    StoryCardAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_folder1);

        // 커스텀 툴바 설정
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        ImageView backButton = findViewById(R.id.imageView36);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(StoryApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        // 리사이클러뷰 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //빈 어댑터 사용
        adapter = new StoryCardAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Intent에서 아이템 ID 가져오기
        Intent intent = getIntent(); // 현재 액티비티로 전달된 Intent를 가져옴
        folderId = intent.getIntExtra("CLICKED_ITEM_ID", -1); // "CLICKED_ITEM_ID" 키로 저장된 int 값을 가져옴

        // 가져온 아이템 ID를 사용하여 세부 정보를 표시하거나 데이터를 로드
        if (folderId != -1) {
            // 유효한 아이템 ID인 경우, 해당 ID를 사용하여 데이터를 로드하거나 처리
            // 예: 데이터베이스 조회, API 요청 등

            //1. 대표사진 가져오기 (storyFolder 테이블에서)
            loadThumbNail();

            //2. 리사이클러뷰에 들어가는 데이터 가져오기 (storyCard 테이블에서)
            loadStoryCards();

        } else {
            // ID가 유효하지 않은 경우의 처리
            Log.e("Error", "서버에서 불러오기에 실패ID가 유효하지 않습니다.");
        }

    }

    private void loadThumbNail(){
        service.getThumbNail(folderId).enqueue(new Callback<StoryFolderResponse>() {
            @Override
            public void onResponse(Call<StoryFolderResponse> call, Response<StoryFolderResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    // 요청 성공 + 응답 존재

                    StoryFolderResponse storyFolderResponse = response.body();
                    if(storyFolderResponse.isSuccess()){
                        //success가 true인 경우,
                        StoryFolderDto storyFolderDto = storyFolderResponse.getStoryFolder();

                        //썸네일 뷰 할당하기
                        TextView thumbDateView = findViewById(R.id.textView26);
                        TextView thumbTitleView = findViewById(R.id.textView27);
                        TextView thumbLocView = findViewById(R.id.textView28);
                        ImageView thumbBackImgView = findViewById(R.id.imageView26);

                        // 서버 날짜 문자열(2024-01-31 12:24:40) => 2023년 12월 25일 (월) 변환
                        String inputDateString = storyFolderDto.getUpdatedAt();
                        try {
                            String outputDateString = DateUtils.convertDateString(inputDateString);
                            thumbDateView.setText(outputDateString);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        thumbTitleView.setText(storyFolderDto.getTitle());
                        thumbLocView.setText(storyFolderDto.getLocation());
                        // 이미지는 글라이드로 할당
                        Glide.with(getApplicationContext())
                                .load(storyFolderDto.getDisplayImageUrl())
                                .into(thumbBackImgView);

                        String message = storyFolderResponse.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    }else {
                        //success가 false인 경우,
                        String message = storyFolderResponse.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                }else {
                    // 응답 실패
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StoryFolderResponse> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void loadStoryCards(){
        service.getStoryCards(folderId).enqueue(new Callback<StoryCardsResponse>() {
            @Override
            public void onResponse(Call<StoryCardsResponse> call, Response<StoryCardsResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    // 요청 성공 + 응답 존재

                    StoryCardsResponse storyCardsResponse = response.body();
                    if(storyCardsResponse.isSuccess()){
                        //success가 true인 경우,
                        List<StoryCardDto> storyCardDtos = storyCardsResponse.getStoryCards();

                        // 서버에서 가져온 리스트를 어댑터에 추가함
                        adapter.setData(storyCardDtos);

                        String message = storyCardsResponse.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }else {
                        //success가 false인 경우,
                        String message = storyCardsResponse.getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }

                }else {
                    // 응답 실패
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StoryCardsResponse> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }
}