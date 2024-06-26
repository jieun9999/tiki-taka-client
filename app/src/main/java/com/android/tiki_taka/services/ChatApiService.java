package com.android.tiki_taka.services;

import com.android.tiki_taka.models.dto.ChatRoom;
import com.android.tiki_taka.models.dto.FcmToken;
import com.android.tiki_taka.models.dto.Message;
import com.android.tiki_taka.models.response.ApiResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatApiService {

    @POST("Chat/makeChatRoom.php")
    Call<ResponseBody> makeChatRoom(@Body ChatRoom chatRoom);

    @GET("Chat/getMessages.php")
    Call<List<Message>> getMessages(@Query("roomId") int roomId);

    @POST("Chat/saveDateMarker.php")
    Call<ResponseBody> saveDateMarker(@Body Message message);

    @POST("Chat/saveToken.php")
    Call<ApiResponse> saveToken(@Body FcmToken fcmToken);

    @GET("Chat/loadLastReadMessageId.php")
    Call<Message> loadLastReadMessageId(@Query("currentUserId") int currentUserId);

    @GET("Chat/readAllMessages.php")
    Call<Boolean> readAllMessages(@Query("userId") int userId);

}
