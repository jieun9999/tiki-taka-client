package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.IntentHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectionActivity2 extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri imageUri; //카메라 앱이 전달받을 파일경로
    ArrayList<Uri> selectedUris = new ArrayList<>();
    int folderId; // 특정 폴더

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection2);

        folderId = IntentHelper.getId(this);

        //카메라
        ImageView cameraIcon = findViewById(R.id.imageView30);
        cameraIcon.setOnClickListener(v -> openCamera());

        ImageView xBtn = findViewById(R.id.imageView40);
        xBtn.setOnClickListener(v -> dismissActivity());
    }

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

    private void dismissActivity() {
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            selectedUris.clear();
            selectedUris.add(imageUri);

            boolean isExistingFolder = true;

            Intent intent = new Intent(SelectionActivity2.this, StoryWritingActivity1.class);
            intent.putExtra("id", folderId);
            intent.putParcelableArrayListExtra("selectedUris", selectedUris);
            intent.putExtra("isExistingFolder", isExistingFolder);
            startActivity(intent);
        }
    }
}