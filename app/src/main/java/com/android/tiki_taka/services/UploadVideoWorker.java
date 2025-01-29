package com.android.tiki_taka.services;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.tiki_taka.models.response.SuccessAndMessageResponse;
import com.android.tiki_taka.ui.activity.Album.ImageFolderActivity;
import com.android.tiki_taka.utils.NotificationUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.android.tiki_taka.utils.UriUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadVideoWorker extends Worker {
    StoryApiService service;
    //retrieve
    ArrayList<String> uris;
    String thumbnail;
    int userId;
    String storyTitle;
    String location;
    ArrayList<String> comments;
    int partnerId;
    int folderId;

    //convert to MutiPart
    private MultipartBody.Part displayImagePart;
    private MultipartBody.Part uriPart;
    private RequestBody userIdBody;
    private RequestBody titleBody;
    private RequestBody locationBody;
    private List<RequestBody> commentsBodies;
    private RequestBody partnerIdBody;
    private RequestBody folderIdBody;
    private boolean fileSizeExceeded;
    public static final String TAG = "UploadVideoWorker";
    private UploadStatusChecker uploadStatusChecker;
    private String parentKey;
    int NOTIFICATION_ID; // 3종류 알림이 같은 아이디를 씀

    public UploadVideoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // The doWork() method runs asynchronously on a background thread managed by WorkManager.
    // This is the correct place to handle heavy or time-consuming operations like:
    // constructing RequestBody instances for text and files.
    @NonNull
    @Override
    // background thread
    public Result doWork() {
        try {
            setupNetworkAndRetrieveId();
            retrieveData();
            ConvertToMultipartData();
            if (!fileSizeExceeded) {
                uploadVideo();
            }

            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error uploading video", e);
            return Result.failure();
        }
    }

    private void setupNetworkAndRetrieveId() {
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(getApplicationContext());
        partnerId = SharedPreferencesHelper.getPartnerId(getApplicationContext());
    }

    public void retrieveData() {

        // Data 객체에서 JSON 문자열 가져오기
        String uriStringsJson = getInputData().getString("uris");
        String commentsStringsJson = getInputData().getString("comments");
        String folderIdString = getInputData().getString("folderId");

        Gson gson = new Gson();

        // Type 정보를 사용하여 JSON 문자열을 ArrayList로 역직렬화
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        uris = gson.fromJson(uriStringsJson, type); // uris가 null이 나오는 문제 발생
        Log.d("uris", String.valueOf(uris));
        Type commentListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        comments = gson.fromJson(commentsStringsJson, commentListType);
        thumbnail = getInputData().getString("thumbnail");
        storyTitle = getInputData().getString("title");
        location = getInputData().getString("location");
        // Safely convert folderId to integer
        try {
            folderId = folderIdString != null ? Integer.parseInt(folderIdString) : -1; // Default to -1 if null
        } catch (NumberFormatException e) {
            Log.e("Worker", "Failed to parse folderId, setting default to -1", e);
            folderId = -1; // Set default or handle error appropriately
        }

    }

    public void ConvertToMultipartData() throws Exception {
        //<멀티 파트 요청에 맞게 필드 변환>
        for (String uriString : uris) {
            Uri uri = Uri.parse(uriString);  // uriString을 Uri로 변환
            File file = UriUtils.getFileFromUri(this.getApplicationContext(), uri);  // Uri를 사용하여 파일 얻기

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

            // S3에 업로드될 키 생성
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String currentDate = dateFormat.format(new Date());
            parentKey = currentDate + "/" + file.getName();
            // Firebase 키에 허용되지 않는 문자를 언더스코어로 대체
            parentKey = generateFirebaseSafeKey(parentKey);
        }

        // displayImage: 이미지 파일이므로 MultipartBody.Part로 변환
        // 썸네일이 로컬 경로인지, 웹 경로인지에 따라서 다르게 처리함
        if (thumbnail.startsWith("http://") || thumbnail.startsWith("https://")) {
            // 웹 URL의 경우
            RequestBody displayImageUrlBody = RequestBody.create(MediaType.parse("text/plain"), thumbnail);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", thumbnail, displayImageUrlBody);

        } else if (thumbnail.startsWith("content://")) {
            // 로컬 파일 시스템의 경로인 경우
            Uri thumbnailUri = Uri.parse(thumbnail);  // thumbnail을 Uri로 변환
            File displayImageFile = UriUtils.getFileFromUri(this.getApplicationContext(), thumbnailUri);
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"), displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName(), displayImageRequestBody);

        } else if (thumbnail.startsWith("file:///")) {
            // 로컬 파일 경로
            String filePath = thumbnail.substring(7);
            File displayImageFile = new File(filePath);
            RequestBody displayImageRequestBody = RequestBody.create(MediaType.parse("image/*"), displayImageFile);
            displayImagePart = MultipartBody.Part.createFormData("displayImage", displayImageFile.getName(), displayImageRequestBody);
        }

        // userId, title, location 등의 RequestBody 변환
        userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        titleBody = RequestBody.create(MediaType.parse("text/plain"), storyTitle != null ? storyTitle : "");
        locationBody = RequestBody.create(MediaType.parse("text/plain"), location != null ? location : "");

        // comments RequestBody 변환
        commentsBodies = new ArrayList<>();
        if (comments != null) {
            for (String comment : comments) {
                RequestBody commentBody = RequestBody.create(MediaType.parse("text/plain"), comment);
                commentsBodies.add(commentBody);
            }
        }

        // partnerId 변환
        partnerIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(partnerId));

        if (folderId != -1) {
            folderIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(folderId));
        }
    }


    public String generateFirebaseSafeKey(String fileName) {
        // 키에서 Firebase에 허용되지 않는 문자를 언더스코어로 대체
        String key = fileName.replaceAll("[.$#\\[\\]]", "_");
        return key;
    }

    public void showFileSizeExceedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getApplicationContext());
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

    public void uploadVideo() {
        NOTIFICATION_ID = (int) System.currentTimeMillis();
        // 업로드 상태 체커 인스턴스 생성
        uploadStatusChecker = new UploadStatusChecker(parentKey, getApplicationContext(), NOTIFICATION_ID);
        // 상태 체킹 시작
        uploadStatusChecker.startChecking();

        service.saveVideoStoryCard(uriPart, displayImagePart, userIdBody, titleBody, locationBody, commentsBodies, partnerIdBody, folderIdBody).enqueue(new Callback<SuccessAndMessageResponse>() {
            @Override
            public void onResponse(Call<SuccessAndMessageResponse> call, Response<SuccessAndMessageResponse> response) {
                ProcessInsertingCardsResponse(response);
                // 응답이 오면 상태 체킹 중단
                uploadStatusChecker.stopChecking();

            }

            @Override
            public void onFailure(Call<SuccessAndMessageResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                // 실패 시에도 상태 체킹 중단
                uploadStatusChecker.stopChecking();

            }
        });
    }

    private void ProcessInsertingCardsResponse(Response<SuccessAndMessageResponse> response){
        if(response.isSuccessful()){
            handleResponse(response);

        } else {
           NotificationUtils.NotificationOnFailure(getApplicationContext());
        }
    }

    // workmanager를 사용하여 백그라운드 스레드로 요청을 보냈다면,
    // 나중에 서버에서 응답이 올때 아래와 같이 화면이동이 아닌 알림
    private void handleResponse(Response<SuccessAndMessageResponse> response){
        if (response.body() != null) {
            int folderId = Integer.parseInt(response.body().getMessage());
            NotificationUtils.NotificationOnSuccess(getApplicationContext(), ImageFolderActivity.class, folderId, NOTIFICATION_ID);

        } else {
            NotificationUtils.NotificationOnFailure(getApplicationContext());
        }
    }


}
