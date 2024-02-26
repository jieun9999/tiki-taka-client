package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.CommentAdapter;
import com.android.tiki_taka.listeners.DeleteCommentListener;
import com.android.tiki_taka.models.dtos.CommentIdRequest;
import com.android.tiki_taka.models.dtos.CommentItem;
import com.android.tiki_taka.models.responses.ApiResponse;
import com.android.tiki_taka.services.StoryApiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithCommentStoryCard2 extends AppCompatActivity implements DeleteCommentListener {
    StoryApiService service;
    int userId;
    int partnerId;
    int cardId;
    CommentAdapter adapter;
    ArrayList<CommentItem> commentList;
    RecyclerView recyclerView;
    boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_comment_story_card2);
    }

    public void onDeleteClick(int position) {
        CommentItem commentItemToDelete = commentList.get(position);
        CommentIdRequest commentIdToDelete = new CommentIdRequest(commentItemToDelete.getCommentId());
        deleteCommentFromServer(commentIdToDelete);
    }

    private void deleteCommentFromServer(CommentIdRequest commentIdRequest){
        service.deleteComment(commentIdRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().isSuccess()){
                        // success가 true일 때의 처리
                        loadComments();
                    }
                }else {
                    // success가 false일 때의 처리
                    Log.e("ERROR", "댓글 업로드 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }

}