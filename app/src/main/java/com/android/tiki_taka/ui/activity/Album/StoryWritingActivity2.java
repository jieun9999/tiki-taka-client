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
import com.android.tiki_taka.models.response.StoryCardsResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
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


public class StoryWritingActivity2 extends AppCompatActivity implements ThumbnailUpdateListener {
    StoryApiService service;
    int folderId;
    Uri sourceUri; // 소스 이미지의 Uri
    Uri destinationUri; // 크롭된 이미지를 저장할 Uri
    ImageView imageViewToCrop;
    ArrayList<Uri> selectedUris;
    String newTitleText;
    String newLocationText;
    String uncroppedimageUriString;
    String croppedimageUriString;
    boolean isExistingFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        setupNetwork();
        setupCancelAndSaveBtnListener();
        extractIntentData();
        loadFolderThumbnail();
        setupRecyclerViewBasedOnFolderType();
        countTitleAndLocationTexts();
        setupThumbnailCropListener();

    }

    private void setupNetwork(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
    }

    private void setupCancelAndSaveBtnListener(){
        TextView cancelBtn = findViewById(R.id.textView33);
        TextView saveBtn = findViewById(R.id.textView34);

        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(croppedimageUriString)){
                    cropAlertDialog();

                }else {
                    Intent resultIntent = new Intent();
                    Bundle bundle = writtenFolderWritingBundle();
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private void cropAlertDialog(){
        // 크롭되지 않았다면 사용자에게 알림창 표시
        AlertDialog.Builder builder = new AlertDialog.Builder(StoryWritingActivity2.this);
        builder.setMessage("사진을 크롭하세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Bundle writtenFolderWritingBundle(){
        Bundle bundle = new Bundle();
        // croppedThumbnailUri, storyTitle, location
        bundle.putString("croppedThumbnailUri", croppedimageUriString);
        bundle.putString("storyTitle", newTitleText);
        bundle.putString("location", newLocationText);
        return bundle;
    }

    private void extractIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            folderId = extras.getInt("folderId");
            uncroppedimageUriString = extras.getString("thumbnailUri");
            selectedUris = extras.getParcelableArrayList("selectedUris");
            sourceUri = Uri.parse(uncroppedimageUriString);
            destinationUri = createUniqueDestinationUri();
            isExistingFolder = getIntent().getBooleanExtra("isExistingFolder", false);
        }
    }

    private Uri createUniqueDestinationUri() {
        String fileName = "cropped_" + System.currentTimeMillis() + ".jpg";
        File outFile = new File(getExternalFilesDir(null), fileName);
        return Uri.fromFile(outFile);
        // 만약 destinationUri가 크롭 작업마다 동일하게 설정된다면, 이전의 크롭 결과를 덮어쓰게 되어 항상 같은 resultUri를 얻게 될 수 있습니다.
    }

    private void loadFolderThumbnail(){
        imageViewToCrop = findViewById(R.id.cropped_image);
            // 받아온 uncroppedimageUriString가 이미지 형식 or 동영상 형식 인지 구분하여 초기 이미지 로드하기
            if (UriUtils.isVideoUri(sourceUri, this)) {
                VideoUtils.loadVideoThumbnail(this, sourceUri, imageViewToCrop);

            } else if (UriUtils.isImageUri(sourceUri, this)) {
                ImageUtils.loadImage(uncroppedimageUriString, imageViewToCrop, this);
            }
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

    private void countTitleAndLocationTexts(){
        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
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

    private void setupThumbnailCropListener(){
        if (UriUtils.isVideoUri(sourceUri, this)) {
            sourceUri = VideoUtils.getThumbNailUri(this, sourceUri);
            imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));

        } else if (UriUtils.isImageUri(sourceUri, this)) {
            imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));
        }
    }

    private void setupRecyclerViewBasedOnFolderType() {
        //기존 폴더가 있는 경우에만 서버에서 추가 데이터를 로드 후 리사이클러뷰 설정
        if(isExistingFolder){
            loadExistingUrisFromFolderAndSetRecyclerView(folderId);
        }else {
            setRecyclerView();
        }
    }

    private void loadExistingUrisFromFolderAndSetRecyclerView(int folderId) {
        service.getStoryCards(folderId).enqueue(new Callback<StoryCardsResponse>() {
            @Override
            public void onResponse(Call<StoryCardsResponse> call, Response<StoryCardsResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    StoryCardsResponse storyCardsResponse = response.body();

                    if(storyCardsResponse.isSuccess()){
                        List<StoryCard> storyCards = storyCardsResponse.getStoryCards();
                        ArrayList<Uri> existingUris = new ArrayList<>();
                        filterAndAddValidUris(storyCards, existingUris);
                        selectedUris.addAll(existingUris);
                        setRecyclerView();

                    }else {
                        String message = storyCardsResponse.getMessage();
                        Log.d("fail",message);
                    }

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

    private void filterAndAddValidUris(List<StoryCard> storyCards, ArrayList<Uri> existingUris) {
        // 이미지와 비디오 카드를 모두 담아야 함
        for (StoryCard storyCard : storyCards) {
            String imageUriString = storyCard.getImage();
            String videoUriString = storyCard.getVideo();
            String dataType = storyCard.getDataType();

            if (imageUriString != null && "image".equals(dataType)) {
                Uri uri = Uri.parse(imageUriString);
                existingUris.add(uri);
            }

            if(videoUriString != null && "video".equals(dataType)){
                Uri uri = Uri.parse(videoUriString);
                existingUris.add(uri);
            }
        }
    }


    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.checkCardRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new ThumbnailCheckAdapter(selectedUris, this, this));
    }


    @Override
    public void onUpdateThumbnail(Uri uri) {
        // 썸네일뷰 업데이트 로직
        ImageUtils.loadImage(String.valueOf(uri), imageViewToCrop, this);
        if(UriUtils.isVideoUri(uri, this)){
            sourceUri = VideoUtils.getThumbNailUri(this, uri);
        }else {
            sourceUri = uri;
        }
        destinationUri = createUniqueDestinationUri();
        imageViewToCrop.setOnClickListener(v -> UCorpSettings(sourceUri, destinationUri));
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

}