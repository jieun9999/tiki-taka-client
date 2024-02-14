package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.yalantis.ucrop.UCrop;


public class StoryWritingActivity2 extends AppCompatActivity {
//    Uri sourceUri = ...; // 소스 이미지의 Uri
//    Uri destinationUri = ...; // 크롭된 이미지를 저장할 Uri

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        EditText editTextStoryTitle = findViewById(R.id.editTextStoryTitle);
        TextView textViewStoryTitleCount = findViewById(R.id.textViewStoryTitleCount);

        editTextStoryTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                textViewStoryTitleCount.setText(text.length() + "/50");
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

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                textViewLocationCount.setText(text.length() + "/30");
            }
        });


//        UCrop.of(sourceUri, destinationUri)
//                .withAspectRatio(16, 9) // 원하는 비율로 설정
//                .withMaxResultSize(1024, 1024) // 최대 결과 이미지 크기
//                .start(this);

    }
}