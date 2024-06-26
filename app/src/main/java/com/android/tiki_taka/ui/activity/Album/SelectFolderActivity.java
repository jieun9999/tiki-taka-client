package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.ChangeFolderAdapter;
import com.android.tiki_taka.listeners.FolderSelectListener;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.models.response.StoryFoldersResponse;
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

public class SelectFolderActivity extends AppCompatActivity implements FolderSelectListener {
    StoryApiService service;
    int userId;
    int cardId;
    ChangeFolderAdapter adapter;
    RecyclerView recyclerView;
    int selectedFolderId;
    int partnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);

        setupNetworkAndRetrieveId();
        setRecyclerView();
        loadStoryFolders();
        setupSaveBtnClickLister();

    }

    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        cardId = IntentHelper.getIdFromIntent(this);
        partnerId = SharedPreferencesHelper.getPartnerId(this);

    }

    private void setRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //빈 어댑터 사용
        adapter = ChangeFolderAdapter.withFolderSelection(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void loadStoryFolders() {
        service.getStoryFolders(userId, partnerId).enqueue(new Callback<StoryFoldersResponse>() {
            @Override
            public void onResponse(Call<StoryFoldersResponse> call, Response<StoryFoldersResponse> response) {
                processStoryFolderResponse(response);

            }

            @Override
            public void onFailure(Call<StoryFoldersResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });

    }

    private void processStoryFolderResponse(Response<StoryFoldersResponse> response){
        if(response.isSuccessful() && response.body() != null){

            StoryFoldersResponse storyFoldersResponse = response.body();
            if(storyFoldersResponse.isSuccess()){
                updateUIOnSuccess(storyFoldersResponse);

            }else {
                handleFailure(storyFoldersResponse);

            }

        }else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void updateUIOnSuccess(StoryFoldersResponse storyFoldersResponse){

        List<StoryFolder> storyFolders = storyFoldersResponse.getStoryFolders();
        adapter.setData(storyFolders);

        String message = storyFoldersResponse.getMessage();
        Log.d("success", message);
    }

    private void handleFailure(StoryFoldersResponse storyFoldersResponse){
        String message = storyFoldersResponse.getMessage();
        Log.e("fail", message);
    }

    private void setupSaveBtnClickLister(){
        Button saveBtn = findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFolderId != -1){
                    saveSelectedFolderToDB();
                }
            }
        });
    }

    private void saveSelectedFolderToDB(){
        StoryCard updatedCard = StoryCard.fromCardIdAndFolderId(cardId, selectedFolderId, userId, partnerId);
        service.updateFolderLocationInCard(updatedCard).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    handleSuccessResponse(response.body());
                }else {
                    Log.e("ERROR", "폴더 위치 업데이트 실패");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ERROR", "네트워크 오류");
            }
        });
    }

    private void handleSuccessResponse(ApiResponse response) {
        if (response.isSuccess()) {
            IntentHelper.passToAlbumFragment(SelectFolderActivity.this);
        } else {
            Log.e("ERROR", "서버 응답 오류");
        }
    }

    @Override
    public void onFolderItemSelect(int folderId) {
        selectedFolderId = folderId;
    }
}