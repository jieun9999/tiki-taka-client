package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.listeners.PencilIconClickListener;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.request.StoryCardRequest;
import com.android.tiki_taka.models.response.StoryFolderResponse;
import com.android.tiki_taka.models.response.SuccessAndMessageResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.InitializeStack;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.TimeUtils;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoryWritingActivity1 extends AppCompatActivity implements PencilIconClickListener {
    StoryApiService service;
    int userId;
    ArrayList<Uri> selectedUris;
    StoryCardRequest cardRequest;
    int folderId;
    Uri firstUri;
    ImageView thumbnailView;
    private static final int REQUEST_CODE_STORY_FOLDER_EDIT = 123;
    TextView locationView;
    TextView titleView;
    String editedThumbnailUri;
    String storyTitle;
    String location;
    private static final int REQUEST_CODE_IMAGE_COMMENT_INPUT = 456;
    ArrayList<String> comments;
    boolean isAddingToExistingFolder;
    String displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing1);

        setupNetworkAndRetrieveId();
        extractIntentData();
        if (isAddingToExistingFolder) {
            // 기존 폴더에 추가하는 로직 처리
            renderThumbnailFromDB();
        } else {
            // 새로운 스토리 카드 생성 로직 처리
            renderThumbnail();
        }
        setRecyclerView();
        setupUIListeners();

    }

    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        folderId = IntentHelper.getId(this);

    }

    private void extractIntentData(){
        isAddingToExistingFolder = getIntent().getBooleanExtra("isAddingToExistingFolder", false);
        selectedUris = getIntent().getParcelableArrayListExtra("selectedUris");
        if(selectedUris != null){
            firstUri = selectedUris.get(0);
        }

    }

    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new StoryWritingAdapter(selectedUris, this,this));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_STORY_FOLDER_EDIT && resultCode == RESULT_OK && data != null){
                Bundle extras = data.getExtras();
                if (extras != null) {
                    editedThumbnailUri = extras.getString("croppedThumbnailUri");
                    storyTitle = extras.getString("storyTitle");
                    location = extras.getString("location");

                    locationView = findViewById(R.id.textView28);
                    titleView = findViewById(R.id.textView27);
                    ImageUtils.loadImage(editedThumbnailUri, thumbnailView, this);
                    titleView.setText(storyTitle);
                    locationView.setText(location);
                }
        } else if (requestCode == REQUEST_CODE_IMAGE_COMMENT_INPUT && resultCode == RESULT_OK && data != null) {
            comments = data.getStringArrayListExtra("comments");

        }
    }

    private void renderThumbnailFromDB(){
        service.getFolderData(folderId).enqueue(new Callback<StoryFolderResponse>() {
            @Override
            public void onResponse(Call<StoryFolderResponse> call, Response<StoryFolderResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    processThumbNailResponse(response);
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

    private void processThumbNailResponse(Response<StoryFolderResponse> response){

        StoryFolderResponse storyFolderResponse = response.body();
        if(storyFolderResponse.isSuccess()){
            updateUIOnSuccess(storyFolderResponse);

        }else {
            handleFailure(storyFolderResponse);

        }
    }

    private void updateUIOnSuccess(StoryFolderResponse storyFolderResponse){
        thumbnailView = findViewById(R.id.imageView26);
        StoryFolder storyFolder = storyFolderResponse.getStoryFolder();
        displayImage = storyFolder.getDisplayImage();

        if(displayImage == null){
            ImageUtils.loadImage(String.valueOf(selectedUris.get(0)), thumbnailView, this);
        }else {
            ImageUtils.loadImage(storyFolder.getDisplayImage(), thumbnailView, this);
        }

    }

    private void handleFailure(StoryFolderResponse storyFolderResponse){
        String message = storyFolderResponse.getMessage();
        Log.d("fail",message);
    }

    // uri를 보고 동영상인지 이미지 인지 판별하여 다른 방법으로 렌더링함
    private void renderThumbnail(){
        thumbnailView = findViewById(R.id.imageView26);

        if(selectedUris != null && !selectedUris.isEmpty()){

                if(UriUtils.isImageUri(firstUri, this)){
                    // MIME 타입이 이미지인 경우
                    ImageUtils.loadImage(firstUri.toString(), thumbnailView, this);

                }else if(UriUtils.isVideoUri(firstUri, this)){
                    // MIME 타입이 동영상인 경우
                    VideoUtils.loadVideoThumbnail(this, firstUri, thumbnailView);

                }


        }
    }

    private void savePhotoCards() {

        ArrayList<String> uriStrings = convertUrisToStringList(selectedUris);
        Log.d("editedThumbnailUri", String.valueOf(editedThumbnailUri));
        Log.d("displayImage", String.valueOf(displayImage));
        Log.d("firstUri", String.valueOf(firstUri));
        String thumbnailUri = determineThumbnailUri(isAddingToExistingFolder, editedThumbnailUri, displayImage, firstUri);

        createStoryCardRequest(isAddingToExistingFolder, userId, folderId, storyTitle, location, thumbnailUri, uriStrings, comments );
        updateDatabase(firstUri);

    }

    private ArrayList<String> convertUrisToStringList(ArrayList<Uri> selectedUris){
        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : selectedUris) {
            uriStrings.add(uri.toString());
        }
        return uriStrings;
    }

    private String determineThumbnailUri(boolean includeFolderId, String editedThumbnailUri, String displayImage, Uri firstUri){
        if (includeFolderId) {
            // 기존 폴더에 추가하는 로직 처리
            if (TextUtils.isEmpty(editedThumbnailUri)) {
                // 편집된 썸네일이 존재 하는지 여부에 따라 갈림
                return (TextUtils.isEmpty(displayImage)) ? firstUri.toString() : displayImage;
            } else {
                return editedThumbnailUri;
            }
        } else {
            // 새로운 스토리 카드 생성 로직 처리
            return (TextUtils.isEmpty(editedThumbnailUri)) ? firstUri.toString() : editedThumbnailUri;
        }
    }

    private void createStoryCardRequest(boolean includeFolderId, int userId, int folderId, String storyTitle, String location, String thumbnailUri,  ArrayList<String> uriStrings, ArrayList<String> comments) {
        if (includeFolderId) {
            cardRequest = new StoryCardRequest(userId, folderId, uriStrings, storyTitle, location, thumbnailUri, comments);
        } else {
            cardRequest = new StoryCardRequest(userId, uriStrings, storyTitle, location, thumbnailUri, comments);
        }
    }

    private void updateDatabase(Uri firstUri) {
        if(UriUtils.isImageUri(firstUri, this)){
            insertImageStoryCardsInDB();
        }else if(UriUtils.isVideoUri(firstUri, this)) {
            insertVideoStoryCardInDB();
        }
    }

    private void insertImageStoryCardsInDB(){
        service.saveImageStoryCards(cardRequest).enqueue(new Callback<SuccessAndMessageResponse>() {
            @Override
            public void onResponse(Call<SuccessAndMessageResponse> call, Response<SuccessAndMessageResponse> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<SuccessAndMessageResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void insertVideoStoryCardInDB(){
        service.saveVideoStoryCard(cardRequest).enqueue(new Callback<SuccessAndMessageResponse>() {
            @Override
            public void onResponse(Call<SuccessAndMessageResponse> call, Response<SuccessAndMessageResponse> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<SuccessAndMessageResponse> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void ProcessInsertingCardsResponse(Response<SuccessAndMessageResponse> response){
        if(response.isSuccessful()){
            handleResponse(response);

        } else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleResponse(Response<SuccessAndMessageResponse> response){
        if (response.body() != null) {
            String message = response.body().getMessage();

            if(response.body().isSuccess()){
                Log.d("message", message);
                InitializeStack.navigateToAlbumFragment(this);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        } else {
            Log.e("Error","서버 응답 오류");
        }
    }


    private Bundle temporarystoryWritingBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("folderId", folderId);
        bundle.putString("thumbnailUri", firstUri.toString());
        bundle.putParcelableArrayList("selectedUris", selectedUris);
        return bundle;
    }

    @Override
    public void pencilIconClicked(ArrayList<Uri> uriList, int position) {

        ArrayList<Uri> imageUris = new ArrayList<>();
        ArrayList<Uri> videoUri = new ArrayList<>();
        Intent intent = new Intent(this, StoryWritingActivity3.class);

        for (Uri uri : uriList) {
                if (UriUtils.isImageUri(uri, this)) {
                    imageUris.add(uri);
                    intent.putParcelableArrayListExtra("selectedImages", imageUris);
                } else if (UriUtils.isVideoUri(uri, this)) {
                    videoUri.add(uri);
                    intent.putParcelableArrayListExtra("selectedVideo", videoUri);
                }
        }
        intent.putExtra("scrollToPosition", position);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_COMMENT_INPUT);
    }

    private void setupUIListeners(){
        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener(v -> finish());
        TextView uploadBtn = findViewById(R.id.textView34);
        uploadBtn.setOnClickListener(v -> savePhotoCards());
        ImageView editBtn = findViewById(R.id.memoView);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = temporarystoryWritingBundle();
                IntentHelper.navigateToActivity(StoryWritingActivity1.this, StoryWritingActivity2.class, bundle, REQUEST_CODE_STORY_FOLDER_EDIT);
            }
        });
    }
}