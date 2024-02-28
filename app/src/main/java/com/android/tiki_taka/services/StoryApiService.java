package com.android.tiki_taka.services;

import com.android.tiki_taka.models.dtos.CommentIdRequest;
import com.android.tiki_taka.models.dtos.CommentItem;
import com.android.tiki_taka.models.dtos.LikeStatusRequest;
import com.android.tiki_taka.models.dtos.StoryCard;
import com.android.tiki_taka.models.dtos.StoryCardRequest;
import com.android.tiki_taka.models.responses.ApiResponse;
import com.android.tiki_taka.models.responses.StoryCardsResponse;
import com.android.tiki_taka.models.responses.StoryFolderResponse;
import com.android.tiki_taka.models.responses.StoryFoldersResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StoryApiService {
    @GET("Story/getStoryFolders.php")
    Call<StoryFoldersResponse> getStoryFolders(@Query("userId") int userId);
    // <StoryFoldersResponse>는 서버에서 받아올 데이터 형식을 지정

   @GET("Story/getThumbNail.php")
    Call<StoryFolderResponse> getThumbNail(@Query("folderId") int folderId);

    @GET("Story/getStoryCards.php")
    Call<StoryCardsResponse> getStoryCards(@Query("folderId") int folderId);

    @POST("Story/saveImageStoryCards.php")
    Call<ResponseBody> saveImageStoryCards(@Body StoryCardRequest cardRequest);

    @POST("Story/saveMemoStoryCard.php")
    Call<ResponseBody> saveMemoStoryCard(@Body StoryCardRequest cardRequest);

   @POST("Story/saveVideoStoryCard.php")
   Call<ResponseBody> saveVideoStoryCard(@Body StoryCardRequest cardRequest);

    @GET("Story/getCardDetails.php")
    Call<StoryCard> getCardDetails(@Query("cardId") int cardId);

   @GET("Story/getPreviewComments.php")
    Call<List<CommentItem>> getPreviewComments(@Query("cardId") int cardId);

    @GET("Story/getComments.php")
    Call<List<CommentItem>> getComments(@Query("cardId") int cardId);

    @POST("Story/postComment.php")
    Call<ApiResponse> postComment(@Body CommentItem commentItem);

    @POST("Story/deleteComment.php")
    Call<ApiResponse> deleteComment(@Body CommentIdRequest commentIdRequest);

    @POST("Story/updateLikeStatus.php")
    Call<ApiResponse> updateLikeStatus(@Body LikeStatusRequest likeStatusRequest);

}
