package com.android.tiki_taka.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationHelper {
    // 액티비티 -> 액티비티 이동, 결과를 받기 위한 메서드
    public static void navigateToActivity(Activity activity, Class<?> targetActivity, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, targetActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    // 액티비티 -> 프래그먼트 이동
    public static void navigateToFragment(FragmentActivity activity, int containerViewId, Fragment targetFragment, boolean addToBackStack) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerViewId, targetFragment);
        if (addToBackStack) { //사용자가 뒤로 가기 버튼을 눌렀을 때 이전 프래그먼트 상태로 돌아갈 수 있음
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    // 프래그먼트 -> 액티비티 이동
    public static void navigateFromFragmentToActivity(Fragment fragment, Class<?> targetActivity, Bundle bundle) {
        Context context = fragment.getContext();
        if (context != null) {
            Intent intent = new Intent(context, targetActivity);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        }
    }
}
