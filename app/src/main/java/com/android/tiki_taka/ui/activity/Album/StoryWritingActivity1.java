package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.android.tiki_taka.models.request.CardIdRequest;
import com.android.tiki_taka.models.request.ProgressRequestBody;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    private MultipartBody.Part uriPart;
    private RequestBody userIdBody;
    private RequestBody titleBody;
    private RequestBody locationBody;
    private List<RequestBody> commentsBodies;
    private RequestBody partnerIdBody;
    private RequestBody folderIdBody;
    private boolean fileSizeExceeded;

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
        folderId = IntentHelper.getId(this);
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

    private void saveCards() throws IOException {

        ArrayList<String> uriStrings = convertUrisToStringList(selectedUris);
        String thumbnailUri = determineThumbnailUri(editedThumbnailUri, displayImage, firstUri);

        if(UriUtils.isImageUri(firstUri, this)){
            //이미지 저장 시
            createImageCardRequest(thumbnailUri, uriStrings);
            insertImageStoryCardsInDB();

        } else if (UriUtils.isVideoUri(firstUri, this)) {
            //동영상 저장 시
            createVideoCardRequest(thumbnailUri, uriStrings);
            // 파일 크기가 50MB를 초과했을 때, 중단됨
            if (!fileSizeExceeded) {
                insertVideoStoryCardInDB();
            }
        }

    }

    private ArrayList<String> convertUrisToStringList(ArrayList<Uri> selectedUris){
        ArrayList<String> uriStrings = new ArrayList<>();
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

    private void createImageCardRequest( String thumbnail,  ArrayList<String> uris) throws IOException {

        //<멀티 파트 요청에 맞게 필드 변환>
        // 이미지 URI 리스트 (uris)
        urisParts = new ArrayList<>();
        for (String uriString : uris) {
            File file = new File(UriUtils.getRealPathFromURIString(this, uriString));
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            urisParts.add(MultipartBody.Part.createFormData("uris[]", file.getName(), fileBody)); //여러 파일 전송시 이름 주의!
        }

        // displayImage: 이미지 파일이므로 MultipartBody.Part로 변환
        // 썸네일이 로컬 경로인지, 웹 경로인지에 따라서 다르게 처리함
        if (thumbnail.startsWith("http://") || thumbnail.startsWith("https://")) {
            // 웹 URL 의 경우, 파일 이름을 웹경로로 지정하여 서버에서 바로 찾을 수 있도록 함
            // 파일 이름으로 웹 경로인 thumbnail을 직접 넘겨서, 서버에서 full-path 항목으로 접근 가능함
            RequestBody displayImageUrlBody = RequestBody.create(MediaType.parse("text/plain"), thumbnail);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", thumbnail, displayImageUrlBody);

        } else if(thumbnail.startsWith("content://")){
            // 로컬 파일 시스템의 경로인 경우 (예: content:// URI)
            File displayImageFile = new File(UriUtils.getRealPathFromURIString(this, thumbnail));
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"),displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName() , displayImageRequestBody);

        }else if(thumbnail.startsWith("file:///")){
            // 로컬 파일이면서, 다음과 같은 경로일 경우
            // (예: file:///storage/emulated/0/Android/data/com.android.tiki_taka/files/cropped_1713277432550.jpg)
            // 파일 경로에서 "file://" 부분을 제거하고 파일을 생성해서, 멀티파트로 서버에 전송함
            String filePath = thumbnail.substring(7);
            File displayImageFile = new File(filePath);
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"),displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName() , displayImageRequestBody);
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

    private void createVideoCardRequest(String thumbnail,  ArrayList<String> uris) throws IOException {

        //<멀티 파트 요청에 맞게 필드 변환>
        for (String uriString : uris) {
            File file = new File(UriUtils.getRealPathFromURIString(this, uriString));

            // 파일 크기 확인
            long fileSizeInBytes = file.length();
            long fileSizeInMB = fileSizeInBytes / (1024 * 1024);
            // 파일 크기가 30MB를 초과하는 경우 알림 표시 후 함수 중단
            if (fileSizeInMB > 30) {
                showFileSizeExceedAlert();
                fileSizeExceeded = true;
                return;
            }

            RequestBody fileBody = RequestBody.create(MediaType.parse("video/*"), file);
            uriPart = MultipartBody.Part.createFormData("uri", file.getName(), fileBody);
        }

        // displayImage: 이미지 파일이므로 MultipartBody.Part로 변환
        // 썸네일이 로컬 경로인지, 웹 경로인지에 따라서 다르게 처리함
        if (thumbnail.startsWith("http://") || thumbnail.startsWith("https://")) {
            // 웹 URL 의 경우, 파일 이름을 웹경로로 지정하여 서버에서 바로 찾을 수 있도록 함
            // 파일 이름으로 웹 경로인 thumbnail을 직접 넘겨서, 서버에서 full-path 항목으로 접근 가능함
            RequestBody displayImageUrlBody = RequestBody.create(MediaType.parse("text/plain"), thumbnail);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", thumbnail, displayImageUrlBody);

        } else if(thumbnail.startsWith("content://")){
            // 로컬 파일 시스템의 경로인 경우 (예: content:// URI)
            File displayImageFile = new File(UriUtils.getRealPathFromURIString(this, thumbnail));
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"),displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName() , displayImageRequestBody);

        }else if(thumbnail.startsWith("file:///")){
            // 로컬 파일이면서, 다음과 같은 경로일 경우
            // (예: file:///storage/emulated/0/Android/data/com.android.tiki_taka/files/cropped_1713277432550.jpg)
            // 파일 경로에서 "file://" 부분을 제거하고 파일을 생성해서, 멀티파트로 서버에 전송함
            String filePath = thumbnail.substring(7);
            File displayImageFile = new File(filePath);
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"),displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName() , displayImageRequestBody);
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

    private void showFileSizeExceedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("30MB 이하의 파일을 업로드하세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 확인 버튼을 클릭하면 아무런 작업을 하지 않고 대화상자를 닫음
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 프로그레스 바를 업데이트하는 메서드
    private void updateProgressBar(int percentage) {
        // UI 스레드에서 UI 업데이트를 수행해야 합니다.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setProgress(percentage);
                        // 진행률이 0이면 프로그레스 바를 표시합니다.
                        if (percentage == 0) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
            });
            }
        });
    }


    private void insertVideoStoryCardInDB(){
        // 서비스 호출 전에 프로그레스 바를 표시하고 초기화합니다.
        updateProgressBar(0);
        Toast.makeText(this, "최대 20초까지 걸릴 수 있습니다", Toast.LENGTH_LONG).show(); // 최대 20초까지 걸릴 수 있다는 안내 토스트 메시지 표시
        service.saveVideoStoryCard(uriPart, displayImagePart, userIdBody, titleBody, locationBody, commentsBodies, partnerIdBody, folderIdBody).enqueue(new Callback<SuccessAndMessageResponse>() {
            @Override
            public void onResponse(Call<SuccessAndMessageResponse> call, Response<SuccessAndMessageResponse> response) {
                // 서비스 호출 후에 최종 진행률을 설정합니다.
                updateProgressBar(100);
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<SuccessAndMessageResponse> call, Throwable t) {
                updateProgressBar(0); // 실패했으므로 진행률을 다시 0으로 초기화합니다.
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ImageView editBtn = findViewById(R.id.memoView);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = temporarystoryWritingBundle();
                IntentHelper.navigateToActivity(StoryWritingActivity1.this, StoryWritingActivity2.class, bundle, EDIT_FOLDER);
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