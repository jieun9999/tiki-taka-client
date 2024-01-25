package com.android.tiki_taka.ui.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.HomeProfiles;
import com.android.tiki_taka.models.PartnerProfile;
import com.android.tiki_taka.models.UserProfile;
import com.android.tiki_taka.services.ApiService;
import com.android.tiki_taka.ui.activity.SigninActivity1;
import com.android.tiki_taka.ui.activity.SignupActivity3;
import com.android.tiki_taka.utils.DateUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.ValidatorSingleton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ApiService service;
    //홈 화면 뷰들
    ImageView profile1;
    ImageView profile2;
    TextView loveDate;
    TextView name1;
    TextView name2;
    ImageView changeBackgroundBtn;
    int userId; // 유저 식별 정보
    ImageView backgroundImageView;

    private int currentAction;
    private static final int PERMISSIONS_REQUEST_CODE = 100; // 권한 요청을 구별하기 위한 고유한 요청 코드, 런타임 권한 요청시 사용
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_GALLERY_IMAGE = 4;
    Uri selectedImageUri; //프로필 사진 uri


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    //  프래그먼트의 레이아웃을 인플레이트하고 초기화하는 데 사용
    // Retrofit 초기화와 SharedPreferences 사용은 이 메서드 내에서 수행
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ApiService.class);

        //Android 프래그먼트에서 getSharedPreferences 메서드를 사용하려 할 때, getSharedPreferences는 Context 객체의 메서드이기 때문에 직접적으로 사용할 수 없습니다.
        // 프래그먼트에서는 getActivity() 메서드를 통해 액티비티의 컨텍스트에 접근한 다음, 이 컨텍스트를 사용하여 getSharedPreferences를 호출해야 합니다.
        //쉐어드에서 userId 가져오기
        if(getActivity()!= null){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정
        }
        return rootView;
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


    }
    private void getHomeProfile(){
        // 1. 유저 프로필 정보 가져오기
        Call<HomeProfiles> call = service.getHomeProfile(userId);
        call.enqueue(new Callback<HomeProfiles>() {
            @Override
            public void onResponse(Call<HomeProfiles> call, Response<HomeProfiles> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //서버에서 홈프로필 정보를 가져옴
                    HomeProfiles homeProfiles = response.body();
                    // 유저 프로필 정보 처리
                    UserProfile userProfile = homeProfiles.getUserProfile();
                    // 파트너 프로필 정보 처리
                    PartnerProfile partnerProfile = homeProfiles.getPartnerProfile();

                    //나의 프로필 교체
                    String myName = userProfile.getName();
                    name2.setText(myName);
                    //UserProfile 객체에서 Base64 인토딩된 이미지 문자열을 가져옴
                    String base64Image = userProfile.getProfileImage();
                    // 로그를 찍습니다. "HomeActivity"는 로그 태그입니다.
                    Log.d("HomeActivity", "Base64 Image String: " + base64Image);
                    ValidatorSingleton.getInstance().updateImageViewWithProfileImage(base64Image, profile2);

                    String firstDateStr = userProfile.getMeetingDay();
                    //사귄 날짜부터 지난 일수 계산
                    long daysTogether = DateUtils.calculateDaysSince(firstDateStr);
                    // 이 데이터를 홈 액티비티의 뷰에 설정하는 로직 구현

                    if (daysTogether >= 0) {
                        loveDate.setText(String.valueOf(daysTogether));
                    } else {
                        // 유효하지 않은 날짜 데이터 처리
                        Log.e("Date Error", "유효하지 않은 날짜 데이터: " + firstDateStr);
                    }

                    //파트너 프로필 교체
                    // 예: 이름, 프로필 사진을 홈 액티비티의 뷰에 설정
                    String myName2 = partnerProfile.getName();
                    name1.setText(myName2);

                    //UserProfile 객체에서 Base64 인토딩된 이미지 문자열을 가져옴
                    String base64Image2 = partnerProfile.getProfileImage();
                    ValidatorSingleton.getInstance().updateImageViewWithProfileImage(base64Image2, profile1);

                } else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<HomeProfiles> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });


    }

    // 1-1. 카메라 권한 요청
    private void requestCameraPermissions() {
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
    private void requestStoragePermissions() {
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
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            // 액티비티 간 데이터를 전달시 사용하는 클래스
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // imageBitmap을 사용하여 사진을 표시하거나 저장
            backgroundImageView.setImageBitmap(imageBitmap);

            //사진을 찍으면 Bitmap이 나옴 => base64String으로 변환
            String base64String = getImageBase64(imageBitmap, null);
            //db 업데이트
            updateBackgroundImage(base64String, userId);


        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // URI로부터 Bitmap을 생성하고, 이를 backgroundImageView에 설정
            displayImageFromUri(selectedImageUri, backgroundImageView);

            // 이미지를 선택하면 Uri가 나옴 => base64String으로 변환
            String base64String = getImageBase64(null, selectedImageUri);
            //db 업데이트
            updateBackgroundImage(base64String, userId);
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

    //Uri를 가지고 비트맵으로 바꿔서, 이미지뷰를 교체하는 함수
    private void displayImageFromUri(Uri imageUri, ImageView imageView) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //이미지 서버 전송시 데이터 형태 갖추기
    //1. 이미지 URI를 Bitmap으로 변환한 다음,
    //2. 이를 byte[] 형태로 변환하여 서버에 전송
    //3. byte[]를 Base64 문자열로 인코딩
    private String convertImageUriToBase64(Uri imageUri) {
        try {
            // 이미지 URI에서 Bitmap을 생성
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);

            // Bitmap을 ByteArrayOutputStream으로 변환
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            // byte[]로 변환
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // byte[]를 Base64 문자열로 인코딩
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

 //찍은 사진의 Bitmap과 갤러리에서 선택한 이미지의 Uri를 모두 처리하여 Base64 문자열로 변환하는 통일적인 메서드 구현
     private String convertToBase64(Bitmap bitmap) {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
         byte[] imageBytes = byteArrayOutputStream.toByteArray();
         return Base64.encodeToString(imageBytes, Base64.DEFAULT);
     }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
    }
    private String getImageBase64(Bitmap bitmap, Uri uri) {
        try {
            Bitmap finalBitmap;
            if (bitmap != null) {
                finalBitmap = bitmap; //비트맵이 존재하면, 비트맵을 그대로 쓰고
            } else if (uri != null) {
                finalBitmap = getBitmapFromUri(uri); //uri가 존재하면, uri를 가지고 비트맵으로 변환함
            } else {
                return null;
            }
            return convertToBase64(finalBitmap); // 공통적으로 추출한 비트맵을 가지고 base64String으로 변환함
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Base64String을 가지고 db에 업데이트하는 call 주고받기
    private void updateBackgroundImage(String imageBase64, int userId){

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

        service.updateBackgroundImage(body).enqueue(new Callback<ResponseBody>() {
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
                            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        } else {
                            // 저장 실패
                            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON 파싱 오류 처리
                        e.printStackTrace();
                        Toast.makeText(getContext().getApplicationContext(), "JSON 파싱 오류", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        // IOException 처리
                        e.printStackTrace();
                        Toast.makeText(getContext().getApplicationContext(), "IO 오류", Toast.LENGTH_LONG).show();
                    }

                } else {
                    //서버 응답 오류
                    Toast.makeText(getContext().getApplicationContext(), "서버 응답 오류: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 요청 실패 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

}