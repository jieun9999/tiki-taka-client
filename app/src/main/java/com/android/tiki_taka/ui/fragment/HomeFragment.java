package com.android.tiki_taka.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.dto.ChatRoom;
import com.android.tiki_taka.models.dto.HomeProfiles;
import com.android.tiki_taka.models.dto.PartnerDataManager;
import com.android.tiki_taka.models.dto.PartnerProfile;
import com.android.tiki_taka.models.dto.UserProfile;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.ui.activity.Profile.HomeActivity;
import com.android.tiki_taka.ui.activity.Profile.ProfileActivity1;
import com.android.tiki_taka.utils.TimeUtils;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    ProfileApiService service;
    //홈 화면 뷰들
    ImageView profile1;
    ImageView profile2;
    TextView loveDate;
    TextView name1;
    TextView name2;
    ImageView changeBackgroundBtn;
    int userId; // 유저 식별 정보
    ImageView backgroundImageView;
    private Uri imageUri; //카메라 앱이 전달받을 파일경로

    private int currentAction;
    private static final int PERMISSIONS_REQUEST_CODE = 100; // 권한 요청을 구별하기 위한 고유한 요청 코드, 런타임 권한 요청시 사용
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_GALLERY_IMAGE = 4;
    View dialogView; // 모달창 ui
    public static  ChatRoom chatRoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrofit 인스턴스 초기화나 쉐어드 프리퍼런스에서 데이터 읽기

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ProfileApiService.class);

        if(getActivity()!= null){
            userId = SharedPreferencesHelper.getUserId(getContext());
        }
    }

    //UI 요소를 초기화하고 뷰 관련 설정
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // onCreateView() 이후에 호출되는 메서드로, 뷰가 생성된 후에 이벤트 리스너를 설정
    // 뷰 요소에 대한 참조를 설정
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile1 = view.findViewById(R.id.imageView20);
        profile2 = view.findViewById(R.id.imageView22);
        loveDate = view.findViewById(R.id.textView14);
        name1 = view.findViewById(R.id.textView16);
        name2 = view.findViewById(R.id.textView17);
        changeBackgroundBtn = view.findViewById(R.id.imageView19);
        backgroundImageView = view.findViewById(R.id.backgroundImageView);

        //홈화면 데이터 불러오기
        getHomeProfile();

        // 여기서 이벤트 리스너 설정
        changeBackgroundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 이벤트 처리
                // getContext()를 사용하여 현재 프래그먼트의 Context를 얻습니다.
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.profile_image_bottom_sheet_dialog, null);
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
        profile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyModalDialog();
            }
        });

        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPtnModalDialog();
            }
        });
    }


    private void getHomeProfile(){
        // 1. 유저 프로필 정보 가져오기
        Call<HomeProfiles> call = service.getHomeProfile(userId);
        call.enqueue(new Callback<HomeProfiles>() {
            @Override
            public void onResponse(Call<HomeProfiles> call, Response<HomeProfiles> response) {
                processHomeProfileResponse(response);

            }

            @Override
            public void onFailure(Call<HomeProfiles> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processHomeProfileResponse(Response<HomeProfiles> response){
        if (response.isSuccessful() && response.body() != null) {
            //서버에서 홈프로필 정보를 가져옴
            HomeProfiles homeProfiles = response.body();
            // 유저 프로필 정보 처리
            UserProfile userProfile = homeProfiles.getUserProfile();
            // 파트너 프로필 정보 처리
            PartnerProfile partnerProfile = homeProfiles.getPartnerProfile();

            //파트너 아이디, 이미지 static 설정
            PartnerDataManager.setPartnerId(partnerProfile.getUserId());
            PartnerDataManager.setPartnerImg(partnerProfile.getProfileImage());

            updateViewWithUserProfile(userProfile);
            updateViewWithPartnerProfile(partnerProfile);

        } else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }
    private void updateViewWithUserProfile(UserProfile userProfile){
        //나의 프로필 교체
        String myName = userProfile.getName();
        name2.setText(myName);
        String profile2ImageUrl = userProfile.getProfileImage();
        Log.e("profile2ImageUrl", profile2ImageUrl);
        ImageUtils.loadImage(profile2ImageUrl,profile2, getContext());

        String firstDateStr = userProfile.getMeetingDay();
        //사귄 날짜부터 지난 일수 계산
        long daysTogether = TimeUtils.calculateDaysSince(firstDateStr);
        // 이 데이터를 홈 액티비티의 뷰에 설정하는 로직 구현

        if (daysTogether >= 0) {
            loveDate.setText(String.valueOf(daysTogether));
        } else {
            // 유효하지 않은 날짜 데이터 처리
            Log.e("Date Error", "유효하지 않은 날짜 데이터: " + firstDateStr);
        }
        //홈 배경 사진 교체
        String backImg = userProfile.getHomeBackgroundImage();
        if(backImg != null){
            ImageUtils.loadImage(backImg,backgroundImageView, getContext());
        }
    }

    private void updateViewWithPartnerProfile(PartnerProfile partnerProfile){
        //파트너 프로필 교체
        // 예: 이름, 프로필 사진을 홈 액티비티의 뷰에 설정
        String myName2 = partnerProfile.getName();
        name1.setText(myName2);
        String profile1ImageUrl = partnerProfile.getProfileImage();
        ImageUtils.loadImage(profile1ImageUrl,profile1, getContext());
    }


    // 1-1. 카메라 권한 요청
    public void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 프래그먼트에서 직접 권한 요청
            requestPermissions (new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            // 권한이 이미 부여되었을 경우, 카메라 및 접근 로직 실행
            openCamera();
        }
    }

    // 1-2. 갤러리 권한 요청
    public void requestStoragePermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 프래그먼트에서 직접 권한 요청
            requestPermissions (new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            // 권한이 이미 부여되었을 경우, 카메라 및 저장소 접근 로직 실행
            openGallery();
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
                } else if (currentAction == REQUEST_GALLERY) {
                    explainStoragePermissionReason();
                }

            }
        }
    }

    // 1-1.카메라 열기
    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(getContext()); //파일 경로를 만듦
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(getContext(),
                        "com.android.tiki_taka.fileprovider", //authorities 문자열이 AndroidManifest.xml 파일과 정확히 일치하는지 확인!
                        photoFile);
                // 로컬 파일 시스템에 있는 파일(photoFile)에 대한 Content Uri를 생성
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //or

    // 1-2.갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY_IMAGE);
    }


    //2. 사진 선택 결과 받기
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //요약하자면, 이미지 변경이 onActivityResult 메서드 내에서 이미 처리되었기 때문에,
        // 사용자가 다른 프래그먼트로 이동했다가 다시 돌아와도 변경된 이미지가 보여지는 것입니다.
        // onResume은 이 경우에 이미지 업데이트와는 직접적인 관련이 없습니다.

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ) {
            // 카메라 앱은 사진을 imageUri 위치에 저장하고, data 인텐트에 추가 정보를 반환하지 않을 수 있습니다. 따라서 data != null 조건을 삭제함
            // 카메라 앱에서 직접 반환받은 Bitmap 객체에는 원본 파일 경로 정보가 포함x
            // 카메라로 사진을 찍을 때 원본 파일 경로를 저장하려면, 사진을 찍기 전에 이미지 파일을 생성하고 이 파일의 경로를 카메라 앱에 전달해야 함

            //db 업데이트
            String imageUriString = imageUri.toString(); // URI를 String으로 변환한 값을 저장
            updateBackgroundImage(imageUriString, userId);

            //글라이드로 이미지를 ImageView에 표시
            ImageUtils.loadImage(imageUriString, backgroundImageView, getActivity());


        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            //db 업데이트
            String selectedImageUriString = selectedImageUri.toString(); // URI를 String으로 변환한 값을 저장
            updateBackgroundImage(selectedImageUriString, userId);

            //글라이드로 이미지를 ImageView에 표시
            ImageUtils.loadImage(selectedImageUriString, backgroundImageView, getActivity());
        }

    }


    // 권한이 필요하다는 설명 다이얼로그
    private void explainCameraPermissionReason() {
        new AlertDialog.Builder(getContext())
                .setTitle("권한 필요")
                .setMessage("이 기능을 사용하기 위해서는 카메라 접근 권한이 필요합니다.")
                .setPositiveButton("권한 요청", (dialog, which) -> requestCameraPermissions())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void explainStoragePermissionReason() {
        new AlertDialog.Builder(getContext())
                .setTitle("권한 필요")
                .setMessage("이 기능을 사용하기 위해서는 저장소 접근 권한이 필요합니다.")
                .setPositiveButton("권한 요청", (dialog, which) -> requestStoragePermissions())
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }


    private void updateBackgroundImage(String imageUrl, int userId){

        // Uri를 파일 경로로 변환
        Uri imageUri = Uri.parse(imageUrl);
        String realPath = ImageUtils.getRealPathFromUri(requireContext(), imageUri);
        // 파일 객체 생성
        File imageFile = new File(realPath);

        // RequestBody 생성 (이미지 파일)
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        // RequestBody 생성 (텍스트 데이터, userId)
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));

        service.updateBackgroundImage(userIdBody, imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                onResponseProcess(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private JSONObject createJsonObject(String imageUrl){
        //JSON 객체를 생성해서 userId와 image를 같이 보내줌
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("image", imageUrl);
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

    private void onResponseProcess(Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful() && response.body() != null) {
            // http 요청 성공시
            try {
                String responseJson = response.body().string();
                JSONObject jsonObject = new JSONObject(responseJson);
                String message = jsonObject.getString("message");

                //성공, 실패 모두 메세지를 내보냄
                showToast(message);

            } catch (JSONException | IOException e) {
                handleErrorMessage(e);
            }

        } else {
            showToast("서버 응답 오류: " + response.code());
        }
    }


    private void showToast(String message) {
        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void handleErrorMessage(Exception e){
        e.printStackTrace();
        Toast.makeText(getContext().getApplicationContext(), "오류 생성", Toast.LENGTH_LONG).show();
    }

    // 나의 모달창
    // 코드 정리: myModalUi 메서드 내에서 dialogView는 전역 변수처럼 보입니다.
    // 가능하면 전역 변수의 사용을 피하고 메서드의 매개변수를 통해 필요한 뷰나 컨텍스트를 전달하는 것이 좋습니다.
    private void showMyModalDialog() {

        myModalUi();
        getMyModalData(dialogView);
    }

    // 파트너의 모달창
    private void showPtnModalDialog(){

        ptnModalUi();
        getPtnModalData(dialogView);
    }


    private void myModalUi(){
        showModalUi(R.layout.modal_my_profile, true);
    }

    private void ptnModalUi() {
        showModalUi(R.layout.modal_ptnr_profile, false);
    }


    private void showModalUi(int layoutResourceId, boolean showUpdateButton){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(layoutResourceId, null);

        builder.setView(dialogView)
                .setCancelable(true); // 다이얼로그 바깥 영역 터치시 닫기 설정

        final AlertDialog dialog = builder.create();

        // XML 레이아웃에서 버튼 찾기
        ImageView closeButton = dialogView.findViewById(R.id.btnCloseModal);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 내정보 수정 버튼 처리
        if(showUpdateButton){
            // '내정보 수정' 버튼 을 누르면, 프로필 화면_1 로 이동
            ImageView updateButton = dialogView.findViewById(R.id.imageView21);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //기존 모달창 닫음
                    dialog.dismiss();

                    // Intent 생성 시 getActivity()를 호출하여 액티비티 컨텍스트를 전달
                    Intent intent = new Intent(getContext(), ProfileActivity1.class);
                    startActivity(intent);
                }
            });
        }

        dialog.show();

        // 다이얼로그 창 크기 조절
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());

            // dp를 픽셀로 변환
            float density = getResources().getDisplayMetrics().density;
            int widthPx = (int)(380 * density); // 380dp를 픽셀로 변환
            int heightPx = (int)(550 * density); // 550dp를 픽셀로 변환

            layoutParams.width = widthPx;
            layoutParams.height = heightPx;
            window.setAttributes(layoutParams);
        }
    }


    private void getMyModalData(View dialogView){
        Call<ResponseBody> call = service.getMyModalData(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    processMyModalDataResponse(response);

                } else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processMyModalDataResponse(Response<ResponseBody> response){
        try {
            // 서버로부터 응답 본문을 문자열로 변환
            String responseString = response.body().string();
            //문자열을 JSON객체로 변환
            JSONObject jsonObject = new JSONObject(responseString);

            if(jsonObject.getBoolean("success")){
                handleUserData(jsonObject);

            }else {
                handleErrorMessage(jsonObject);
            }

        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleUserData(JSONObject jsonObject) throws JSONException {
        // 성공적으로 데이터를 가져온 경우
        JSONObject userData = jsonObject.getJSONObject("data");
        updateViewWithUserData(userData);
    }

    private void handleErrorMessage(JSONObject jsonObject) throws JSONException {
        // 데이터를 가져오는데 실패한 경우
        String message = jsonObject.getString("message");
        // 실패 메시지 처리
        Log.e("Error", "데이터를 가져오는데 실패: " + message);
    }

    private void updateViewWithUserData(JSONObject userData){
        //데이터 사용 (칼럼명으로 접근)
        // optString 메서드의 주요 특징은 JSON 객체에 특정 키가 없을때, 기본값("")을 반환
        String name = userData.optString("name", ""); // "name" 키가 없으면,  빈 문자열 반환
        String birthday = userData.optString("birthday", "");
        String profile_message = userData.optString("profile_message", ""); //null 일 수도 있음
        String profile_image = userData.optString("profile_image", "");
        String profile_background_image = userData.optString("profile_background_image", ""); //null 일 수도 있음

        // 이제 이 데이터를 UI에 표시하거나 필요한 작업 수행
        TextView nameTextView = dialogView.findViewById(R.id.textView19);
        TextView birthdayTextView = dialogView.findViewById(R.id.textView20);
        TextView messageTextView = dialogView.findViewById(R.id.textView18);
        ImageView profileImageView = dialogView.findViewById(R.id.imageView17);
        ImageView profileBackImageView = dialogView.findViewById(R.id.backgroundImageView);

        nameTextView.setText(name);
        birthdayTextView.setText(birthday);
        if(!profile_message.isEmpty()){
            messageTextView.setText(profile_message);
        }
        //글라이드로 imageUriString 분기처리
        ImageUtils.loadImage(profile_image,profileImageView, getContext());
        if(!profile_background_image.isEmpty()){
            ImageUtils.loadImage(profile_background_image,profileBackImageView, getContext());
        }
    }

    private void getPtnModalData(View dialogView){

        Call<ResponseBody> call = service.getPtnrModalData(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                processPtnModalDataResponse(response);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void processPtnModalDataResponse(Response<ResponseBody> response){
        if(response.isSuccessful() && response.body() != null){
            try {
                // 서버로부터 응답 본문을 문자열로 변환
                String responseString = response.body().string();
                //문자열을 JSON객체로 변환
                JSONObject jsonObject = new JSONObject(responseString);

                if(jsonObject.getBoolean("success")) {
                    handleUserData(jsonObject);
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }

    }
}