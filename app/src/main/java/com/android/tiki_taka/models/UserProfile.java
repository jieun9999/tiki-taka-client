package com.android.tiki_taka.models;

public class UserProfile {
    private int userId;
    private byte[] profileImage; // 이미지를 저장할 바이트 배열
    private String gender;
    private String name;
    private String birthday;
    private String meetingDay;
    private boolean agreeTerms;
    private boolean agreePrivacy;

    public UserProfile(int userId, byte[] profileImage, String gender, String name, String birthday, String meetingDay, boolean agreeTerms, boolean agreePrivacy) {
        this.userId = userId;
        this.profileImage = profileImage;
        this.gender = gender;
        this.name = name;
        this.birthday = birthday;
        this.meetingDay = meetingDay;
        this.agreeTerms = agreeTerms;
        this.agreePrivacy = agreePrivacy;
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

    public boolean isAgreeTerms() {
        return agreeTerms;
    }

    public boolean isAgreePrivacy() {
        return agreePrivacy;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

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

    public void setAgreeTerms(boolean agreeTerms) {
        this.agreeTerms = agreeTerms;
    }

    public void setAgreePrivacy(boolean agreePrivacy) {
        this.agreePrivacy = agreePrivacy;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }
}
