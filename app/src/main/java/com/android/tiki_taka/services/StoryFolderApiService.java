package com.android.tiki_taka.services;

import com.android.tiki_taka.models.dtos.StoryFolderDto;
import com.android.tiki_taka.models.responses.StoryFoldersResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StoryFolderApiService {
    @GET("StoryFolder/getStoryFolders.php")
    Call<StoryFoldersResponse> getStoryFolders(@Query("userId") int userId);
    // <StoryFoldersResponse>는 서버에서 받아올 데이터 형식을 지정

}
