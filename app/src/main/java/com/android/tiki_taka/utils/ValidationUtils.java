package com.android.tiki_taka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.android.tiki_taka.R;

public class ValidationUtils {
    // 유효성 검사 메서드는 애플리케이션 전반에서 여러번 재사용될 수 있는 유틸리티 기능을 제공합니다.
    // 따라서, 유틸리티 클래스로 구현하는 것이 더 적합합니다. 유틸리티 클래스를 사용하면 인스턴스 생성 없이 메서드에 직접 접근하여 필요한 곳에서 유효성 검사를 쉽게 수행할 수 있습니다.
    //유틸리티 클래스로 구현할 경우, 다음과 같이 정적 메서드로 변환할 수 있습니다.

    public static boolean isValidEmail(CharSequence email) {
        return TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // 8자 이상 20자 이하, 영문과 숫자를 혼합한 기본 조건
        boolean basicCriteria = password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$");
        // 기본 조건을 만족하거나, 기본 조건을 만족한 이후 특수 문자가 추가된 경우도 허용
        boolean extendedCriteria = password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=]{8,20}$");
        return basicCriteria || extendedCriteria;
    }

}
