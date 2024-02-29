package com.android.tiki_taka.models.request;


import java.util.ArrayList;

public class StoryCardRequest {
    // 서버에 보내는 것
    // userId, 스토리 카드 리스트(uris), 스토리 폴더 제목(title), 스토리 폴더 장소(location), 썸네일(displayImage), 댓글(comments)
    private int userId;
    private ArrayList<String> uris;
    private String text; //메모의 텍스트
    private String title;
    private String location;
    private String displayImage;
    private ArrayList<String> comments;

    // 이미지(사진, 카메라) 스토리 카드
    public StoryCardRequest(int userId, ArrayList<String> uris, String title, String location, String displayImage, ArrayList<String> comments) {
        this.userId = userId;
        this.uris = uris;
        this.title = title;
        this.location = location;
        this.displayImage = displayImage;
        this.comments = comments;
    }

    // 메모 스토리 카드
    public StoryCardRequest(int userId, String text, String title, String location) {
        this.userId = userId;
        this.text = text;
        this.title = title;
        this.location = location;
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

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
