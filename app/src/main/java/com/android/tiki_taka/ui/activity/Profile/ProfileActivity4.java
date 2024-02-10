package com.android.tiki_taka.ui.activity.Profile;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.services.ProfileApiService;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity4 extends AppCompatActivity {
    Button saveBtn;
    TextInputEditText nameView;
    ProfileApiService service;
    int userId; // 유저 식별 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile4);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("이름 변경");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Retrofit retrofit = RetrofitClient.getClient();
        service = retrofit.create(ProfileApiService.class);
        userId = SharedPreferencesHelper.getUserId(this);

        saveBtn = findViewById(R.id.button);
        nameView = findViewById(R.id.이름);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = String.valueOf(nameView.getText());
                Call<ResponseBody> call = service.updateProfileName(userId, nameText);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        responseProcess(response);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 요청 실패 처리
                        Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
                    }
                });

            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void responseProcess(Response<ResponseBody> response){
        // 요청 성공 처리
        if (response.isSuccessful()) {
            // http 요청 성공시
            try {
                String responseJson = response.body().string();
                JSONObject jsonObject = new JSONObject(responseJson);
                boolean success = jsonObject.getBoolean("success");
                String message = jsonObject.getString("message");

                if (success) {
                    // 저장 성공
                    showToast(message);
                    // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
                    finish();

                } else {
                    // 저장 실패
                   showToast(message);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
                showToast("오류 발생");
            }

        } else {
            showToast("서버 응답 오류");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}