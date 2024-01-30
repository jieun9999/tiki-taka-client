package com.android.tiki_taka.services;
import com.android.tiki_taka.models.CodeResponse;
import com.android.tiki_taka.models.HomeProfiles;
import com.android.tiki_taka.models.UserProfile;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileApiService {

    @GET("/UserPref/homeProfile.php")
    Call<HomeProfiles> getHomeProfile(@Query("userId") int userId);

    @POST("/UserPref/updateBackgroundImage.php")
    Call<ResponseBody> updateBackgroundImage(@Body RequestBody body);

    @GET("/UserPref/getMyModalData.php")
    Call<ResponseBody> getMyModalData(@Query("userId") int userId);

    @GET("/UserPref/getPtnrModalData.php")
    Call<ResponseBody> getPtnrModalData(@Query("userId") int userId);

    @POST("/UserPref/updateProfileBackImage.php")
    Call<ResponseBody> updateProfileBackImage(@Body RequestBody body);

    @POST("/UserPref/updateProfileImage.php")
    Call<ResponseBody> updateProfileImage(@Body RequestBody body);

    @FormUrlEncoded
    @POST("/UserPref/updateProfileName.php")
    Call<ResponseBody> updateProfileName(@Field("userId") int userId,
                                        @Field("name") String name);
    //"name"은 폼 필드의 키로 사용되며, String name은 해당 필드에 저장될 값

    @FormUrlEncoded
    @POST("/UserPref/updateProfileMessage.php")
    Call<ResponseBody> updateProfileMessage(@Field("userId") int userId,
                                            @Field("message") String message);

    @GET("/UserPref/disconnectAccount.php")
    Call<ResponseBody> disconnectAccount(@Query("userId") int userId);

    @GET("/UserPref/checkConnectState.php")
    Call<ResponseBody> checkConnectState(@Query("userId") int userId);

    @GET("/UserPref/reconnectAccount.php")
    Call<ResponseBody> reconnectAccount(@Query("userId") int userId);

   @GET("/UserPref/dropAccount.php")
    Call<ResponseBody> dropAccount(@Query("userId") int userId);
}
