package com.android.tiki_taka.ui.activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.Manifest;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.UserProfile;
import com.android.tiki_taka.services.ApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity3 extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100; // 권한 요청을 구별하기 위한 고유한 요청 코드, 런타임 권한 요청시 사용
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_GALLERY_IMAGE = 4;
    private int currentAction;


    //프로필 항목
    CircleImageView profileImage;
    RadioGroup radioGroupGender;
    TextInputEditText editTextName;
    TextInputLayout textInputLayout;
    TextInputLayout textInputLayout2;
    TextInputLayout textInputLayout3;
    TextInputEditText editTextDate;
    TextInputEditText editTextDate2;
    CheckBox checkBoxTerms;
    CheckBox checkBoxPrivacy;
    ImageView startButton;
    private boolean isProfileImageChanged;
    Uri selectedImageUri; //프로필 사진 uri (갤러리)
    Bitmap selectedPhotoBitmap; //프로필 사진 비트맵 (카메라)
    private boolean isValidInput = false; // 전역 변수 선언
    UserProfile userProfile;
    ApiService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        RadioButton radioButton = findViewById(R.id.radio_female);
        radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
        RadioButton radioButton2 = findViewById(R.id.radio_male);
        radioButton2.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));

        profileImage = findViewById(R.id.imageView11);
        profileImage.setImageResource(R.drawable.group_49);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        editTextName = findViewById(R.id.이름);
        editTextDate = findViewById(R.id.생일);
        editTextDate2 = findViewById(R.id.처음사귄날);
        checkBoxTerms = findViewById(R.id.checkbox_agree_terms);
        checkBoxPrivacy = findViewById(R.id.checkbox_agree_terms2);
        startButton = findViewById(R.id.imageView12);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SignupActivity3.this);
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

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickDialog(editTextDate);
            }
        });

        editTextDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickDialog(editTextDate2);
            }
        });




        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ApiService.class);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
                if (isValidInput) {
                    // 유효한 입력 처리 로직
                    // 유저 데이터 모아서 객체 생성
                    userProfile = collectUserData();

                    //서버에게 http 요청보내기
                    Call<ResponseBody> call = service.saveUserProfile(userProfile);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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

                                        //로그인 화면으로 이동
                                        Intent intent = new Intent(SignupActivity3.this, SigninActivity1.class);
                                        startActivity(intent);

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
                            // 네트워크 오류 처리
                            Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    // 입력이 유효하지 않을 때의 처리 로직
                    Toast.makeText(getApplicationContext(), "입력이 유효하지 않음", Toast.LENGTH_LONG).show();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            // 액티비티 간 데이터를 전달시 사용하는 클래스
            selectedPhotoBitmap = (Bitmap) extras.get("data");
            // imageBitmap을 사용하여 사진을 표시하거나 저장
            profileImage.setImageBitmap(selectedPhotoBitmap);
            isProfileImageChanged = true;


        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // URI로부터 Bitmap을 생성하고, 이를 ImageView에 설정
            displayImageFromUri(selectedImageUri, profileImage);
            isProfileImageChanged = true;


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


    //Uri를 가지고 비트맵으로 바꿔서, 이미지뷰를 교체하는 함수
    private void displayImageFromUri(Uri imageUri, ImageView imageView) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // datePickDialog 생성 함수
    private void datePickDialog(TextInputEditText editTextDate) {
        // 현재 날짜를 기본값으로 설정
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity3.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 사용자가 선택한 날짜로 EditText 업데이트
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        editTextDate.setText(selectedDate);
                    }
                }, year, month, day);

        // DatePickerDialog 표시
        datePickerDialog.show();
    }

    // 사용자가 모든 항목을 기입하였는지 확인하는 함수
    private void validateInputs() {
        //프로필 체크
        if (!isProfileImageChanged) {
            Toast.makeText(this, "프로필 사진을 입력해주세요.", Toast.LENGTH_SHORT).show();
            isValidInput = false;
        }

        //성별 체크
        if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
            isValidInput = false;
        }

        //이름 체크
        if (TextUtils.isEmpty(editTextName.getText())) {
            textInputLayout.setError("이름을 입력해주세요.");
            isValidInput = false;
        }

        //생일 체크
        if (TextUtils.isEmpty(editTextDate.getText())) {
            textInputLayout2.setError("생일을 입력해주세요.");
            isValidInput = false;
        }

        //처음만난 날 체크
        if (TextUtils.isEmpty(editTextDate2.getText())) {
            textInputLayout3.setError("처음 만난 날을 입력해주세요.");
            isValidInput = false;
        }

        //약관 동의 체크
        if (!checkBoxTerms.isChecked() || !checkBoxPrivacy.isChecked()) {
            Toast.makeText(this, "모든 약관에 동의해주세요.", Toast.LENGTH_SHORT).show();
            isValidInput = false;
        }

        //모든 조건이 만족되었을 경우
        isValidInput = true;

    }

    //이미지 서버 전송시 데이터 형태 갖추기 (갤러리 편)
    //1. 이미지 URI를 Bitmap으로 변환한 다음,
    //2. 이를 byte[] 형태로 변환
    //3. byte[]를 Base64 문자열로 인코딩 후 서버 전송
    private String convertImageUriToBase64(Uri imageUri) {
        try {
            // 이미지 URI에서 Bitmap을 생성
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

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

    //이미지 서버 전송시 데이터 형태 갖추기 (카메라 편)
    //1. 찍은 사진의 Bitmap을 byte[] 형태로 변환
    //2. byte[]를 Base64 문자열로 인코딩 후 서버 전송

    private String convertToBase64(Bitmap bitmap) {
        // ByteArrayOutputStream을 사용하여 Bitmap을 byte array로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Bitmap을 JPEG 형식으로 압축. 두 번째 파라미터는 품질 설정 (0-100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // ByteArrayOutputStream을 byte array로 변환
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // byte array를 Base64 문자열로 인코딩
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }




    //사용자 입력값을 가져와서 객체 생성
    private UserProfile collectUserData() {

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        int Id = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

        //카메라로 사진을 찍었을 때와 갤러리에서 이미지를 선택했을 때를 구분
        String profileImage = "";
        if (isProfileImageChanged) {
            if (selectedImageUri != null) {
                // 갤러리에서 선택된 이미지의 Uri를 Base64 문자열로 변환
                profileImage = convertImageUriToBase64(selectedImageUri);
            } else {
                // 카메라로 찍은 사진의 Bitmap을 Base64 문자열로 변환
                profileImage = convertToBase64(selectedPhotoBitmap);
            }
        }

        String gender = ((RadioButton)findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        String name = editTextName.getText().toString();
        String birthday = editTextDate.getText().toString();
        String meetingDay = editTextDate2.getText().toString();
        boolean agreeTerms = checkBoxTerms.isChecked();
        boolean agreePrivacy = checkBoxPrivacy.isChecked();

        return new UserProfile(Id, profileImage, gender, name, birthday, meetingDay, agreeTerms, agreePrivacy);
    }
    }


