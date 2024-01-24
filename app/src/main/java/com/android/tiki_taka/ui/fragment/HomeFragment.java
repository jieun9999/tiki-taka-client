package com.android.tiki_taka.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tiki_taka.R;
import com.android.tiki_taka.models.HomeProfiles;
import com.android.tiki_taka.models.PartnerProfile;
import com.android.tiki_taka.models.UserProfile;
import com.android.tiki_taka.services.ApiService;
import com.android.tiki_taka.utils.DateUtils;
import com.android.tiki_taka.utils.RetrofitClient;
import com.android.tiki_taka.utils.ValidatorSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ApiService service;
    //홈 화면 뷰들
    ImageView profile1;
    ImageView profile2;
    TextView loveDate;
    TextView name1;
    TextView name2;
    ImageView changeBackgroundBtn;
    int userId; // 유저 식별 정보

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // url설정한 Retrofit 인스턴스를 사용하기 위해 호출
        Retrofit retrofit = RetrofitClient.getClient();
        // Retrofit을 통해 ApiService 인터페이스를 구현한 서비스 인스턴스를 생성
        service = retrofit.create(ApiService.class);

        //Android 프래그먼트에서 getSharedPreferences 메서드를 사용하려 할 때, getSharedPreferences는 Context 객체의 메서드이기 때문에 직접적으로 사용할 수 없습니다.
        // 프래그먼트에서는 getActivity() 메서드를 통해 액티비티의 컨텍스트에 접근한 다음, 이 컨텍스트를 사용하여 getSharedPreferences를 호출해야 합니다.
        //쉐어드에서 userId 가져오기
        if(getActivity()!= null){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            userId = sharedPreferences.getInt("userId", -1); // 기본값으로 -1이나 다른 유효하지 않은 값을 설정
        }

        //홈화면 데이터 불러오기
        getHomeProfile();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        profile1 = rootView.findViewById(R.id.imageView20);
        profile2 = rootView.findViewById(R.id.imageView22);
        loveDate = rootView.findViewById(R.id.textView14);
        name1 = rootView.findViewById(R.id.textView16);
        name2 = rootView.findViewById(R.id.textView17);
        changeBackgroundBtn = rootView.findViewById(R.id.imageView19);

        return rootView;
    }

    private void getHomeProfile(){
        // 1. 유저 프로필 정보 가져오기
        Call<HomeProfiles> call = service.getHomeProfile(userId);
        call.enqueue(new Callback<HomeProfiles>() {
            @Override
            public void onResponse(Call<HomeProfiles> call, Response<HomeProfiles> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //서버에서 홈프로필 정보를 가져옴
                    HomeProfiles homeProfiles = response.body();
                    // 유저 프로필 정보 처리
                    UserProfile userProfile = homeProfiles.getUserProfile();
                    // 파트너 프로필 정보 처리
                    PartnerProfile partnerProfile = homeProfiles.getPartnerProfile();

                    //나의 프로필 교체
                    String myName = userProfile.getName();
                    name2.setText(myName);
                    //UserProfile 객체에서 Base64 인토딩된 이미지 문자열을 가져옴
                    String base64Image = userProfile.getProfileImage();
                    // 로그를 찍습니다. "HomeActivity"는 로그 태그입니다.
                    Log.d("HomeActivity", "Base64 Image String: " + base64Image);
                    ValidatorSingleton.getInstance().updateImageViewWithProfileImage(base64Image, profile2);

                    String firstDateStr = userProfile.getMeetingDay();
                    //사귄 날짜부터 지난 일수 계산
                    long daysTogether = DateUtils.calculateDaysSince(firstDateStr);
                    // 이 데이터를 홈 액티비티의 뷰에 설정하는 로직 구현

                    if (daysTogether >= 0) {
                        loveDate.setText(String.valueOf(daysTogether));
                    } else {
                        // 유효하지 않은 날짜 데이터 처리
                        Log.e("Date Error", "유효하지 않은 날짜 데이터: " + firstDateStr);
                    }

                    //파트너 프로필 교체
                    // 예: 이름, 프로필 사진을 홈 액티비티의 뷰에 설정
                    String myName2 = partnerProfile.getName();
                    name1.setText(myName2);

                    //UserProfile 객체에서 Base64 인토딩된 이미지 문자열을 가져옴
                    String base64Image2 = partnerProfile.getProfileImage();
                    ValidatorSingleton.getInstance().updateImageViewWithProfileImage(base64Image2, profile1);

                } else {
                    Log.e("Error", "서버에서 불러오기에 실패: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<HomeProfiles> call, Throwable t) {
                //네트워크 오류 처리
                Log.e("Network Error", "네트워크 호출 실패: " + t.getMessage());
            }
        });


    }
}