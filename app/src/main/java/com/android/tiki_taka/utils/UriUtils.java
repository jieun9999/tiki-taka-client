package com.android.tiki_taka.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public class UriUtils {

    public static boolean isVideoUri(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String mimeType = contentResolver.getType(uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    public static boolean isImageUri(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String mimeType = contentResolver.getType(uri);
        return mimeType != null && mimeType.startsWith("image/");
    }
}
