package com.android.tiki_taka.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import java.io.IOException;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoUtils {
    private VideoUtils() {}

    public static String extractYoutubeVideoId(String videoUrl) {
        // YouTube 동영상 ID를 추출하기 위한 정규 표현식 패턴
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|shorts\\/|youtu.be\\/)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoUrl); // 주어진 URL에 대해 패턴 적용

        if (matcher.find()) {
            return matcher.group(); // 첫 번째로 찾아진 동영상 ID 반환
        }
        return null; // 동영상 ID를 찾지 못한 경우 null 반환
    }

    // Glide를 사용해 동영상 URI에서 썸네일을 로드하는 메서드
    public static void loadVideoThumbnail(Context context, Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .thumbnail(0.1f) // 썸네일의 크기를 지정
                .into(imageView);
    }


}
