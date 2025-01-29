package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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

    // 파일 경로 대신 URI로부터 파일을 복사하여 임시 파일을 만드는 방식
    public static File getFileFromUri(Context context, Uri uri) throws Exception {
        ContentResolver contentResolver = context.getContentResolver();
        String fileName = getFileName(contentResolver, uri);
        File tempFile = new File(context.getCacheDir(), fileName);

        try (InputStream inputStream = contentResolver.openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return tempFile;
    }

    // URI로부터 파일 이름을 가져오는 메서드
    @SuppressLint("Range")
    private static String getFileName(ContentResolver contentResolver, Uri uri) {
        String fileName = "";
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        return fileName;
    }
}
