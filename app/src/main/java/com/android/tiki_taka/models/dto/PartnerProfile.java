package com.android.tiki_taka.models.dto;

import com.google.gson.annotations.SerializedName;

public class PartnerProfile {
    // PartnerProfile 클래스가 데이터 전송 객체(Data Transfer Object, DTO)로 사용
    // JSON 키와 Java 클래스의 변수명이 다를 경우에도 올바르게 매핑하기 위해서 @SerializedName 어노테이션을 사용
    @SerializedName("user_id")
    private int userId;
    @SerializedName("profile_image")
    private String profileImage;
    @SerializedName("gender")
    private String gender;
    @SerializedName("name")
    private String name;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("first_date")
    private String meetingDay;
    @SerializedName("agree_app_terms")
    private int agreeTerms;
    @SerializedName("agree_privacy_policy")
    private int agreePrivacy;
    @SerializedName("home_background_image")
    private String homeBackgroundImage;
    @SerializedName("profile_background_image")
    private String profileBackgroundImage;
    @SerializedName("profile_message")
    private String profileMessage;


    public PartnerProfile(int userId, String profileImage, String gender, String name, String birthday, String meetingDay,
                          boolean agreeTerms, boolean agreePrivacy) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.gender = gender;
        this.name = name;
        this.birthday = birthday;
        this.meetingDay = meetingDay;
        this.agreeTerms = agreeTerms ? 1 : 0; // true를 1로 매핑, false를 0으로 매핑
        this.agreePrivacy = agreePrivacy ? 1 : 0; // true를 1로 매핑, false를 0으로 매핑
    }

    // 게터 메서드
    public int getUserId() {return  userId;}
    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public int isAgreeTerms() {
        return agreeTerms;
    }

    public int isAgreePrivacy() {
        return agreePrivacy;
    }

    public String getProfileImage() {
        return profileImage;
    }
    public String getHomeBackgroundImage(){return homeBackgroundImage;}
    public String getProfileBackgroundImage(){return profileBackgroundImage;}
    public String getProfileMessage(){return  profileMessage;}

    // 세터 메서드
    public void setUserId(int userId){this.userId = userId;}
    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = meetingDay;
    }

    public void setAgreeTerms(int agreeTerms) {
        this.agreeTerms = agreeTerms;
    }

    public void setAgreePrivacy(int agreePrivacy) {
        this.agreePrivacy = agreePrivacy;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setHomeBackgroundImage(String homeBackgroundImage) {
        this.homeBackgroundImage = homeBackgroundImage;
    }

    public void setProfileBackgroundImage(String profileBackgroundImage) {
        this.profileBackgroundImage = profileBackgroundImage;
    }

    public void setProfileMessage(String profileMessage) {
        this.profileMessage = profileMessage;
    }
}
