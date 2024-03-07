package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.request.StoryCardRequest;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.utils.InitializeStack;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoryWritingActivity4 extends AppCompatActivity {
    StoryApiService service;
    int userId;
    int folderId;
    boolean isExistingFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing4);

        setupNetworkAndRetrieveId();
        extractIntentData();
        locationEditTextGone();
        displayCurrentDateOnToolbar();
        setupUIListeners();

    }

    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
    }

    private void displayCurrentDateOnToolbar() {
        String currentDate = getIntent().getStringExtra("currentDate");
        TextView toolbarDateView = findViewById(R.id.textView35);
        toolbarDateView.setText(currentDate);
    }

    private void extractIntentData(){
        folderId =  getIntent().getIntExtra("id", -1);
        isExistingFolder = getIntent().getBooleanExtra("isExistingFolder", false);
    }

    private void locationEditTextGone(){
        if(isExistingFolder){
            // 기존 폴더가 있거나, 기존 메모 내용을 편집할 때
            EditText locationEditText = findViewById(R.id.locationEditText);
            locationEditText.setVisibility(View.GONE);
        }
    }

    private void setupUIListeners(){
        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener( v -> finish());
        TextView uploadBtn = findViewById(R.id.textView34);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExistingFolder){
                    StoryCardRequest cardRequest = createStoryCardRequestFromInput(folderId);
                    uploadMemoToServer(cardRequest);
                }else {
                    StoryCardRequest cardRequest = createStoryCardRequestFromInput();
                    uploadMemoToServer(cardRequest);
                }
            }
        });
    }

    private StoryCardRequest createStoryCardRequestFromInput(){
        EditText noteEditTextView = findViewById(R.id.memoEditText);
        String memoText = noteEditTextView.getText().toString();
        String title = makeTitleFromTexts(memoText);
        EditText locationView = findViewById(R.id.locationEditText);
        String location = locationView.getText().toString();
        StoryCardRequest cardRequest = new StoryCardRequest(userId, memoText,title, location);
        return cardRequest;
    }

    private StoryCardRequest createStoryCardRequestFromInput(int folderId){
        EditText noteEditTextView = findViewById(R.id.memoEditText);
        String memoText = noteEditTextView.getText().toString();
        StoryCardRequest cardRequest = new StoryCardRequest(userId, folderId, memoText);
        return cardRequest;
    }

    private String makeTitleFromTexts(String memoText){
        String title;
        if (memoText.length() > 10) {
            title = memoText.substring(0, 10);
        } else {
            title = memoText;
        }
        return title;
    }
    private void uploadMemoToServer(StoryCardRequest cardRequest){
        service.saveMemoStoryCard(cardRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void ProcessInsertingCardsResponse( Response<ResponseBody> response){
        if(response.isSuccessful()){
            handleResponse(response);

        } else {
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleResponse(Response<ResponseBody> response){
        if (response.isSuccessful()) {
            try {
                String message = parseResponseData(response);
                Log.d("success",message);
                InitializeStack.navigateToAlbumFragment(this);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                Log.e("Error","catch문 오류 발생");
            }
        } else {
            Log.e("Error","서버 응답 오류");
        }
    }

    private String parseResponseData(Response<ResponseBody> response) throws JSONException, IOException {
        String responseJson = response.body().string();
        JSONObject jsonObject = new JSONObject(responseJson);
        return jsonObject.getString("message");
    }
}