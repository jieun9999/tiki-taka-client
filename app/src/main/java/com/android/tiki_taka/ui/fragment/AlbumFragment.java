package com.android.tiki_taka.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryFolderAdapter;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dtos.StoryFolderDto;
import com.android.tiki_taka.models.responses.StoryFoldersResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.ui.activity.Album.SelectionActivity1;
import com.android.tiki_taka.ui.activity.Album.StoryFolderActivity;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.SharedPreferencesHelper;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// 프래그먼트 내에서 ItemClickListener 인터페이스를 구현하고, 이를 어댑터에 전달할 수 있음
public class AlbumFragment extends Fragment implements ItemClickListener {
    private StoryFolderAdapter adapter;
    StoryApiService storyFolderApiService;
    int userId; // 유저 식별 정보

    public AlbumFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 프래그먼트 생성 시 초기화, 비 UI 관련 작업 수행

        super.onCreate(savedInstanceState);
        Retrofit retrofit = RetrofitClient.getClient();
        storyFolderApiService = retrofit.create(StoryApiService.class);
        userId = SharedPreferencesHelper.getUserId(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트의 뷰 생성 및 UI 관련 작업 수행
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 어댑터 설정 이후 데이터 로드
        // 1.빈 어댑터로 초기화
        adapter = new StoryFolderAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        // 2.데이터를 비동기적으로 가져오는 메서드 호출
        loadStoryFolders();

        ImageView plusBtn = view.findViewById(R.id.imageView39);
        plusBtn.setOnClickListener(v -> navigateToSelectionActivity()); //매개변수 v는 클릭 이벤트가 발생한 View 객체를 참조, 즉 사용자가 클릭한 버튼을 의미

        return view;

    }
    private void navigateToSelectionActivity() {
        Intent intent = new Intent(getContext(), SelectionActivity1.class);
        startActivity(intent);
    }

    private void loadStoryFolders() {
        storyFolderApiService.getStoryFolders(userId).enqueue(new Callback<StoryFoldersResponse>() {
            @Override
            public void onResponse(Call<StoryFoldersResponse> call, Response<StoryFoldersResponse> response) {
                processStoryFolderResponse(response);

            }

            @Override
            public void onFailure(Call<StoryFoldersResponse> call, Throwable t) {
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });

    }

    private void processStoryFolderResponse(Response<StoryFoldersResponse> response){
        if(response.isSuccessful() && response.body() != null){

            StoryFoldersResponse storyFoldersResponse = response.body();
            if(storyFoldersResponse.isSuccess()){
                updateUIOnSuccess(storyFoldersResponse);

            }else {
                handleFailure(storyFoldersResponse);

            }

        }else {
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void updateUIOnSuccess(StoryFoldersResponse storyFoldersResponse){

        List<StoryFolderDto> storyFolderDtos = storyFoldersResponse.getStoryFolders();

        adapter.setData(storyFolderDtos);

        String message = storyFoldersResponse.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleFailure(StoryFoldersResponse storyFoldersResponse){
        String message = storyFoldersResponse.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        StoryFolderDto clickedItem = adapter.getItem(position);

        Intent intent = new Intent(getContext(), StoryFolderActivity.class);
        intent.putExtra("CLICKED_ITEM_ID", clickedItem.getFolderId());
        startActivity(intent);
    }
}