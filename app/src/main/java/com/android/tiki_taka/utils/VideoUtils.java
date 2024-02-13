package com.android.tiki_taka.utils;

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

}
