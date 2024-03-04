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

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.listeners.PencilIconClickListener;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.request.StoryCardRequest;
import com.android.tiki_taka.models.response.StoryFolderResponse;
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

        ImageUtils.loadImage(storyFolder.getDisplayImage(), thumbnailView, this);

    }

    private void handleFailure(StoryFolderResponse storyFolderResponse){
        String message = storyFolderResponse.getMessage();
        Log.d("fail",message);
    }

    // uri를 보고 동영상인지 이미지 인지 판별하여 다른 방법으로 렌더링함
    private void renderThumbnail(){
        thumbnailView = findViewById(R.id.imageView26);

        if(selectedUris != null && !selectedUris.isEmpty()){
            firstUri = selectedUris.get(0);

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
        convertUrisToStringListAndWrap();
        if(UriUtils.isImageUri(firstUri, this)){
            insertImageStoryCardsInDB();
        }else if(UriUtils.isVideoUri(firstUri, this)) {
            insertVideoStoryCardInDB();
        }

    }

    private void convertUrisToStringListAndWrap(){
        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : selectedUris) {
            uriStrings.add(uri.toString());
        }
        // 아예, 편집을 하지 않은 경우와 다음 액티비티에서 편집을 해온 경우의 차이
        String thumbnailUri = (TextUtils.isEmpty(editedThumbnailUri)) ? firstUri.toString() : editedThumbnailUri;

        cardRequest = new StoryCardRequest(userId, uriStrings ,storyTitle, location, thumbnailUri, comments);
    }

    private void insertImageStoryCardsInDB(){
        service.saveImageStoryCards(cardRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void insertVideoStoryCardInDB(){
        service.saveVideoStoryCard(cardRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void ProcessInsertingCardsResponse( Response<ResponseBody> response){
        if(response.isSuccessful()){
            handleResponse(response);
            InitializeStack.navigateToAlbumFragment(this);

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleResponse(Response<ResponseBody> response){
        if (response.isSuccessful()) {
            try {
                String message = parseResponseData(response);
                Log.d("success",message);

            } catch (JSONException | IOException e) {
                // JSON 파싱 오류 처리 , IOException 처리
                e.printStackTrace();
                Log.e("Error","catch문 오류 발생");
            }
        } else {
            Log.e("Error","서버 응답 오류");
        }
    }


    private String parseResponseData(Response<ResponseBody> response) throws JSONException, IOException {
        String responseJson = response.body().string();
        JSONObject jsonObject = new JSONObject(responseJson);
        return jsonObject.getString("message");
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