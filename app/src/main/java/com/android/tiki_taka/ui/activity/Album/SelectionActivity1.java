package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.tiki_taka.R;

import java.util.ArrayList;

public class SelectionActivity1 extends AppCompatActivity {

    private static final int PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE = 1 ;

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

    private void launchPhotoPickerInMultiSelectedMode(){
        final int maxNumPhoto = 10;
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNumPhoto);
        startActivityForResult(intent, PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.e("SelectionActivity1", "Failed to pick photos");
            return;
        }

        // onActivityResult에서 선택된 사진(들)의 URI를 StoryWritingActivity1로 전달함
        if (requestCode == PHOTO_PICKER_MULTI_SELECT_REQUEST_CODE) {
            ArrayList<Uri> selectedUris = new ArrayList<>();
            ClipData clipData = data.getClipData();

            if(clipData != null){ // 여러 사진 선택
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri currentUri = clipData.getItemAt(i).getUri();
                    selectedUris.add(currentUri);
                }
            } else if (data.getData() != null) { // 단일 사진 선택
                selectedUris.add(data.getData());
            }

            Intent intent = new Intent(SelectionActivity1.this, StoryWritingActivity1.class);
            intent.putParcelableArrayListExtra("selectedUris", selectedUris);
            // 안드로이드 Intent 객체에 ArrayList를 추가
            startActivity(intent);
        }
    }
}