package com.android.tiki_taka.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String SHARED_PREF_NAME = "MySharedPref";
    private static final int DEFAULT_USER_ID = -1;

    public static void saveAutoLoginState(Context context, boolean state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAutoLoginEnabled", state);
        editor.apply();
    }

    public static int getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt("userId", DEFAULT_USER_ID);
    }

    public static void clearShared(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // SharedPreferences의 모든 데이터를 초기화
        editor.clear();
        editor.apply(); // 비동기적으로 저장
    }

    public static void setRoomId(Context context, int roomId){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("roomId", roomId);
        editor.apply();
    }

    public static int getRoomId(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt("roomId", DEFAULT_USER_ID);
    }

    public static void setPartnerId(Context context, int partnerId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("partnerId", partnerId);
        editor.apply();
    }

    public static int getPartnerId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt("partnerId", DEFAULT_USER_ID);
    }


}
