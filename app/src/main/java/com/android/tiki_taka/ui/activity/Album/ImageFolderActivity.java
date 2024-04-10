package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryCardAdapter;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.response.StoryCardsResponse;
import com.android.tiki_taka.models.response.StoryFolderResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.TimeUtils;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImageFolderActivity extends AppCompatActivity implements ItemClickListener {
    int folderId;
    StoryApiService service;
    int userId;
    int clickedCardId;
    StoryCardAdapter adapter;
    RecyclerView recyclerView;
    List<StoryCard> storyCards;
    private static final int REQUEST_CODE_IMAGE_CARD = 111;
    private static final int REQUEST_CODE_TEXT_CARD = 222;
    private static final int REQUEST_CODE_VIDEO_CARD = 333;
    private static final int REQUEST_EDIT_FOLDER = 444;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder);

        setUpCustomToolBar();
        setupNetworkAndRetrieveId();
        setRecyclerView();
        loadThumbnailAndStoryCards();
        setupEditStoryFolder();
        navigateToSelection2Activity();

    }

    private void setUpCustomToolBar(){
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        ImageView backButton = findViewById(R.id.imageView36);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

    }

    private void setRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //빈 어댑터 사용
        adapter = new StoryCardAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void loadThumbnailAndStoryCards(){
        Intent intent = getIntent();
        // case 1: PUSH 알림을 클릭해서 온 경우
        boolean storyNotification = intent.getBooleanExtra("storyNotification", false);
        if(storyNotification){
            folderId = intent.getIntExtra("folderId", -1);
        }else {
            // case 2: 폴더를 클릭해서 온 경우
            folderId = intent.getIntExtra("CLICKED_ITEM_ID", -1); // "CLICKED_ITEM_ID" 키로 저장된 int 값을 가져옴
        }
        // 가져온 아이템 ID를 사용하여 세부 정보를 표시하거나 데이터를 로드
        if (folderId != -1) {
            loadThumbNail();
            loadStoryCards();

        } else {
            Log.e("Error", "서버에서 불러오기에 실패: ID가 유효하지 않습니다.");
        }
    }

    private void loadThumbNail(){
        service.getFolderData(folderId).enqueue(new Callback<StoryFolderResponse>() {
            @Override
            public void onResponse(Call<StoryFolderResponse> call, Response<StoryFolderResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    processThumbNailResponse(response);
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

    private void processThumbNailResponse(Response<StoryFolderResponse> response){
        // 요청 성공 + 응답 존재
        StoryFolderResponse storyFolderResponse = response.body();
        if(storyFolderResponse.isSuccess()){
            updateUIOnSuccess(storyFolderResponse);

        }else {
            handleFailure(storyFolderResponse);

        }
    }

    private void updateUIOnSuccess(StoryFolderResponse storyFolderResponse){
        StoryFolder storyFolder = storyFolderResponse.getStoryFolder();

        //썸네일 뷰 할당하기
        TextView thumbDateView = findViewById(R.id.textView26);
        TextView thumbTitleView = findViewById(R.id.textView27);
        TextView thumbLocView = findViewById(R.id.textView28);
        ImageView thumbBackImgView = findViewById(R.id.imageView26);

        // 서버 날짜 문자열(2024-01-31 12:24:40) => 2023년 12월 25일 (월) 변환
        String inputDateString = storyFolder.getCreatedAt();
        try {
            String outputDateString = TimeUtils.convertDateString(inputDateString);
            thumbDateView.setText(outputDateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        thumbTitleView.setText(storyFolder.getTitle());
        thumbLocView.setText(storyFolder.getLocation());
        ImageUtils.loadImage(storyFolder.getDisplayImage(), thumbBackImgView, this);

        String message = storyFolderResponse.getMessage();
        Log.d("success", message);
    }

    private void handleFailure(StoryFolderResponse storyFolderResponse){
        //success가 false인 경우,
        String message = storyFolderResponse.getMessage();
        Log.d("fail",message);
    }

    private void loadStoryCards(){
        service.getStoryCards(folderId).enqueue(new Callback<StoryCardsResponse>() {
            @Override
            public void onResponse(Call<StoryCardsResponse> call, Response<StoryCardsResponse> response) {
                if(response.isSuccessful() && response.body() != null){   // 요청 성공 + 응답 존재
                    handleStoryCardsResponse(response);

                }else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StoryCardsResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void handleStoryCardsResponse(Response<StoryCardsResponse> response){

        StoryCardsResponse storyCardsResponse = response.body();
        if(storyCardsResponse.isSuccess()){
            updateUIonSuccess(storyCardsResponse);

        }else {
            handleFailure(storyCardsResponse);
        }

    }

    //"오버로딩(Overloading)"이라고 합니다. 함수 오버로딩은 같은 이름의 함수가 서로 다른 매개변수를 가질 수 있도록 허용
    private void updateUIonSuccess(StoryCardsResponse storyCardsResponse){
        storyCards = storyCardsResponse.getStoryCards();

        // 서버에서 가져온 리스트를 어댑터에 추가함
        adapter.setCardsData(storyCards);
        String message = storyCardsResponse.getMessage();
        Log.d("success",message);
    }

    private void handleFailure(StoryCardsResponse storyCardsResponse){
        String message = storyCardsResponse.getMessage();
        Log.d("fail",message);
    }

    @Override
    public void onItemClick(int position) {
        //클릭된 아이템과 그 아이디를 가져옴
        StoryCard clickedCard = storyCards.get(position);
        clickedCardId = clickedCard.getCardId();

        // 스토리 폴더 아래에 있는 스토리 카드가 이미지, 메모, 동영상 3가지 형식이 있기 때문에
        // data_type에 따라 나눠서 다른 액티비티로 이동함
        if("image".equals(clickedCard.getDataType())){
            IntentHelper.navigateToActivity(ImageFolderActivity.this, WithCommentStoryCard1.class, clickedCardId, REQUEST_CODE_IMAGE_CARD);
        } else if ("text".equals(clickedCard.getDataType())) {
            IntentHelper.navigateToActivity(ImageFolderActivity.this, WithCommentStoryCard2.class, clickedCardId, REQUEST_CODE_TEXT_CARD);
        } else if ("video".equals(clickedCard.getDataType())) {
            IntentHelper.navigateToActivity(ImageFolderActivity.this, WithCommentStoryCard3.class, clickedCardId, REQUEST_CODE_VIDEO_CARD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_CARD && resultCode == RESULT_OK) {
            loadStoryCards();
        } else if (requestCode == REQUEST_CODE_TEXT_CARD && resultCode == RESULT_OK) {
            loadStoryCards();
        } else if (requestCode == REQUEST_CODE_VIDEO_CARD && resultCode == RESULT_OK) {
            loadStoryCards();
        } else if (requestCode == REQUEST_EDIT_FOLDER && resultCode == RESULT_OK) {
            loadThumbNail();
        }
    }

    private void setupEditStoryFolder(){
        ImageView editBtn = findViewById(R.id.edit_icon);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentHelper.navigateToActivity(ImageFolderActivity.this, FolderEditActivity.class, folderId, REQUEST_EDIT_FOLDER);
            }
        });
    }

    private void navigateToSelection2Activity(){
        ImageView plusBtn = findViewById(R.id.plus_image);
        plusBtn.setOnClickListener(v -> IntentHelper.navigateToActivity(this, SelectionActivity2.class, folderId));

    }

}