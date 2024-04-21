package com.android.tiki_taka.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    // Private 생성자를 사용하여 인스턴스화 방지
    private ImageUtils() {}

    //사진을 저장할 파일 생성, 이 파일 경로는 나중에 사진의 경로로 사용됨
    public static File createImageFile(Context context) throws IOException {
        // 이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    // 이미지 로드 메서드
    public static void loadImage(String imageUriString, ImageView imageView, Context context) {
        // 웹 URL에서 이미지 로드
        Glide.with(context)
                .load(imageUriString)
                .into(imageView);
    }

    public static void loadDrawableIntoView(Context context, ImageView imageView, String drawableName) {
        @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }

}
