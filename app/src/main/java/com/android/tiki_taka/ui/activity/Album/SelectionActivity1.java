package com.android.tiki_taka.ui.activity.Album;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tiki_taka.R;

import java.util.ArrayList;

public class SelectionActivity1 extends AppCompatActivity {

    private static final int PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE = 1 ;
    ArrayList<Uri> selectedUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection1);

        ImageView xBtn = findViewById(R.id.imageView40);
        xBtn.setOnClickListener(v -> dismissActivity());

        ImageView photoIcon = findViewById(R.id.imageView35);
        photoIcon.setOnClickListener(v -> launchPhotoPickerInMultiSelectedMode());

    }

    private void dismissActivity(){
        finish();
    }

    @SuppressLint("IntentReset")
    private void launchPhotoPickerInMultiSelectedMode(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // 초기화를 여기서 수행하면 사용자가 다시 이미지 선택을 시작할 때마다 리스트가 초기화됩니다.
            selectedUris.clear();

            ClipData clipData = data.getClipData();
            if (clipData != null) {
                // 여러 사진 선택
                if (clipData.getItemCount() > 10) {
                    showToast("사진은 10장까지 선택 가능합니다.");
                }
                else {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri currentUri = clipData.getItemAt(i).getUri();
                        selectedUris.add(currentUri);  //uri를 list에 담는다.
                    }
                }

            } else if (data.getData() != null) {
                // 단일 사진 선택
                selectedUris.add(data.getData());
            }

            if(!selectedUris.isEmpty()){
                Intent intent = new Intent(SelectionActivity1.this, StoryWritingActivity1.class);
                intent.putParcelableArrayListExtra("selectedUris", selectedUris);
                startActivity(intent);
            }

        } else {
            //이미지 미선택
            showToast("이미지를 선택하지 않았습니다.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}