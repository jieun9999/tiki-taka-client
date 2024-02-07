package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.android.tiki_taka.utils.ImageSingleton;
import com.android.tiki_taka.utils.RetrofitClient;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

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
    String imgAbsolutePath;

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

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

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
                        // 사진 찍기 로직
                        currentAction = REQUEST_CAMERA;
                        requestCameraPermissions();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetView.findViewById(R.id.choose_from_gallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 앨범에서 선택하기 로직
                        currentAction = REQUEST_GALLERY;
                        requestStoragePermissions();
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.show();
            }
        });

        // 배경 사진 변경
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 앨범에서 선택하기 로직
                currentAction = REQUEST_GALLERY_BACK_IMG;
                requestStoragePermissions();
            }
        });

        //이름 변경 액티비티로 이동
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity1.this, ProfileActivity4.class);
                startActivity(intent);
            }
        });

        //상메 변경 액티비티로 이동
        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity1.this, ProfileActivity5.class);
                startActivity(intent);
            }
        });



        listView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.profile_list_item, R.id.text_view_item, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // "로그아웃" 옵션을 클릭했을 때

                if (options[position].equals("로그아웃")) {

                    // 쉐어드에 자동로그인 비활성화 상태저장
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAutoLoginEnabled", false);
                    editor.apply();

                    // 로그인_1 화면으로 이동하면서 스택 초기화
                    Intent intent = new Intent(getApplicationContext(), SigninActivity1.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료


                } else if (options[position].equals("비밀번호 변경하기")) {

                    // 로그인_2 화면으로 이동하면서 스택 초기화
                    Intent intent = new Intent(ProfileActivity1.this, SigninActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료

                } else if (options[position].equals("알림 동의 설정")) {

                    // 앱의 알림 설정 화면으로 이동
                    goToSetting();

                } else if (options[position].equals("상대방과 연결끊기")) {
                    Intent intent = new Intent(ProfileActivity1.this, ProfileActivity3.class);
                    startActivity(intent);

                } else if (options[position].equals("회원 탈퇴")){
                    Intent intent = new Intent(ProfileActivity1.this, ProfileActivity2.class);
                    startActivity(intent);
                }
            }
        });
    }

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
                    if(response.isSuccessful()){
                        try {
                            // 서버로부터 응답 본문을 문자열로 변환
                            String responseString = response.body().string();
                            //문자열을 JSON객체로 변환
                            JSONObject jsonObject = new JSONObject(responseString);

                            if(jsonObject.getBoolean("success")){
                                // 성공적으로 데이터를 가져온 경우
                                JSONObject userData = jsonObject.getJSONObject("data");

                                //데이터 사용 (칼럼명으로 접근)
                                // optString 메서드의 주요 특징은 JSON 객체에 특정 키가 없을때, 기본값("")을 반환
                                String name = userData.optString("name", ""); // "name" 키가 없으면,  빈 문자열 반환
                                String profile_message = userData.optString("profile_message", ""); //null 일 수도 있음
                                String profile_image = userData.optString("profile_image", "");
                                String profile_background_image = userData.optString("profile_background_image", ""); //null 일 수도 있음

                                nameView.setText(name);
                                if(!profile_message.isEmpty()){
                                    messageView.setText(profile_message);
                                }
                                //글라이드로 profile과 배경이미지 처리함
                                Glide.with(getApplicationContext())
                                        .load(profile_image)
                                        .into(profileImage);
                                if(!profile_background_image.isEmpty()){
                                    Glide.with(getApplicationContext())
                                            .load(profile_background_image)
                                            .into(backImage);
                                }
                            }else {
                                // 데이터를 가져오는데 실패한 경우
                                String message = jsonObject.getString("message");
                                // 실패 메시지 처리
                                Log.e("Error", "데이터를 가져오는데 실패: " + message);
                            }

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        // 응답 실패
                        Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                    }

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //네트워크 오류 처리
                    Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                }
            });
        }

    private void getNameAndMessage(){
        Call<ResponseBody> call = service.getMyModalData(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        // 서버로부터 응답 본문을 문자열로 변환
                        String responseString = response.body().string();
                        //문자열을 JSON객체로 변환
                        JSONObject jsonObject = new JSONObject(responseString);

                        if(jsonObject.getBoolean("success")){
                            // 성공적으로 데이터를 가져온 경우
                            JSONObject userData = jsonObject.getJSONObject("data");

                            //데이터 사용 (칼럼명으로 접근)
                            // optString 메서드의 주요 특징은 JSON 객체에 특정 키가 없을때, 기본값("")을 반환
                            String name = userData.optString("name", ""); // "name" 키가 없으면,  빈 문자열 반환
                            String profile_message = userData.optString("profile_message", ""); //null 일 수도 있음

                            nameView.setText(name);
                            if(!profile_message.isEmpty()){
                                messageView.setText(profile_message);
                            }
                        }else {
                            // 데이터를 가져오는데 실패한 경우
                            String message = jsonObject.getString("message");
                            // 실패 메시지 처리
                            Log.e("Error", "데이터를 가져오는데 실패: " + message);
                        }

                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // 응답 실패
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
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

    // 1-1.카메라 열기
    // 카메라 인텐트를 생성하고, EXTRA_OUTPUT에 사진을 저장할 파일의 Uri를 전달
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(); //파일 경로를 만듦
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) { // 카메라 앱은 사진을 imageUri 위치에 저장하고, data 인텐트에 추가 정보를 반환하지 않을 수 있습니다. 따라서 data != null 조건을 삭제함
            // 카메라 앱에서 직접 반환받은 Bitmap 객체에는 원본 파일 경로 정보가 포함x
            // 카메라로 사진을 찍을 때 원본 파일 경로를 저장하려면, 사진을 찍기 전에 이미지 파일을 생성하고 이 파일의 경로를 카메라 앱에 전달해야 함

            //db 업데이트
            String imageUriString = imageUri.toString(); // URI를 String으로 변환한 값을 저장
            updateProfileImage(imageUriString, userId);

            //글라이드로 이미지를 ImageView에 표시
            //Uri로 로드
            Glide.with(this)
                    .load(imageUri)
                    .into(profileImage);


        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Uri를 통해 파일 경로 추출
            String imagePath = getPathFromUri(selectedImageUri);
            //db 업데이트
            updateProfileImage(imagePath, userId);
            //글라이드로 이미지를 ImageView에 표시
            Glide.with(this)
                    .load(imagePath)
                    .into(profileImage);


        } else if (requestCode == REQUEST_BACKGROUND_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Uri를 통해 파일 경로 추출
            String imagePath = getPathFromUri(selectedImageUri);
            //db 업데이트
            updateProfileBackImage(imagePath, userId);
            //글라이드로 이미지를 ImageView에 표시
            Glide.with(this)
                    .load(imagePath)
                    .into(backImage);
        }

    }

    //사진을 저장할 파일 생성, 이 파일 경로는 나중에 사진의 경로로 사용됨
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // 파일의 절대 경로를 로그로 출력
        Log.d("ImagePath", "File path: " + image.getAbsolutePath());
        imgAbsolutePath = image.getAbsolutePath();         // 파일: 경로를 문자열로 저장


        return image;
    }

    // Uri를 실제 파일 경로로 변환하는 메소드
    public String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
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

    // Base64String을 가지고 db에 업데이트하는 call 주고받기
    private void updateProfileBackImage(String imageBase64, int userId){

        //JSON 객체를 생성해서 userId와 image를 같이 보내줌
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("image", imageBase64);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //JSON 객체를 문자열로 변환
        String jsonData = jsonObject.toString();
        //RequestBody 생성
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        service.updateProfileBackImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 요청 성공 처리
                if (response.isSuccessful()) {
                    // http 요청 성공시
                    try {
                        String responseJson = response.body().string();
                        //response.body().string() 메서드를 사용하여 ResponseBody를 문자열로 읽어오는 것
                        //.toString() 과 다름
                        JSONObject jsonObject = new JSONObject(responseJson);
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            // 저장 성공
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else {
                            // 저장 실패
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // JSON 파싱 오류 처리
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "JSON 파싱 오류", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // IOException 처리
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "IO 오류", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //서버 응답 오류
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void updateProfileImage(String imageUrl, int userId){

        //JSON 객체를 생성해서 userId와 image를 같이 보내줌
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("image", imageUrl);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //JSON 객체를 문자열로 변환
        String jsonData = jsonObject.toString();
        //RequestBody 생성
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonData);
        service.updateProfileImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 요청 성공 처리
                if (response.isSuccessful()) {
                    // http 요청 성공시
                    try {
                        String responseJson = response.body().string();
                        //response.body().string() 메서드를 사용하여 ResponseBody를 문자열로 읽어오는 것
                        //.toString() 과 다름
                        JSONObject jsonObject = new JSONObject(responseJson);
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        if (success) {
                            // 저장 성공
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else {
                            // 저장 실패
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // JSON 파싱 오류 처리
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "JSON 파싱 오류", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // IOException 처리
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "IO 오류", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //서버 응답 오류
                    Toast.makeText(getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
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