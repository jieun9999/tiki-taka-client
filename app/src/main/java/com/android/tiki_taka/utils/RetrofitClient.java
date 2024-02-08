package com.android.tiki_taka.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 싱글톤 패턴은 전역적으로 단 하나의 인스턴스만을 생성하고 관리해야 할때 유용합니다.
    // 예를 들어, 애플리케이션 전반에 걸쳐 사용되는 공유 리소스나 서비스에 접근할 필요가 있는 경우에 적합합니다.
    // 데이터베이스 연결관리, 네트워크 요청 관리, 로깅, 환경 설정 관리 등이 싱글톤 패턴을 사용하기 좋은 예시입니다.
    // (상태를 유지할 필요가 있을때 사용함)
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://52.79.41.79/";

    // Retrofit 인스턴스를 생성하는 메서드
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
