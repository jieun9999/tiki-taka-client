package com.android.tiki_taka.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.android.tiki_taka.ui.activity.Album.LocalVideoPlayerActivity;
import com.android.tiki_taka.ui.activity.Album.YoutubeVideoPlayerActivity;
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

    // video Uri를 가지고 썸네일 이미지를 Uri 형식으로 추출하는 메서드
    public static Uri getThumbNailUri(Context context, Uri videoUri){
            // 썸네일 추출
            Bitmap thumbnail = extractThumbnail(context, videoUri);
            if (thumbnail == null) {
                // 썸네일 추출 실패 처리
                return null;
            }

            // 썸네일을 임시 파일로 저장
            File thumbnailFile = saveThumbnailToFile(context, thumbnail);
            if (thumbnailFile == null) {
                // 파일 저장 실패 처리
                return null;
            }
            return  Uri.fromFile(thumbnailFile);
    }


    // 비디오 Uri에서 썸네일 추출
    private static Bitmap extractThumbnail(Context context, Uri videoUri) {
        Bitmap thumbnail = null;
        try {
            Long videoId = Long.parseLong(videoUri.getLastPathSegment());
            thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
                    context.getContentResolver(),
                    videoId,
                    MediaStore.Video.Thumbnails.MINI_KIND,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnail;
    }

    // 썸네일을 파일로 저장
    private static File saveThumbnailToFile(Context context, Bitmap thumbnail) {
        File thumbnailFile = null;
        try {
            thumbnailFile = new File(context.getExternalCacheDir(), "temp_thumbnail.jpg");
            FileOutputStream fos = new FileOutputStream(thumbnailFile);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return thumbnailFile;
    }

    public static void openVideo(Context context, String video) {
        if (video.startsWith("https://")) {
            // 웹 기반 동영상 경로
            Intent intent = new Intent(context, YoutubeVideoPlayerActivity.class);
            String videoId = extractYoutubeVideoId(video); // VideoUtils 클래스의 메서드를 여기에 직접 구현하거나, 해당 클래스 메서드를 호출
            intent.putExtra("VIDEO_ID", videoId);
            context.startActivity(intent);

        } else if (video.startsWith("content://")) {
            // 로컬 기반 동영상 경로
            Intent intent = new Intent(context, LocalVideoPlayerActivity.class);
            intent.putExtra("videoUriString", video);
            context.startActivity(intent);
        }
    }

}
