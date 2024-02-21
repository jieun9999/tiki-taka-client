package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.CommentAdapter;
import com.android.tiki_taka.models.dtos.CommentItem;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WithCommentStoryCard1 extends AppCompatActivity {
    StoryApiService service;
    int userId;
    int cardId;
    CommentAdapter adapter;
    ArrayList<CommentItem> commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_comment_story_card1);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        cardId = IntentHelper.getId(this);

        RecyclerView recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 어댑터 설정 이후 데이터 로드
        // 1.빈 어댑터로 초기화
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        // 2.데이터를 비동기적으로 가져오는 메서드 호출
        loadComments();
    }

    private void loadComments(){
        service.getComments(cardId).enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                processCommentsResponse(response);
            }

            @Override
            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processCommentsResponse(Response<List<CommentItem>> response){
        if (response.isSuccessful() && response.body() != null) {
            List<CommentItem> newCommentsData = response.body();
            // Call<List<CommentItem>> getCommentsForStory(@Query("cardId") int cardId);
            // 인터페이스에서 Retrofit은 자동으로 JSON 응답을 List<CommentItem> 형식의 객체로 변환해줌
            adapter.setCommentsData(newCommentsData);

        }else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }
}