package com.android.tiki_taka.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tiki_taka.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 프래그먼트 생성 시 초기화, 비 UI 관련 작업 수행
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //프래그먼트의 뷰 생성 및 UI 관련 작업 수행
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_album, container,false);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);

        // 탭 설정
        tabLayout.addTab(tabLayout.newTab().setText("스토리"));
        tabLayout.addTab(tabLayout.newTab().setText("추억상자"));

        // 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new StoryFragment();
                        break;
                    case 1:
                        selectedFragment = new MemoryBoxFragment();
                        break;
                }

                // 프래그먼트 전환
                if (selectedFragment != null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 탭 선택 해제 시 필요한 경우 여기에 로직 추가
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 탭이 재선택될 때 필요한 경우 여기에 로직 추가
            }
        });

        // 현재 화면에 보이는 프래그먼트 내에서 자식 프래그먼트들을 관리하기 위한 용도
        // 초기상태에서 스토리 프래그먼트를 선택
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new StoryFragment())
                .commit();

        return rootView;
    }

}