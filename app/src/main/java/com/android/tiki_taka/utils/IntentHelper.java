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



}
