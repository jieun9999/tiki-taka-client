package com.android.tiki_taka.services;
import com.android.tiki_taka.models.CodeResponse;
import com.android.tiki_taka.models.UserProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    // 안드로이드 클라이언트에서 Retrofit 인터페이스를 정의함
    // 이 인터페이스는 서버의 특정 URL로 HTTP요청을 보내는 메서드를 정의함

    @GET("/Auth/emailVerification.php") // 서버의 해당 엔드포인트
    Call<Boolean> checkUserEmail(@Query("email") String email);
    //@Query("email")는 "email"이라는 이름의 URL 쿼리 매개변수에 이메일 주소를 전달

    //폼 데이터를 전송
    @FormUrlEncoded
    @POST("/Auth/sendVerificationCode.php")
    Call<Boolean> sendEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("/Auth/checkAuthCode.php")
    Call<ResponseBody> sendAuthCode(@Field("email") String email,
                                    @Field("authCode") String authCode);

    @FormUrlEncoded
    @POST("/Auth/savePass.php")
    Call<Boolean> sendPass(@Field("email") String email,
                           @Field("password") String password);

    //URL에 쿼리 파라미터를 추가
    @GET("/Auth/generateCode.php")
    Call<CodeResponse> getInvitationCode(@Query("userId") int userId);

    @FormUrlEncoded
    @POST("/Auth/connect.php")
    Call<ResponseBody> sendInviteCode(@Field("userId") int userId,
                                      @Field("inviCode") String inviCode);
    //@field나 @Query는 키-값 쌍 형식으로 데이터를 저장

    @POST("/Auth/saveUserProfile.php")
    Call<ResponseBody> saveUserProfile(@Body UserProfile userProfile);
     //@Body 어노테이션으로 UserProfile 객체를 전송하는 경우, 이 데이터는 기본적으로 JSON 형식으로 서버에 전달


    @FormUrlEncoded
    @POST("/Auth/signIn.php")
    Call<ResponseBody> signIn(@Field("email") String email,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("/Auth/sendTemporaryPassword.php")
    Call<ResponseBody> sendTemporaryPassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("/Auth/saveNewPassword.php")
    Call<ResponseBody> saveNewPassword(@Field("email") String email,
                                       @Field("temporaryPassword") String temporaryPassword,
                                       @Field("newPassword") String newPassword);

}
