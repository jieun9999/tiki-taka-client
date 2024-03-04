package com.android.tiki_taka.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class IntentHelper {
    // A 액티비티 -> B 액티비티 이동, 결과를 받기 위한 메서드 (Bundle bundle)
    public static void navigateToActivity(Activity activity, Class<?> targetActivity, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, targetActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    // A 액티비티 -> B액티비티 이동, 결과를 받기 위한 메서드 (int id)
    public static void navigateToActivity(Activity activity, Class<?> targetActivity, int id, int requestCode) {
        Intent intent = new Intent(activity, targetActivity);
        if (id != -1) {
            intent.putExtra("id", id);
        }
        activity.startActivityForResult(intent, requestCode);
    }


    // B 액티비티에서 A 액티비티의 데이터를 받을때 (int id)
    public static int getId(Activity activity){
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

    // 현재 액티비티에서 다른 액티비티로 이동하는 메서드, id 전달
    public static void navigateToActivity(Activity currentActivity, Class<?> targetActivityClass, int id) {
        Intent intent = new Intent(currentActivity, targetActivityClass);
        // id가 유효한 값인지 확인 후 인텐트에 추가
        if (id >= 0) {
            intent.putExtra("id", id);
        }
        currentActivity.startActivity(intent);
    }


}
