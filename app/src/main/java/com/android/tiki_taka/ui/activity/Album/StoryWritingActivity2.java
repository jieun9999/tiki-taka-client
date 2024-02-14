package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.adapters.thumbnailCheckAdapter;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Retrofit;


public class StoryWritingActivity2 extends AppCompatActivity {

    StoryApiService service;
    int userId;
    int folderId;
    Uri sourceUri; // 소스 이미지의 Uri
    Uri destinationUri; // 크롭된 이미지를 저장할 Uri
    ImageView imageViewToCrop;
    ArrayList<Uri> selectedUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        imageViewToCrop = findViewById(R.id.cropped_image);
        TextView dateView = findViewById(R.id.textView26);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            folderId = extras.getInt("folderId");
            String thumbnailUriString = extras.getString("thumbnailUri");
            String dateText = extras.getString("date");
            ImageUtils.loadImage(thumbnailUriString, imageViewToCrop, this);
            dateView.setText(dateText);

            sourceUri = Uri.parse(thumbnailUriString);
            destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped")); //임시 저장 경로 설정

            selectedUris = extras.getParcelableArrayList("selectedUris");
        }

        RecyclerView recyclerView = findViewById(R.id.checkCardRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new thumbnailCheckAdapter(selectedUris, this));

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
        imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            final Uri resultUri = UCrop.getOutput(data);
            String imageUriString = resultUri.toString();
            ImageUtils.loadImage(imageUriString, imageViewToCrop, this);

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.e("UCropError", "Crop error: ", cropError);
        }
    }


}