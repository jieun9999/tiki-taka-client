package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.CommentAdapter;
import com.android.tiki_taka.listeners.DeleteCommentListener;
import com.android.tiki_taka.listeners.EditCommentListener;
import com.android.tiki_taka.models.request.CardIdRequest;
import com.android.tiki_taka.models.request.CommentIdRequest;
import com.android.tiki_taka.models.dto.CommentItem;
import com.android.tiki_taka.models.request.LikeStatusRequest;
import com.android.tiki_taka.models.dto.PartnerDataManager;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.models.response.FolderDeletedResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.InitializeStack;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.LikesUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.TimeUtils;
import com.android.tiki_taka.utils.VideoUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WithCommentStoryCard3 extends AppCompatActivity implements DeleteCommentListener, EditCommentListener {

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
        setContentView(R.layout.activity_with_comment_story_card3);

        setupNetworkAndRetrieveIds();
        loadCardDetails();
        setRecyclerView();
        loadComments();
        setupSendCommentButtonClickListener();
        setupLikeImageViewClickListener();
        showEditOptionsBottomSheet();
    }

    private void setupNetworkAndRetrieveIds(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        cardId = IntentHelper.getId(this);
    }

    private void loadCardDetails(){
        service.getCardDetails(cardId).enqueue(new Callback<StoryCard>() {
            @Override
            public void onResponse(Call<StoryCard> call, Response<StoryCard> response) {
                try {
                    processCardDetailsResponse(response);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<StoryCard> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processCardDetailsResponse(Response<StoryCard> response) throws ParseException {
        if(response.isSuccessful() && response.body() != null){
            StoryCard storyCard = response.body();
            // 상단바
            ImageView profileImgView = findViewById(R.id.imageView41);
            TextView nameView = findViewById(R.id.textView37);
            TextView timeView = findViewById(R.id.textView38);
            ImageUtils.loadImage(storyCard.getUserProfile(), profileImgView, this);
            nameView.setText(storyCard.getUserName());
            String outputDateString = TimeUtils.convertDateString(storyCard.getCreatedAt());
            timeView.setText(outputDateString);

            // 본문
            ImageView cardImgView = findViewById(R.id.thumbnailView);
            ImageView myLikesView = findViewById(R.id.imageView31);
            FrameLayout partnerLikesView = findViewById(R.id.frameLayout9);
            ImageView partnerLikesProfileView = findViewById(R.id.imageView33);
            String videoUrl = storyCard.getVideo();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                if (videoUrl.startsWith("https:")) {
                    ImageUtils.loadImage(storyCard.getVideoThumbnail(), cardImgView, this);

                } else if (videoUrl.startsWith("content:")) {
                    Uri uri = Uri.parse(videoUrl);
                    VideoUtils.loadVideoThumbnail(this, uri, cardImgView);
                }
            }

            //파트너 아이디와 이미지 가져오기
            partnerId = PartnerDataManager.getPartnerId();
            String partnerImg = PartnerDataManager.getPartnerImg();
            Pair<Integer, Integer> likes = LikesUtils.getLikesFor2Users(storyCard, userId, partnerId);
            int myLikes = likes.first;
            int partnerLikes = likes.second;
            if(myLikes == 0){
                ImageUtils.loadDrawableIntoView(this, myLikesView, "akar_icons_heart");
                isLiked = false;
            }else {
                isLiked = true;
            }
            if(partnerLikes == 0){
                partnerLikesView.setVisibility(View.GONE);
            }else {
                ImageUtils.loadImage(partnerImg, partnerLikesProfileView, this);
            }
            //편집 버튼
            ImageView editBtn = findViewById(R.id.editBtn);
            if(userId == storyCard.getUserId()){
                editBtn.setVisibility(View.VISIBLE);
            }else {
                editBtn.setVisibility(View.GONE);
            }

            setupPlayVideo(storyCard.getVideo());

        }else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void setRecyclerView(){
        recyclerView = findViewById(R.id.commentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList,this, true,this);
        recyclerView.setAdapter(adapter);
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
            adapter.setCommentsData(newCommentsData);

        }else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
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

    private void setupSendCommentButtonClickListener(){
        TextView sendCommentButton = findViewById(R.id.send_comment_view);
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    private void postComment(){
        EditText inputCommentView = findViewById(R.id.inputbox_comment);
        String inputText = inputCommentView.getText().toString();

        if(!inputText.isEmpty()){
            //새 댓글 객체 생성
            CommentItem newComment = new CommentItem(cardId, userId, inputText);
            service.postComment(newComment).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if(response.isSuccessful() && response.body() != null){
                        if(response.body().isSuccess()){
                            // success가 true일 때의 처리
                            // 댓글 업로드 성공 후 전체 댓글 목록 새로고침
                            loadComments();
                            inputCommentView.setText("");
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

    private void setupLikeImageViewClickListener(){
        ImageView myLikesView = findViewById(R.id.imageView31);
        myLikesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLiked = !isLiked;
                updateHeartIcon(myLikesView, isLiked);
                updateLikeStatusOnServer(isLiked);

            }
        });
    }
    private void updateHeartIcon(ImageView heartView, boolean isLiked){
        if(isLiked){
            ImageUtils.loadDrawableIntoView(this, heartView, "fluent_emoji_flat_red_heart");
        }else {
            ImageUtils.loadDrawableIntoView(this, heartView, "akar_icons_heart");
        }
    }

    private void updateLikeStatusOnServer(boolean isLiked){
        LikeStatusRequest likeStatusRequest = new LikeStatusRequest(cardId, userId, isLiked, partnerId);

        service.updateLikeStatus(likeStatusRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if (response.body().isSuccess()) {
                        // success가 true일 때의 처리
                        Log.d("success", "좋아요 상태 업데이트 성공");
                    } else {
                        // success가 false일 때의 처리
                        Log.e("ERROR", "좋아요 상태 업데이트 실패");
                    }
                }else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // 뒤로가기를 누를 때마다 상태 변경 결과를 알림
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showEditOptionsBottomSheet(){
        ImageView editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(WithCommentStoryCard3.this);
                bottomSheetDialog.setContentView(R.layout.bottomsheet_edit_image_video_card);

                TextView changeFolder = bottomSheetDialog.findViewById(R.id.changeFolder);
                changeFolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentHelper.navigateToActivity(WithCommentStoryCard3.this, SelectFolderActivity.class, cardId);
                        bottomSheetDialog.dismiss();
                    }
                });

                TextView deleteBtn = bottomSheetDialog.findViewById(R.id.textView9);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog();
                    }
                });
                TextView cancelBtn = bottomSheetDialog.findViewById(R.id.box2);
                cancelBtn.setOnClickListener( v2->  bottomSheetDialog.dismiss());
                bottomSheetDialog.show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WithCommentStoryCard3.this);

        builder.setTitle("스토리 카드 삭제");
        builder.setMessage("이 게시물을 정말 삭제하시겠습니까? 해당 스토리 카드가 삭제됩니다.");

        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteStoryCardFromServer();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStoryCardFromServer() {
        CardIdRequest cardIdRequest = new CardIdRequest(cardId);
        service.deleteCard(cardIdRequest).enqueue(new Callback<FolderDeletedResponse>() {
            @Override
            public void onResponse(Call<FolderDeletedResponse> call, Response<FolderDeletedResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().isSuccess()){
                        if(response.body().isFolderDeleted()){
                            //isFolderDeleted가 true일 때의 처리
                            InitializeStack.navigateToAlbumFragment(WithCommentStoryCard3.this);

                        } else if (!response.body().isFolderDeleted()) {
                            //isFolderDeleted가 false일때 처리
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                    }else if(!response.body().isSuccess()){
                        // success가 false일 때의 처리
                        Log.e("ERROR", "서버 오류");
                    }
                }else {
                    Log.e("ERROR", "댓글 업로드 실패");
                }
            }

            @Override
            public void onFailure(Call<FolderDeletedResponse> call, Throwable t) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }

    private void setupPlayVideo(String video){
        ImageView playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(v-> VideoUtils.openVideo(this, video));
    }

    public void onEditClick(int position, int commentId, String commentText) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_edit_comment);

        EditText editTextComment = bottomSheetDialog.findViewById(R.id.inputbox_comment);
        TextView buttonSaveComment = bottomSheetDialog.findViewById(R.id.send_comment_view);
        editTextComment.setText(commentText);

        buttonSaveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedComment = editTextComment.getText().toString();
                updateCommentToServer(commentId, updatedComment);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void updateCommentToServer(int commentId, String updatedComment){
        CommentItem newCommentItem = new CommentItem(commentId, updatedComment);
        service.updateComment(newCommentItem).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().isSuccess()){
                        loadComments();

                    }else if(!response.body().isSuccess()){
                        Log.e("ERROR", "서버 응답 오류");
                    }
                }else {

                    Log.e("ERROR", "댓글 업데이트 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }
}