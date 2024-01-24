package com.android.tiki_taka.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.android.tiki_taka.R;

public class ValidatorSingleton {
    //싱글톤 패턴이란?
    // 앱 내에 클래스의 인스턴스가 딱 1개만 존재하도록 보장하는 것
    // 이 패턴은 전역 변수를 사용하지 않고 객체에 전역접근을 제공하는 방법
    // 여러곳에서 공유되는 리소스나 서비스에 대한 중앙관리가 필요할 떄 사용

    //싱글톤 인스턴스를 저장하기 위한 private static 변수
    private static final ValidatorSingleton instance = new ValidatorSingleton();

    //생성자를 private으로 선언하여 외부에서의 인스턴스화를 방지
    private ValidatorSingleton() {}

    //싱글톤 인스턴스에 접근하기 위한 public static 메서드
    public static ValidatorSingleton getInstance(){
        return instance;
    }

    public boolean isValidEmail(CharSequence email) {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        return !password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$");
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

}
