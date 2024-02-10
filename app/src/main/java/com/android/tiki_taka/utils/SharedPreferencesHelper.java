package com.android.tiki_taka.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static void saveAutoLoginState(Context context, boolean state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAutoLoginEnabled", state);
        editor.apply();
    }
}
