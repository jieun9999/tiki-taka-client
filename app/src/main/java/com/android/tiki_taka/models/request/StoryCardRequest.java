package com.android.tiki_taka.models.request;


import java.util.ArrayList;

public class StoryCardRequest {
    // 서버에 보내는 것
    // userId, 스토리 카드 리스트(uris), 스토리 폴더 제목(title), 스토리 폴더 장소(location), 썸네일(displayImage), 댓글(comments), 파트너 아이디(partnerId)
    private int cardId;
    private int userId;
    private int folderId;
    private ArrayList<String> uris;
    private String text; //메모의 텍스트
    private String title;
    private String location;
    private String displayImage;
    private ArrayList<String> comments;
    int partnerId;

    // 메모 스토리 카드
    public StoryCardRequest(int userId, String text, String title, String location, int partnerId) {
        this.userId = userId;
        this.text = text;
        this.title = title;
        this.location = location;
        this.partnerId = partnerId;
    }

    // 기존 폴더에 메모 스토리 카드 추가
    public StoryCardRequest(int userId, int folderId, String text, int partnerId) {
        this.userId = userId;
        this.folderId = folderId;
        this.text = text;
        this.partnerId = partnerId;
    }

    // 메모 카드 내용 편집
    public StoryCardRequest(int cardId, String text, int partnerId, int userId) {
        this.cardId = cardId;
        this.text = text;
        this.partnerId = partnerId;
        this.userId = userId;
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

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
