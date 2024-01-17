package com.android.tiki_taka;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    // 안드로이드 클라이언트에서 Retrofit 인터페이스를 정의함
    // 이 인터페이스는 서버의 특정 URL로 HTTP요청을 보내는 메서드를 정의함

    @GET("/emailVerification.php") // 서버의 해당 엔드포인트
    Call<Boolean> checkUserEmail(@Query("email") String email);
    //@Query("email")는 "email"이라는 이름의 URL 쿼리 매개변수에 이메일 주소를 전달

    @FormUrlEncoded
    @POST("/sendVerificationCode.php")
    Call<Boolean> sendEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("/checkAuthCode.php")
    Call<ResponseBody> sendAuthCode(@Field("email") String email,
                                    @Field("authCode") String authCode);

    @FormUrlEncoded
    @POST("/savePass.php")
    Call<Boolean> sendPass(@Field("email") String email,
                           @Field("password") String password);

    @GET("/generateCode.php")
    Call<CodeResponse> getInvitationCode(@Query("userId") int userId);

    @FormUrlEncoded
    @POST("/connect.php")
    Call<ResponseBody> sendInviteCode(@Field("userId") int userId,
                                      @Field("inviCode") String inviCode);

}
