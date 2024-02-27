package com.android.tiki_taka.utils;

import android.content.Context;
import android.content.Intent;

import com.android.tiki_taka.ui.activity.Profile.HomeActivity;

public class InitializeStack {
    public static void navigateToAlbumFragment(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("OPEN_FRAGMENT", "ALBUM_FRAGMENT");
        context.startActivity(intent);
    }
}
