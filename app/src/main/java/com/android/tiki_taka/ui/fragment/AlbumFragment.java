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
import android.widget.Toast;

import com.android.tiki_taka.R;
import com.android.tiki_taka.adapters.StoryFolderAdapter;
import com.android.tiki_taka.listeners.ItemClickListener;
import com.android.tiki_taka.models.dtos.StoryFolderDto;
import com.android.tiki_taka.models.responses.StoryFoldersResponse;
import com.android.tiki_taka.services.StoryApiService;
import com.android.tiki_taka.ui.activity.Album.StoryFolderActivity1;
import com.android.tiki_taka.utils.RetrofitClient;

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
        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        storyFolderApiService = retrofit.create(StoryApiService.class);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정

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

        return view;

    }

    private void loadStoryFolders() {
        storyFolderApiService.getStoryFolders(userId).enqueue(new Callback<StoryFoldersResponse>() {
            @Override
            public void onResponse(Call<StoryFoldersResponse> call, Response<StoryFoldersResponse> response) {
                processStoryFolderResponse(response);

            }

            @Override
            public void onFailure(Call<StoryFoldersResponse> call, Throwable t) {
                // 요청 실패 처리
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
            // 응답 실패
            Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
        }
    }

    private void updateUIOnSuccess(StoryFoldersResponse storyFoldersResponse){

        List<StoryFolderDto> storyFolderDtos = storyFoldersResponse.getStoryFolders();

        // 서버에서 가져온 리스트를 어댑터에 추가함
        adapter.setData(storyFolderDtos);

        String message = storyFoldersResponse.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleFailure(StoryFoldersResponse storyFoldersResponse){
        String message = storyFoldersResponse.getMessage();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    //아이템 클릭시, 아이템의 id를 다음 화면에 넘겨줍니다.
    @Override
    public void onItemClick(int position) {
        StoryFolderDto clickedItem = adapter.getItem(position);

        Intent intent = new Intent(getContext(), StoryFolderActivity1.class);
        intent.putExtra("CLICKED_ITEM_ID", clickedItem.getFolderId());
        startActivity(intent);
    }
}