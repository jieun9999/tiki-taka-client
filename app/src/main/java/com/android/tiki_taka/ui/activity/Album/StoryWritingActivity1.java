package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.listeners.PencilIconClickListener;
import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.response.StoryFolderResponse;
import com.android.tiki_taka.models.response.SuccessAndMessageResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.services.UploadVideoWorker;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.IntentHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoryWritingActivity1 extends AppCompatActivity implements PencilIconClickListener {
    StoryApiService service;
    int userId;
    ArrayList<Uri> selectedUris;
    int folderId;
    Uri firstUri;
    ImageView thumbnailView;
    TextView locationView;
    TextView titleView;
    String editedThumbnailUri;
    String storyTitle;
    String location;
    ArrayList<String> comments;
    boolean isExistingFolder;
    String displayImage;
    private static final int EDIT_FOLDER = 123;
    private static final int INPUT_IMAGE_COMMENT = 456;
    int partnerId;
    private MultipartBody.Part displayImagePart;
    private List<MultipartBody.Part> urisParts;
    private RequestBody userIdBody;
    private RequestBody titleBody;
    private RequestBody locationBody;
    private List<RequestBody> commentsBodies;
    private RequestBody partnerIdBody;
    private RequestBody folderIdBody;
    ArrayList<String> uriStrings;
    String thumbnailUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing1);

        setupNetworkAndRetrieveId();
        extractIntentData();
        renderThumbnailBasedOnCondition(isExistingFolder);
        setRecyclerView();
        setupUIListeners();

    }

    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
        partnerId = SharedPreferencesHelper.getPartnerId(this);
    }

    private void extractIntentData(){
        folderId = IntentHelper.getIdFromIntent(this);
        isExistingFolder = getIntent().getBooleanExtra("isExistingFolder", false);
        selectedUris = getIntent().getParcelableArrayListExtra("selectedUris");
        if(selectedUris != null){
            firstUri = selectedUris.get(0);
        }

    }

    private void renderThumbnailBasedOnCondition(boolean isAddingToExistingFolder){
        if (isAddingToExistingFolder) {
            // 기존 폴더에 추가하는 로직 처리
            renderThumbnailFromDB();
        } else {
            // 새로운 스토리 카드 생성 로직 처리
            renderThumbnail();
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
            determineThumbnailViewBasedOnUriType();
        }else {
            ImageUtils.loadImage(storyFolder.getDisplayImage(), thumbnailView, this);
        }
    }

    private void determineThumbnailViewBasedOnUriType(){
        Uri firstUri = selectedUris.get(0);
        if(UriUtils.isVideoUri(firstUri, this)){
            VideoUtils.loadVideoThumbnail(this, firstUri, thumbnailView);
        } else if (UriUtils.isImageUri(firstUri, this)) {
            ImageUtils.loadImage(String.valueOf(firstUri), thumbnailView, this);
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

    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new StoryWritingAdapter(selectedUris, this,this));
    }

    private void saveCards() throws Exception {

        ArrayList<String> uriStrings = convertUrisToStringList(selectedUris);
        thumbnailUri = determineThumbnailUri(editedThumbnailUri, displayImage, firstUri);

        if(UriUtils.isImageUri(firstUri, this)){
            //이미지 저장 시
            createImageCardRequest(thumbnailUri, uriStrings);
            insertImageStoryCardsInDB();

        } else if (UriUtils.isVideoUri(firstUri, this)) {
            //동영상 저장 시
            // Create a WorkRequest
            enqueueVideoUpload();
        }

    }

    private ArrayList<String> convertUrisToStringList(ArrayList<Uri> selectedUris){
        uriStrings = new ArrayList<>();
        for (Uri uri : selectedUris) {
            uriStrings.add(uri.toString());
        }
        return uriStrings;
    }

    private String determineThumbnailUri(String editedThumbnailUri, String displayImage, Uri firstUri){
        if (isExistingFolder) {
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

    private void createImageCardRequest( String thumbnail,  ArrayList<String> uris) throws Exception {

        //<멀티 파트 요청에 맞게 필드 변환>
        urisParts = new ArrayList<>();
        for (String uriString : uris) {
            Uri uri = Uri.parse(uriString);
            File file = UriUtils.getFileFromUri(this, uri);  // 파일을 얻음
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            urisParts.add(MultipartBody.Part.createFormData("uris[]", file.getName(), fileBody));  // 여러 파일 전송 시 이름 주의!
        }

        // displayImage: 이미지 파일이므로 MultipartBody.Part로 변환
        if (thumbnail.startsWith("http://") || thumbnail.startsWith("https://")) {
            RequestBody displayImageUrlBody = RequestBody.create(MediaType.parse("text/plain"), thumbnail);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", thumbnail, displayImageUrlBody);

        } else {
            Uri thumbnailUri = Uri.parse(thumbnail);
            File displayImageFile = UriUtils.getFileFromUri(this, thumbnailUri);  // 썸네일을 파일로 얻음
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"), displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName(), displayImageRequestBody);
        }

        // userId: RequestBody로 변환
        userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        // title: RequestBody로 변환
        titleBody = RequestBody.create(MediaType.parse("text/plain"), storyTitle != null ? storyTitle : "");
        // location: RequestBody로 변환
        locationBody = RequestBody.create(MediaType.parse("text/plain"), location != null ? location : "");
        // comments: 각각의 댓글을 RequestBody로 변환한 후 리스트로 묶음
        commentsBodies = new ArrayList<>();
        if(comments != null){
            for (String comment : comments) {
                RequestBody commentBody = RequestBody.create(MediaType.parse("text/plain"), comment);
                commentsBodies.add(commentBody);
            }
        }
        // partnerId: RequestBody로 변환
        partnerIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(partnerId));

        if (isExistingFolder) {
            // 기존 폴더에 추가하는 경우에는 folderIdBody를 인자로 추가함
            folderIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(folderId));

        }

    }

    private void insertImageStoryCardsInDB(){
        // 데이터가 잘 넘어감
        Log.d("urisParts", urisParts.toString());
        Log.d("displayImagePart", displayImagePart.toString());
        Log.d("userIdBody", userIdBody.toString());
        Log.d("titleBody", titleBody.toString());
        Log.d("locationBody", locationBody.toString());
        Log.d("commentsBodies", commentsBodies.toString());
        Log.d("partnerIdBody", partnerIdBody.toString());

        service.saveImageStoryCards(urisParts, displayImagePart, userIdBody, titleBody, locationBody, commentsBodies, partnerIdBody, folderIdBody).enqueue(new Callback<SuccessAndMessageResponse>() {
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


    // You will enqueue the work from an Activity or any other component in your application
    public void enqueueVideoUpload(){
        // attach data to the workRequest
        Data.Builder data = new Data.Builder();

        // Use Gson to convert arrays to JSON strings
        Gson gson = new Gson();
        String uriStringsJson = gson.toJson(uriStrings);
        data.putString("uris", uriStringsJson);
        data.putString("thumbnail", thumbnailUri);
        data.putString("title", storyTitle);
        data.putString("location", location);
        String commentsStringsJson = gson.toJson(comments);
        data.putString("comments", commentsStringsJson);
        if (isExistingFolder) {
            data.putString("folderId", String.valueOf(folderId));
        } else {
            // 기존 폴더가 아닐 때는 folderId로 "-1" 사용
            data.putString("folderId", "-1");
        }

        // create the input data for the work request
        Data inputData = data.build();

        //  WorkRequest (and its subclasses) define how and when it should be run.
        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadVideoWorker.class)
                        .setInputData(inputData)
                        .build();
        WorkManager.getInstance(this).enqueue(uploadWorkRequest);

        // 백그라운드 WorkRequest 스레드 요청 후에 앨범화면으로 이동시킴
        IntentHelper.passToAlbumFragment(this);
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
                IntentHelper.passToAlbumFragment(this);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        } else {
            Log.e("Error","서버 응답 오류");
        }
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
        startActivityForResult(intent, INPUT_IMAGE_COMMENT);
    }

    private void setupUIListeners(){
        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener(v -> finish());
        TextView uploadBtn = findViewById(R.id.textView34);
        uploadBtn.setOnClickListener(v -> {
            try {
                saveCards();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ImageView editBtn = findViewById(R.id.memoView);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = temporarystoryWritingBundle();
                IntentHelper.navigateToActivityForResultWithBundle(StoryWritingActivity1.this, StoryWritingActivity2.class, bundle, EDIT_FOLDER);
            }
        });
    }

    private Bundle temporarystoryWritingBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("folderId", folderId);
        bundle.putString("thumbnailUri", firstUri.toString());
        bundle.putParcelableArrayList("selectedUris", selectedUris);
        bundle.putBoolean("isExistingFolder", isExistingFolder);
        return bundle;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_FOLDER && resultCode == RESULT_OK && data != null){
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
        }  else if (requestCode == INPUT_IMAGE_COMMENT && resultCode == RESULT_OK && data != null) {
            comments = data.getStringArrayListExtra("comments");

        }
    }

}