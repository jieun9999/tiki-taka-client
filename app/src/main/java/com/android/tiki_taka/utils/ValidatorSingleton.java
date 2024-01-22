package com.android.tiki_taka.utils;

import android.text.TextUtils;

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
}
