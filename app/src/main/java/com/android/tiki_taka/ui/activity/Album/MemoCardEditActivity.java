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
import com.android.tiki_taka.utils.IntentHelper;
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

public class MemoCardEditActivity extends AppCompatActivity {
    StoryApiService service;
    int userId;
    int cardId;
    String EditingMemoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_card_edit);
        setupNetworkAndRetrieveId();
        extractIntentAndSetText();
        setupUIListeners();

    }
    private void setupNetworkAndRetrieveId(){
        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);
    }

    private void extractIntentAndSetText(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cardId = extras.getInt("cardId");
            EditingMemoText = extras.getString("memoText");
            EditText memoEditText = findViewById(R.id.memoEditText);
            memoEditText.setText(EditingMemoText);
        }
    }

    private void setupUIListeners(){
        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener( v -> finish());
        TextView editBtn = findViewById(R.id.textView34);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoryCardRequest cardRequest = createStoryCardRequestFromInput(cardId);
                updateMemoStoryCardToServer(cardRequest);
            }
        });
    }

    private StoryCardRequest createStoryCardRequestFromInput(int cardId){
        EditText noteEditTextView = findViewById(R.id.memoEditText);
        String memoText = noteEditTextView.getText().toString();
        StoryCardRequest cardRequest = new StoryCardRequest(cardId, memoText, SharedPreferencesHelper.getPartnerId(this), userId);
        return cardRequest;
    }

    private void updateMemoStoryCardToServer(StoryCardRequest cardRequest){
        service.updateMemoStoryCard(cardRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });
    }

    private void ProcessInsertingCardsResponse( Response<ResponseBody> response){
        if(response.isSuccessful()){
            handleResponse(response);

        } else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void handleResponse(Response<ResponseBody> response){
        try {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            boolean isSuccess = jsonObject.getBoolean("success");

            if (isSuccess) {
                Log.d("Success", "업데이트 성공");
                IntentHelper.setResultAndFinish(this, RESULT_OK);
            } else {
                Log.e("Error", "서버로부터의 응답이 success: false임");

            }
        } catch (IOException e) {
            Log.e("Error", "응답 바디 읽기 오류: " + e.getMessage());

        } catch (JSONException e) {
            Log.e("Error", "JSON 파싱 오류: " + e.getMessage());

        }
    }


}