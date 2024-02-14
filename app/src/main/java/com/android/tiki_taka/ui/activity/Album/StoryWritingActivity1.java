package com.android.tiki_taka.ui.activity.Album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryWritingAdapter;
import com.android.tiki_taka.models.dtos.PhotoUriRequest;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.ui.activity.Profile.HomeActivity;
import com.android.tiki_taka.ui.activity.Sign.SigninActivity1;
import com.android.tiki_taka.ui.fragment.AlbumFragment;
import com.android.tiki_taka.utils.NavigationHelper;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoryWritingActivity1 extends AppCompatActivity {
    StoryApiService service;
    int userId;
    ArrayList<Uri> selectedUris;
    PhotoUriRequest photoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_writing1);

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);


        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        selectedUris = getIntent().getParcelableArrayListExtra("selectedUris");
        recyclerView.setAdapter(new StoryWritingAdapter(selectedUris, this));

        TextView cancelBtn = findViewById(R.id.textView33);
        cancelBtn.setOnClickListener(v -> finish());
        TextView uploadBtn = findViewById(R.id.textView34);
        uploadBtn.setOnClickListener(v -> savePhotoCards());
        ImageView editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> NavigationHelper.navigateToActivity(this, StoryWritingActivity2.class,null));

    }

    private void savePhotoCards() {
        convertUrisToStringListAndWrap();
        insertStoryCardsInDB();
        InitializeStackAndNavigateToAlbumFragment();
    }

    private void convertUrisToStringListAndWrap(){
        ArrayList<String> uriStrings = new ArrayList<>();
        for (Uri uri : selectedUris) {
            uriStrings.add(uri.toString());
        }
        photoRequest = new PhotoUriRequest(userId, uriStrings);
    }

    private void insertStoryCardsInDB(){
        service.savePhotoUris(photoRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProcessInsertingCardsResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
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
                showToast(message);

            } catch (JSONException | IOException e) {
                // JSON 파싱 오류 처리 , IOException 처리
                e.printStackTrace();
                Log.e("Error","catch문 오류 발생");
            }
        } else {
            Log.e("Error","서버 응답 오류");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private String parseResponseData(Response<ResponseBody> response) throws JSONException, IOException {
        String responseJson = response.body().string();
        JSONObject jsonObject = new JSONObject(responseJson);
        return jsonObject.getString("message");
    }

    private void InitializeStackAndNavigateToAlbumFragment(){
        // (HomeActivity.class) AlbumFragment로 이동면서 스택 초기화
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // HomeActivity를 새 태스크로 시작하고, 이전에 있던 모든 액티비티를 클리어
        intent.putExtra("OPEN_FRAGMENT", "ALBUM_FRAGMENT"); // 추가 정보
        startActivity(intent);
    }

}