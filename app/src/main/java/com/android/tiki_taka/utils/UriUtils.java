package com.android.tiki_taka.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

    // 이미지 파일을 서버에 업로드 할때는 실제 파일 경로가 필요함
    // content:// URI를 실제 파일 경로로 변환하는 방법
    public static String getRealPathFromURIString(Context context, String uriString) {
        Uri contentUri = Uri.parse(uriString);
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null; // 파일 경로를 찾을 수 없는 경우
    }
}
