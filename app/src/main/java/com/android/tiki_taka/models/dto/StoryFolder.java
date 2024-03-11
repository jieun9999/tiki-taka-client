package com.android.tiki_taka.models.dto;

import com.google.gson.annotations.SerializedName;

public class StoryFolder {
    // JSON 키와 Java 클래스의 변수명이 다를 경우에도 올바르게 매핑하기 위해서 @SerializedName 어노테이션을 사용
    @SerializedName("folder_id")
    private int folderId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("data_type")
    private String dataType;
    @SerializedName("display_image")

    private String displayImage;
    @SerializedName("title")
    private String title;
    @SerializedName("location")
    private String location;

    // 클라이언트 측에서 createdAt과 updatedAt을 직접 만들 필요가 없는 경우라도,
    // 서버로부터 해당 정보를 받아와서 클라이언트 측에서 사용하고자 한다면 클래스의 멤버 변수로 선언해 주는 것이 좋습니다. (데이터 동기화)
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;


    public StoryFolder(int folderId, String displayImage, String title, String location) {
        this.folderId = folderId;
        this.displayImage = displayImage;
        this.title = title;
        this.location = location;
    }


    public static StoryFolder fromDetails(int folderId, String displayImage, String title, String location){
        return new StoryFolder(folderId, displayImage, title, location);
    }


    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


}