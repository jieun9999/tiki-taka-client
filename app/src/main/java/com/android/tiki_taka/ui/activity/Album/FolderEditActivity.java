package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.ThumbnailCheckAdapter;
import com.android.tiki_taka.listeners.ThumbnailUpdateListener;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.models.response.StoryCardsResponse;
import com.android.tiki_taka.models.response.StoryFolderResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FolderEditActivity extends AppCompatActivity implements ThumbnailUpdateListener {

    StoryApiService service;
    int userId;
    int folderId;
    Uri sourceUri; // 소스 이미지의 Uri
    Uri destinationUri; // 크롭된 이미지를 저장할 Uri
    ImageView imageViewToCrop;
    String newTitleText;
    String newLocationText;
    String croppedimageUriString;
    List<StoryCard> storyCards;
    ArrayList<Uri> selectedUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_edit);

        setupNetworkAndRetrieveIds();
        setupCancelAndSaveBtn();
        loadFolderTexts();
        countTitleAndLocationTexts();
        getStoryCards();
    }

    private void setupNetworkAndRetrieveIds(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        folderId = IntentHelper.getId(this);
    }

    private void setupCancelAndSaveBtn(){
        TextView cancelBtn = findViewById(R.id.textView33);
        TextView saveBtn = findViewById(R.id.textView34);

        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(croppedimageUriString)){
                    cropAlertDialog();

                }else {
                    updateFolderInDB();

                }
            }
        });
    }

    private void cropAlertDialog(){
        // 크롭되지 않았다면 사용자에게 알림창 표시
        AlertDialog.Builder builder = new AlertDialog.Builder(FolderEditActivity.this);
        builder.setMessage("사진을 크롭하세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void loadFolderTexts(){
        service.getFolderData(folderId).enqueue(new Callback<StoryFolderResponse>() {
            @Override
            public void onResponse(Call<StoryFolderResponse> call, Response<StoryFolderResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    processFolderDataResponse(response);
                }else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StoryFolderResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processFolderDataResponse(Response<StoryFolderResponse> response){
        // 요청 성공 + 응답 존재
        StoryFolderResponse storyFolderResponse = response.body();
        if(storyFolderResponse.isSuccess()){
            loadUIFolderTexts(storyFolderResponse);

        }else {
            handleFailure(storyFolderResponse);

        }
    }

    private void loadUIFolderTexts(StoryFolderResponse storyFolderResponse){
        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
        EditText editTextLocation = findViewById(R.id.editTextLocation);
        StoryFolder storyFolder = storyFolderResponse.getStoryFolder();

        editTextStoryTitle.setText(storyFolder.getTitle());
        editTextLocation.setText(storyFolder.getLocation());

        }

    private void handleFailure(StoryFolderResponse storyFolderResponse){
        //success가 false인 경우,
        String message = storyFolderResponse.getMessage();
        Log.d("fail",message);
    }

    private Uri createUniqueDestinationUri() {
        String fileName = "cropped_" + System.currentTimeMillis() + ".jpg";
        File outFile = new File(getExternalFilesDir(null), fileName);
        return Uri.fromFile(outFile);
        // 만약 destinationUri가 크롭 작업마다 동일하게 설정된다면, 이전의 크롭 결과를 덮어쓰게 되어 항상 같은 resultUri를 얻게 될 수 있습니다.
    }

    private void countTitleAndLocationTexts(){
        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
        newTitleText = editTextStoryTitle.getText().toString();
        TextView textViewStoryTitleCount = findViewById(R.id.textViewStoryTitleCount);
        editTextStoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                newTitleText = s.toString();
                textViewStoryTitleCount.setText(newTitleText.length() + "/50");
            }
        });

        EditText editTextLocation = findViewById(R.id.editTextLocation);
        newLocationText = editTextLocation.getText().toString();
        TextView textViewLocationCount = findViewById(R.id.textViewLocationCount);
        editTextLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                newLocationText = s.toString();
                textViewLocationCount.setText(newLocationText.length() + "/30");
            }
        });
    }

    private void getStoryCards(){
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

    private void updateUIonSuccess(StoryCardsResponse storyCardsResponse){
        storyCards = storyCardsResponse.getStoryCards();

        String message = storyCardsResponse.getMessage();
        Log.d("success",message);

        // 네트워크 호출이 성공한 후에 다음 메서드를 실행합니다 (null 오류 방지)
        updateSelectedUrisFromStoryCards();
        setRecyclerView();
        setupCropListener();

    }

    private void handleFailure(StoryCardsResponse storyCardsResponse){
        String message = storyCardsResponse.getMessage();
        Log.d("fail",message);
    }

    private void updateSelectedUrisFromStoryCards(){
        selectedUris = new ArrayList<>();
        for(StoryCard storyCard : storyCards){
            Uri uri = null;

            if(storyCard.getImage() != null && !storyCard.getImage().isEmpty()){
                uri = Uri.parse(storyCard.getImage());

            } else if (storyCard.getVideo() != null && !storyCard.getVideo().isEmpty()) {
                uri = Uri.parse(storyCard.getVideo());
            }

            if(uri != null){
                selectedUris.add(uri);
            }
        }
    }

    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.checkCardRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new ThumbnailCheckAdapter(selectedUris, this, this));
    }

    private void setupCropListener(){
        imageViewToCrop = findViewById(R.id.cropped_image);

        sourceUri = selectedUris.get(0);

        if (UriUtils.isVideoUri(sourceUri, this)) {
            sourceUri = VideoUtils.getThumbNailUri(this, sourceUri);
        }
        ImageUtils.loadImage(String.valueOf(sourceUri), imageViewToCrop, this);

        destinationUri = createUniqueDestinationUri();
        imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));
    }


    private void UCorpSettings(Uri sourceUri, Uri destinationUri){
        int maxWidthPx = dpToPx(412); // 412dp를 픽셀로 변환
        int maxHeightPx = dpToPx(200); // 200dp를 픽셀로 변환

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(412, 200) // 원하는 비율로 설정
                .withMaxResultSize(maxWidthPx, maxHeightPx) // 최대 결과 이미지 크기
                .start(this);
    }

    public int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onUpdateThumbnail(Uri uri) {
        // 대표 사진들 중에서 체크 박스를 클릭하면 썸네일이 해당 사진으로 바뀜
        imageViewToCrop = findViewById(R.id.cropped_image);

        // imageViewToCrop 클릭하면, 크롭 실행됨
        sourceUri = uri;
        if (UriUtils.isVideoUri(sourceUri, this)) {
            sourceUri = VideoUtils.getThumbNailUri(this, sourceUri);
        }
        ImageUtils.loadImage(String.valueOf(sourceUri), imageViewToCrop, this);

        destinationUri = createUniqueDestinationUri();
        imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri resultUri = UCrop.getOutput(data);
            croppedimageUriString = resultUri.toString();
            ImageUtils.loadImage(croppedimageUriString, imageViewToCrop, this);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("UCropError", "Crop error: ", cropError);
        }
    }

    private void updateFolderInDB(){
        StoryFolder newStoryFolder = new StoryFolder(folderId, croppedimageUriString, newTitleText, newLocationText);
        service.updateFolder(newStoryFolder).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    if (response.body().isSuccess()) {
                        // success가 true일 때의 처리
                        // DB 업데이트된 후에만 이전 액티비티로 결과를 반환하고 현재 액티비티를 종료하게 됩니다.
                        IntentHelper.setResultAndFinish(FolderEditActivity.this, RESULT_OK);
                        Log.d("success", "폴더 상태 업데이트 성공");
                    } else {
                        // success가 false일 때의 처리
                        Log.e("ERROR", "폴더 상태 업데이트 실패");
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
}