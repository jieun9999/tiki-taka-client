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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.adapters.thumbnailCheckAdapter;
import com.android.tiki_taka.listeners.ThumbnailUpdateListener;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.ImageUtils;
import com.android.tiki_taka.utils.NavigationHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Retrofit;


public class StoryWritingActivity2 extends AppCompatActivity implements ThumbnailUpdateListener {

    StoryApiService service;
    int userId;
    int folderId;
    Uri sourceUri; // 소스 이미지의 Uri
    Uri destinationUri; // 크롭된 이미지를 저장할 Uri
    ImageView imageViewToCrop;
    ArrayList<Uri> selectedUris;
    String newTitleText;
    String newLocationText;
    String croppedimageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing2);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        TextView cancelBtn = findViewById(R.id.textView33);
        TextView saveBtn = findViewById(R.id.textView34);

        cancelBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = folderWritingBundle();
                NavigationHelper.navigateToActivity(StoryWritingActivity2.this, StoryWritingActivity1.class, bundle);
            }
        });

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
            destinationUri = createUniqueDestinationUri();

            selectedUris = extras.getParcelableArrayList("selectedUris");
        }

        RecyclerView recyclerView = findViewById(R.id.checkCardRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(new thumbnailCheckAdapter(selectedUris, this, this));

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

            @Override
            public void afterTextChanged(Editable s) {
                newLocationText = s.toString();
                textViewLocationCount.setText(newLocationText.length() + "/30");
            }
        });

       imageViewToCrop.setOnClickListener( v -> UCorpSettings(sourceUri, destinationUri));

    }

    private Bundle folderWritingBundle(){
        Bundle bundle = new Bundle();
        // croppedThumbnailUri, storyTitle, location
        bundle.putString("croppedThumbnailUri", croppedimageUriString);
        bundle.putString("storyTitle", newTitleText);
        bundle.putString("location", newLocationText);
        return bundle;
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

}