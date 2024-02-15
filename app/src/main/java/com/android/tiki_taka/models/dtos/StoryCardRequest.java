package com.android.tiki_taka.models.dtos;


import java.util.ArrayList;

public class StoryCardRequest {
    // 서버에 보내는 것
    // userId, 스토리 카드 리스트(uris), 스토리 폴더 제목(title), 스토리 폴더 장소(location), 썸네일(displayImage)
    private int userId;
    private ArrayList<String> uris;
    private String title;
    private String location;
    private String displayImage;

    public StoryCardRequest(int userId, ArrayList<String> uris, String title, String location, String displayImage) {
        this.userId = userId;
        this.uris = uris;
        this.title = title;
        this.location = location;
        this.displayImage = displayImage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<String> uris) {
        this.uris = uris;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }
}
