package com.android.tiki_taka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;


import com.android.tiki_taka.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageSingleton {

    private static ImageSingleton instance;

    private ImageSingleton() {
        // private 생성자
    }

    public static synchronized ImageSingleton getInstance() {
        if (instance == null) {
            instance = new ImageSingleton();
        }
        return instance;
    }

    // Base64 인코딩된 이미지 데이터를 byte[]로 디코딩한 후, 이미지 뷰에 설정
    public void updateImageViewWithProfileImage(String base64Image, ImageView imageView) {
        if (base64Image != null && !base64Image.isEmpty()) {
            // Base64 문자열을 byte[] 형태로 디코딩함
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

            //디코딩된 byte[]를 사용하여 Bitmap을 생성함
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // Bitmap을 ImageView에 설정합니다.
            imageView.setImageBitmap(decodedByte);
        }else {
            imageView.setImageResource(R.drawable.ph_user_circle_plus_duotone);
        }
    }


    //찍은 사진의 Bitmap과 갤러리에서 선택한 이미지의 Uri를 모두 처리하여 Base64 문자열로 변환하는 통일적인 메서드 구현
    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    //이미지 서버 전송시 데이터 형태 갖추기
    //1. 이미지 URI를 Bitmap으로 변환한 다음,
    //2. 이를 byte[] 형태로 변환하여 서버에 전송
    //3. byte[]를 Base64 문자열로 인코딩
    public String getImageBase64(Bitmap bitmap, Uri uri, Context context) {
        try {
            Bitmap finalBitmap;
            if (bitmap != null) {
                finalBitmap = bitmap; //비트맵이 존재하면, 비트맵을 그대로 쓰고
            } else if (uri != null) {
                finalBitmap = getBitmapFromUri(uri, context); //uri가 존재하면, uri를 가지고 비트맵으로 변환함
            } else {
                return null;
            }
            return convertToBase64(finalBitmap); // 공통적으로 추출한 비트맵을 가지고 base64String으로 변환함
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Uri를 가지고 비트맵으로 바꿔서, 이미지뷰를 교체하는 함수
    public void displayImageFromUri(Uri imageUri, ImageView imageView, Context context) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
