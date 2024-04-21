package com.android.tiki_taka.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.tiki_taka.ui.activity.Profile.HomeActivity;


public class IntentHelper {
    // Bundle 객체를 만들어 타겟 액티비티로 보냄
        public static void navigateToActivityForResultWithBundle(Activity activity, Class<?> targetActivity, Bundle bundle, int requestCode) {
            Intent intent = new Intent(activity, targetActivity);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivityForResult(intent, requestCode);
        }

    // 결과를 받기 위해 사용
    // int id를 타겟 액티비티로 보냄
    public static void passToActivityWithId(Activity activity, Class<?> targetActivity, int id, int requestCode) {
        Intent intent = new Intent(activity, targetActivity);
        if (id != -1) {
            intent.putExtra("id", id);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    // 단순히 액티비티 간의 전환을 위해 사용
    // int id를 타겟 액티비티로 보냄
    public static void passToActivityWithId(Activity currentActivity, Class<?> targetActivityClass, int id) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        // id가 유효한 값인지 확인 후 인텐트에 추가
        if (id >= 0) {
            intent.putExtra("id", id);
        }
        currentActivity.startActivity(intent);
    }

    // Intent에서 ID를 추출
    public static int getIdFromIntent(Activity activity){
        Intent intent = activity.getIntent();
        if(intent != null && intent.hasExtra("id")){
            return intent.getIntExtra("id", -1);
        }
        return -1;
    }

    // 호출한 액티비티에 결과를 설정하고 액티비티를 종료하는 메소드.
    public static void setResultAndFinish(Activity activity, int resultCode) {
        Intent resultIntent = new Intent();
        activity.setResult(resultCode, resultIntent);
        activity.finish();
    }

    // 스택 초기화 후 앨범 프래그먼트로 이동
    public static void passToAlbumFragment(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("OPEN_FRAGMENT", "ALBUM_FRAGMENT");
        context.startActivity(intent);
    }


}
