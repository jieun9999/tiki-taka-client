package com.android.tiki_taka.services;

import com.android.tiki_taka.models.dto.StoryFolder;
import com.android.tiki_taka.models.request.CardIdRequest;
import com.android.tiki_taka.models.request.CommentIdRequest;
import com.android.tiki_taka.models.request.CommentRequest;
import com.android.tiki_taka.models.request.LikeStatusRequest;
import com.android.tiki_taka.models.dto.StoryCard;
import com.android.tiki_taka.models.request.StoryCardRequest;
import com.android.tiki_taka.models.response.ApiResponse;
import com.android.tiki_taka.models.response.FolderDeletedResponse;
import com.android.tiki_taka.models.response.StoryCardsResponse;
import com.android.tiki_taka.models.response.StoryFolderResponse;
import com.android.tiki_taka.models.response.StoryFoldersResponse;
import com.android.tiki_taka.models.response.SuccessAndMessageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface StoryApiService {
    @GET("Story/getStoryFolders.php")
    Call<StoryFoldersResponse> getStoryFolders(@Query("userId") int userId, @Query("partnerId") int partnerId);
    // <StoryFoldersResponse>는 서버에서 받아올 데이터 형식을 지정

   @GET("Story/getFolderData.php")
    Call<StoryFolderResponse> getFolderData(@Query("folderId") int folderId);

    @GET("Story/getStoryCards.php")
    Call<StoryCardsResponse> getStoryCards(@Query("folderId") int folderId);

    @Multipart
    @POST("Story/saveImageStoryCards.php")
    Call<SuccessAndMessageResponse> saveImageStoryCards(
            @Part List<MultipartBody.Part> uris,
            @Part MultipartBody.Part displayImage, //이미지와 텍스트 모두 전송 가능
            @Part("userId") RequestBody userId,
            @Part("title") RequestBody title,
            @Part("location") RequestBody location,
            @Part("comments") List<RequestBody> comments,
            @Part("partnerId") RequestBody partnerId,
            @Part("folderId") RequestBody folderId
            );

    @POST("Story/saveMemoStoryCard.php")
    Call<ResponseBody> saveMemoStoryCard(@Body StoryCardRequest cardRequest);

    @POST("Story/updateMemoStoryCard.php")
    Call<ResponseBody> updateMemoStoryCard(@Body StoryCardRequest cardRequest);

   @Multipart
   @POST("Story/saveVideoStoryCard.php")
   Call<SuccessAndMessageResponse> saveVideoStoryCard(
           @Part List<MultipartBody.Part> uris, // 하나 이상의 동영상 파일
           @Part MultipartBody.Part displayImage, //이미지와 텍스트 모두 전송 가능
           @Part("userId") RequestBody userId,
           @Part("title") RequestBody title,
           @Part("location") RequestBody location,
           @Part("comments") List<RequestBody> comments,
           @Part("partnerId") RequestBody partnerId,
           @Part("folderId") RequestBody folderId
   );

    @GET("Story/getCardDetails.php")
    Call<StoryCard> getCardDetails(@Query("cardId") int cardId);

   @GET("Story/getPreviewComments.php")
    Call<List<CommentRequest>> getPreviewComments(@Query("cardId") int cardId);

    @GET("Story/getComments.php")
    Call<List<CommentRequest>> getComments(@Query("cardId") int cardId);

    @POST("Story/postComment.php")
    Call<ApiResponse> postComment(@Body CommentRequest commentItem);

    @POST("Story/deleteComment.php")
    Call<ApiResponse> deleteComment(@Body CommentIdRequest commentIdRequest);
    @POST("Story/updateComment.php")
    Call<ApiResponse> updateComment(@Body CommentRequest commentItem);

    @POST("Story/updateLikeStatus.php")
    Call<ApiResponse> updateLikeStatus(@Body LikeStatusRequest likeStatusRequest);

    @POST("Story/deleteCard.php")
    Call<FolderDeletedResponse> deleteCard(@Body CardIdRequest cardIdRequest);

    @POST("Story/updateFolder.php")
    Call<ApiResponse> updateFolder(@Body StoryFolder storyFolder);

    @POST("Story/updateFolderLocationInCard.php")
    Call<ApiResponse> updateFolderLocationInCard(@Body StoryCard storyCard);

}
