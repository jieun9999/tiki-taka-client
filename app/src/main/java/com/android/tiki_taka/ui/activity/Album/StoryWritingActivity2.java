package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.ThumbnailCheckAdapter;
import com.android.tiki_taka.listeners.ThumbnailUpdateListener;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.UriUtils;
import com.android.tiki_taka.utils.VideoUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;


public class StoryWritingActivity2 extends AppCompatActivity implements ThumbnailUpdateListener {

    int folderId;
    Uri sourceUri; // 소스 이미지의 Uri
    Uri destinationUri; // 크롭된 이미지를 저장할 Uri
    ImageView imageViewToCrop;
    ArrayList<Uri> selectedUris;
    String newTitleText;
    String newLocationText;
    String uncroppedimageUriString;
    String croppedimageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        setupCancelAndSaveBtnListener();
        loadFolderThumbnail();
        setRecyclerView();
        countTitleAndLocationTexts();
        setupThumbnailCropListener();

    }
    private void setupCancelAndSaveBtnListener(){
        TextView cancelBtn = findViewById(R.id.textView33);
        TextView saveBtn = findViewById(R.id.textView34);

        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(croppedimageUriString)){
                    cropAlertDialog();

                }else {
                    Intent resultIntent = new Intent();
                    Bundle bundle = writtenFolderWritingBundle();
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private void cropAlertDialog(){
        // 크롭되지 않았다면 사용자에게 알림창 표시
        AlertDialog.Builder builder = new AlertDialog.Builder(StoryWritingActivity2.this);
        builder.setMessage("사진을 크롭하세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadFolderThumbnail(){
        imageViewToCrop = findViewById(R.id.cropped_image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            folderId = extras.getInt("folderId");
            uncroppedimageUriString = extras.getString("thumbnailUri");

            // 받아온 uncroppedimageUriString가 이미지 형식 or 동영상 형식 인지 구분하여 초기 이미지 로드하기
            sourceUri = Uri.parse(uncroppedimageUriString);
            if (UriUtils.isVideoUri(sourceUri, this)) {
                VideoUtils.loadVideoThumbnail(this, sourceUri, imageViewToCrop);

            } else if (UriUtils.isImageUri(sourceUri, this)) {
                ImageUtils.loadImage(uncroppedimageUriString, imageViewToCrop, this);
            }
            destinationUri = createUniqueDestinationUri();
            selectedUris = extras.getParcelableArrayList("selectedUris");
        }
    }

    private void UCorpSettings(Uri sourceUri, Uri destinationUri){
        int maxWidthPx = dpToPx(412); // 412dp를 픽셀로 변환
        int maxHeightPx = dpToPx(200); // 200dp를 픽셀로 변환

     UCrop.of(sourceUri, destinationUri)
               .withAspectRatio(412, 200) // 원하는 비율로 설정
               .withMaxResultSize(maxWidthPx, maxHeightPx) // 최대 결과 이미지 크기
               .start(this);
    }

    public int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.checkCardRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new ThumbnailCheckAdapter(selectedUris, this, this));
    }

    private void countTitleAndLocationTexts(){
        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
        TextView textViewStoryTitleCount = findViewById(R.id.textViewStoryTitleCount);
        editTextStoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                newTitleText = s.toString();
                textViewStoryTitleCount.setText(newTitleText.length() + "/50");
            }

        });

        EditText editTextLocation = findViewById(R.id.editTextLocation);
        TextView textViewLocationCount = findViewById(R.id.textViewLocationCount);
        editTextLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                newLocationText = s.toString();
                textViewLocationCount.setText(newLocationText.length() + "/30");
            }
        });
    }

    private void setupThumbnailCropListener(){
        if (UriUtils.isVideoUri(sourceUri, this)) {
            sourceUri = VideoUtils.getThumbNailUri(this, sourceUri);
            imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));

        } else if (UriUtils.isImageUri(sourceUri, this)) {
            imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            Uri resultUri = UCrop.getOutput(data);
            croppedimageUriString = resultUri.toString();
            ImageUtils.loadImage(croppedimageUriString, imageViewToCrop, this);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("UCropError", "Crop error: ", cropError);
        }
    }

    @Override
    public void onUpdateThumbnail(Uri uri) {
        // 썸네일뷰 업데이트 로직
        ImageUtils.loadImage(String.valueOf(uri), imageViewToCrop, this);
        sourceUri = uri; // sourceUri 업데이트

        destinationUri = createUniqueDestinationUri();
        // 새로운 destinationUri 생성
        imageViewToCrop.setOnClickListener(v -> UCorpSettings(sourceUri, destinationUri));
        // imageViewToCrop의 클릭 리스너를 업데이트된 sourceUri를 사용하여 재설정
    }

    private Uri createUniqueDestinationUri() {
        String fileName = "cropped_" + System.currentTimeMillis() + ".jpg";
        File outFile = new File(getExternalFilesDir(null), fileName);
        return Uri.fromFile(outFile);
        // 만약 destinationUri가 크롭 작업마다 동일하게 설정된다면, 이전의 크롭 결과를 덮어쓰게 되어 항상 같은 resultUri를 얻게 될 수 있습니다.
    }

    private Bundle writtenFolderWritingBundle(){
        Bundle bundle = new Bundle();
        // croppedThumbnailUri, storyTitle, location
        bundle.putString("croppedThumbnailUri", croppedimageUriString);
        bundle.putString("storyTitle", newTitleText);
        bundle.putString("location", newLocationText);
        return bundle;
    }

}