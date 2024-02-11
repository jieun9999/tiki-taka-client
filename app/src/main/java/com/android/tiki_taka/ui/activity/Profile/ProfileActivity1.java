package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Sign.SigninActivity1;
import com.android.tiki_taka.ui.activity.Sign.SigninActivity2;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity1 extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100; // 권한 요청을 구별하기 위한 고유한 요청 코드, 런타임 권한 요청시 사용
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_GALLERY_BACK_IMG = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4; // 프로필 사진 변경을 위한 요청 코드(카메라)
    private static final int REQUEST_GALLERY_IMAGE = 5; // 프로필 사진 변경을 위한 요청 코드(갤러리)
    private static final int REQUEST_BACKGROUND_IMAGE = 6; // 배경 사진 변경을 위한 요청 코드(갤러리)

    private int currentAction;

    private ListView listView;
    private String[] options = {"로그아웃", "비밀번호 변경하기", "알림 동의 설정", "상대방과 연결끊기", "회원 탈퇴"};

    ImageView profileImage;
    ImageView backImage;
    ImageView galleryBtn;
    TextView nameView;
    TextView messageView;
    ProfileApiService service;
    int userId; // 유저 식별 정보
    private Uri imageUri; //카메라 앱이 전달받을 파일경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile1);

        //기본 액션바를 사용
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("내 정보 설정"); // 액션바 타이틀 설정
            actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        profileImage = findViewById(R.id.imageView28);
        backImage = findViewById(R.id.imageView25);
        galleryBtn = findViewById(R.id.imageView27);
        nameView = findViewById(R.id.textView23);
        messageView = findViewById(R.id.textView24);

        // 저장된 프로필 정보 불러오기
        getData();

        // 프로필 사진 변경
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity1.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.profile_image_bottom_sheet_dialog, null);
                bottomSheetDialog.setContentView(bottomSheetView);

                bottomSheetView.findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAction = REQUEST_CAMERA;
                        requestCameraPermissions();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetView.findViewById(R.id.choose_from_gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentAction = REQUEST_GALLERY;
                        requestStoragePermissions();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAction = REQUEST_GALLERY_BACK_IMG;
                requestStoragePermissions();
            }
        });

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileScreen(ProfileActivity4.class);
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileScreen(ProfileActivity5.class);
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.profile_list_item, R.id.text_view_item, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (options[position].equals("로그아웃")) {

                    SharedPreferencesHelper.saveAutoLoginState(ProfileActivity1.this,false);
                    InitializeStackAndNavigateToLogin1Screen();

                } else if (options[position].equals("비밀번호 변경하기")) {

                    InitializeStackAndNavigateToLogin2Screen();

                } else if (options[position].equals("알림 동의 설정")) {

                    goToSetting();

                } else if (options[position].equals("상대방과 연결끊기")) {

                    navigateToProfileScreen(ProfileActivity3.class);

                } else if (options[position].equals("회원 탈퇴")){

                    checkConnectStateAndNavigateToProfileScreen();

                }
            }
        });
    }

    private void InitializeStackAndNavigateToLogin1Screen(){
        // 로그인_1 화면으로 이동하면서 스택 초기화
        Intent intent = new Intent(getApplicationContext(), SigninActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    private void InitializeStackAndNavigateToLogin2Screen(){
        // 로그인_2 화면으로 이동하면서 스택 초기화
        Intent intent = new Intent(getApplicationContext(), SigninActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    private void navigateToProfileScreen(Class<?> profileActivityClass) {
        // <?>는 와일드카드 타입으로, 어떤 클래스든 받아들일 수 있다는 것을 의미
        Intent intent = new Intent(ProfileActivity1.this, profileActivityClass);
        startActivity(intent);
    }

    public void checkConnectStateAndNavigateToProfileScreen(){
        service.checkConnectState(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleConnectStateResponse(response);
                } else {
                    // 서버 응답 실패 처리
                    showToast("서버 응답 오류: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void handleConnectStateResponse(Response<ResponseBody> response) {
        try {
            String responseBodyString = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBodyString);
            getUserState(jsonObject);

        } catch (IOException | JSONException e) {
            handleResponseError(e);
        }
    }

    private void getUserState(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getBoolean("success")) {
            int userState = jsonObject.getInt("userState");
            if (userState == 0) {
                // 현재 연결 상태를 확인한뒤, 연결이 끊어져 있으면 프로필_6 화면으로 이동
                navigateToProfileScreen(ProfileActivity6.class);
            } else {
                // 현재 연결 상태를 확인한 뒤, 연결이 되어 있으면 프로필_2 화면으로 이동
                navigateToProfileScreen(ProfileActivity2.class);
            }

        } else {
            // 프로필이 존재하지 않는 경우의 처리 로직
            String errorMessage = jsonObject.getString("message");
            showToast(errorMessage);
        }
    }

    private void handleResponseError(Exception e) {
        e.printStackTrace();
        showToast("데이터 처리 오류");
    }

    // 사용자 경험(UX)을 최적화: onResume()에서 사용자의 기타 정보를 업데이트
    @Override
    protected void onResume() {
        super.onResume();
        getNameAndMessage();
    }

    private void getData(){
        Call<ResponseBody> call = service.getMyModalData(userId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    getDataResponseProcess(response);
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //네트워크 오류 처리
                    Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                }
            });
        }

    private void getDataResponseProcess(Response<ResponseBody> response){
        if(response.isSuccessful()){
            try {
                handleSuccessfulResponse(response);

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleSuccessfulResponse(Response<ResponseBody> response) throws JSONException, IOException {
        // 서버로부터 응답 본문을 문자열로 변환
        String responseString = response.body().string();
        //문자열을 JSON객체로 변환
        JSONObject jsonObject = new JSONObject(responseString);

        if(jsonObject.getBoolean("success")){
            // 성공적으로 데이터를 가져온 경우
            handleUserData(jsonObject);

        }else {
            // 데이터를 가져오는데 실패한 경우
            handleFailureMessage(jsonObject);
        }
    }

    private void handleUserData(JSONObject jsonObject) throws JSONException {
        // 성공적으로 데이터를 가져온 경우
        JSONObject userData = jsonObject.getJSONObject("data");

        //데이터 사용 (칼럼명으로 접근)
        // optString 메서드의 주요 특징은 JSON 객체에 특정 키가 없을때, 기본값("")을 반환
        String name = userData.optString("name", ""); // "name" 키가 없으면,  빈 문자열 반환
        String profile_message = userData.optString("profile_message", ""); //null 일 수도 있음
        String profile_image = userData.optString("profile_image", "");
        String profile_background_image = userData.optString("profile_background_image", ""); //null 일 수도 있음

        updateUI(name, profile_message, profile_image, profile_background_image);
    }

    private void updateUI(String name, String profile_message, String profile_image,  String profile_background_image){
        nameView.setText(name);
        if(!profile_message.isEmpty()){
            messageView.setText(profile_message);
        }
        //글라이드로 프로필이미지 & 배경이미지 처리함
        ImageUtils.loadImage(profile_image, profileImage, ProfileActivity1.this);
        if(!profile_background_image.isEmpty()){
            ImageUtils.loadImage(profile_background_image, backImage, ProfileActivity1.this);
        }
    }

    private void handleFailureMessage(JSONObject jsonObject) throws JSONException {
        String message = jsonObject.getString("message");
        Log.e("Error", "데이터를 가져오는데 실패: " + message);
    }

    private void getNameAndMessage(){
        Call<ResponseBody> call = service.getMyModalData(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                getNameAndMessageResponseProcess(response);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void getNameAndMessageResponseProcess(Response<ResponseBody> response){
        if(response.isSuccessful()){
            try {
                handleSuccessfulResponseWithNameAndData(response);

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleSuccessfulResponseWithNameAndData (Response<ResponseBody> response) throws JSONException, IOException {
        // 서버로부터 응답 본문을 문자열로 변환
        String responseString = response.body().string();
        //문자열을 JSON객체로 변환
        JSONObject jsonObject = new JSONObject(responseString);

        if(jsonObject.getBoolean("success")){
            // 성공적으로 데이터를 가져온 경우
            handleNameAndData(jsonObject);
        }else {
            // 데이터를 가져오는데 실패한 경우
            handleFailureMessage(jsonObject);
        }
    }

    private void handleNameAndData(JSONObject jsonObject) throws JSONException {
        JSONObject userData = jsonObject.getJSONObject("data");

        //데이터 사용 (칼럼명으로 접근)
        // optString 메서드의 주요 특징은 JSON 객체에 특정 키가 없을때, 기본값("")을 반환
        String name = userData.optString("name", ""); // "name" 키가 없으면,  빈 문자열 반환
        String profile_message = userData.optString("profile_message", ""); //null 일 수도 있음

        nameView.setText(name);
        if(!profile_message.isEmpty()){
            messageView.setText(profile_message);
        }
    }


    // 1-1. 카메라 권한 요청
    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            // 권한이 이미 부여되었을 경우, 카메라 및 접근 로직 실행
            openCamera();
        }
    }

    // 1-2. 갤러리 권한 요청
    private void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            // 권한이 이미 부여되었을 경우, 카메라 및 저장소 접근 로직 실행
            if (currentAction == REQUEST_GALLERY) {
                openGallery();
            } else if (currentAction == REQUEST_GALLERY_BACK_IMG) {
                openGalleryForBackground();
            }
        }
    }

    // 2. 요청에 대한 사용자 응답을 처리하는 콜백 메서드
    // 콜백 메서드란?
    // 프로그래머가 아닌 시스템적으로 자동 호출되는 메서드 (ex. 사용자의 응답이 있을때 호출된다)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되었을 경우, 카메라 및 저장소 접근 로직 실행
                if (currentAction == REQUEST_CAMERA) {
                    openCamera();
                } else if (currentAction == REQUEST_GALLERY) {
                    openGallery();
                }
            } else {
                // 권한이 거부되었을 경우, 사용자에게 권한이 필요한 이유 설명
                if (currentAction == REQUEST_CAMERA) {
                    explainCameraPermissionReason();
                } else if (currentAction == REQUEST_GALLERY || currentAction == REQUEST_GALLERY_BACK_IMG) {
                    explainStoragePermissionReason();
                }

            }
        }
    }

    // 액티비티의 컨텍스트와 밀접한 관련이 있음
    // 1-1.카메라 열기
    // 카메라 인텐트를 생성하고, EXTRA_OUTPUT에 사진을 저장할 파일의 Uri를 전달
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(this); //파일 경로를 만듦
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.android.tiki_taka.fileprovider", //authorities 문자열이 AndroidManifest.xml 파일과 정확히 일치하는지 확인!
                        photoFile);
                // 로컬 파일 시스템에 있는 파일(photoFile)에 대한 Content Uri를 생성
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 1-2.갤러리 열기 (프로필)
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE);
    }

    // 1-3.갤러리 열기 (배경)
    private void openGalleryForBackground() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_BACKGROUND_IMAGE);
    }

    //2. 사진 선택 결과 받기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 카메라 앱은 사진을 imageUri 위치에 저장하고, data 인텐트에 추가 정보를 반환하지 않을 수 있습니다. 따라서 data != null 조건을 삭제함

            // Uri를 직접 사용하는 것이 파일 접근에 있어 더 현대적이고 안전한 방법
            String imageUriString = imageUri.toString();
            //db 업데이트
            updateProfileImage(imageUriString, userId);

            //imageUri (Uri 객체)나 imageUriString (Uri의 String 표현) 중 어느 것을 사용하여도 Glide는 올바르게 이미지를 로드가능
            ImageUtils.loadImage(imageUriString, profileImage, ProfileActivity1.this);

        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            String selectedImageUriString =selectedImageUri.toString();
            updateProfileImage(selectedImageUriString, userId);
            ImageUtils.loadImage(selectedImageUriString, profileImage, ProfileActivity1.this);


        } else if (requestCode == REQUEST_BACKGROUND_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            String selectedImageUriString =selectedImageUri.toString();
            updateProfileBackImage(selectedImageUriString, userId);
            ImageUtils.loadImage(selectedImageUriString, backImage, ProfileActivity1.this);
        }

    }

    // 권한이 필요하다는 설명 다이얼로그
    private void explainCameraPermissionReason() {
        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage("이 기능을 사용하기 위해서는 카메라 접근 권한이 필요합니다.")
                .setPositiveButton("권한 요청", (dialog, which) -> requestCameraPermissions())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void explainStoragePermissionReason() {
        new AlertDialog.Builder(this)
                .setTitle("권한 필요")
                .setMessage("이 기능을 사용하기 위해서는 저장소 접근 권한이 필요합니다.")
                .setPositiveButton("권한 요청", (dialog, which) -> requestStoragePermissions())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateProfileBackImage(String imageUriString, int userId){
        JSONObject jsonObject = createJsonRequestBody(imageUriString);
        RequestBody requestBody = createRequestBody(jsonObject);

        service.updateProfileBackImage(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                updateBackImgResponseProcess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private JSONObject createJsonRequestBody(String imageUriString){
        //JSON 객체를 생성해서 userId와 image를 같이 보내줌
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("image", imageUriString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    private RequestBody createRequestBody(JSONObject jsonObject){
        //JSON 객체를 문자열로 변환
        String jsonData = jsonObject.toString();
        //RequestBody 생성
         return RequestBody.create(MediaType.parse("application/json"), jsonData);
    }


    private void updateBackImgResponseProcess(Response<ResponseBody> response){
        if (response.isSuccessful()) {
            handleSuccessfulResponseToUpdateBackImg(response);

        } else {
            showToast("서버 응답 오류");
        }
    }

    private void handleSuccessfulResponseToUpdateBackImg(Response<ResponseBody> response){
        // http 요청 성공시
        try {
            String message = parseResponseData(response);
            showToast(message);

        } catch (JSONException | IOException e) {
            // JSON 파싱 오류 처리, IOException 처리
            e.printStackTrace();
            showToast("오류 발생");
        }
    }

    private String parseResponseData(Response<ResponseBody> response) throws JSONException, IOException {
        String responseJson = response.body().string();
        JSONObject jsonObject = new JSONObject(responseJson);
        return jsonObject.getString("message");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void updateProfileImage(String imageUriString, int userId){

        JSONObject jsonObject = createJsonRequestBody(imageUriString);
        RequestBody requestBody = createRequestBody(jsonObject);

        service.updateProfileImage(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                updateProfileImageResponseProcess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void updateProfileImageResponseProcess( Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                String message = parseResponseData(response);
                showToast(message);

            } catch (JSONException | IOException e) {
                // JSON 파싱 오류 처리 , IOException 처리
                e.printStackTrace();
                showToast("오류 발생");
            }

        } else {
           showToast("서버 응답 오류");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 뒤로가기 버튼 클릭 시의 동작
                onBackPressed(); // 이전 액티비티로 돌아가기
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSetting(){
        // 현재 앱의 패키지 이름을 가져옵니다.
        String packageName = getApplicationContext().getPackageName();

        // 설정 화면으로 이동하는 Intent 생성
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);

        // 안드로이드 버전별로 필요한 Intent 정보를 추가합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 안드로이드 8.0 (오레오) 이상의 경우
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 안드로이드 5.0 (롤리팝) 이상의 경우
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", getApplicationContext().getApplicationInfo().uid);
        }
        // 설정 화면으로 이동합니다.
        startActivity(intent);

    }


}