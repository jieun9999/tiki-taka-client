package com.android.tiki_taka.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;


import com.android.tiki_taka.R;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    //제공된 코드는 이미지 처리와 관련된 다양한 작업을 수행하는 메서드들을 포함하고 있으며,
    // 이러한 메서드들은 상태를 저장하거나 공유 리소스에 대한 전역적인 접근을 제어할 필요가 없습니다.
    // 대신, 이 메서드들은 주로 이미지 변환, 이미지 로딩 등의 유틸리티 작업을 수행합니다.
    // 따라서, 이 경우 유틸리티 클래스로 구현하는 것이 더 적합합니다.

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
    // Glide를 사용하여 imageUriString으로부터 이미지를 렌더링하는 분기 로직은, URI가 HTTP/HTTPS URL인지, 또는 로컬 파일 시스템의 content/file URI인지에 따라 달라짐
    public static void loadImage(String imageUriString, ImageView imageView, Context context) {
        if (imageUriString.startsWith("http://") || imageUriString.startsWith("https://")) {
            // 웹 URL에서 이미지 로드
            Glide.with(context)
                    .load(imageUriString)
                    .into(imageView);
        } else {
            // 로컬 content URI 또는 파일 URI에서 이미지 로드
            // Uri.parse()를 사용하여 imageUriString에서 직접 Uri 객체 생성
            Uri imageUri = Uri.parse(imageUriString);
            Glide.with(context)
                    .load(imageUri)
                    .into(imageView);
        }
        // file:///storage/emulated/0/Android/data/com.android.tiki_taka/cache/temp_thumbnail.jpg
        // ex.  안드로이드 기기 내부 스토리지에 있는 로컬 파일의 경로

    }

    public static void loadDrawableIntoView(Context context, ImageView imageView, String drawableName) {
        @SuppressLint("DiscouragedApi") int resourceId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }


}
