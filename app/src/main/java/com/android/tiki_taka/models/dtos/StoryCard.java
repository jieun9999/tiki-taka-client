package com.android.tiki_taka.models.dtos;

import com.google.gson.annotations.SerializedName;

public class StoryCard {
    // JSON 키와 Java 클래스의 변수명이 다를 경우에도 올바르게 매핑하기 위해서 @SerializedName 어노테이션을 사용
    @SerializedName("card_id")
    private int cardId;
    @SerializedName("folder_id")
    private int folderId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("profile_image")
    private String userProfile;
    @SerializedName("name")
    private String userName;
    @SerializedName("data_type")
    private String dataType;
    @SerializedName("image")
    private String image;
    @SerializedName("memo")
    private String memo;
    @SerializedName("video")
    private String video;
    @SerializedName("video_thumbnail")
    private String videoThumbnail;
    @SerializedName("user_a_likes")
    private int userALikes;
    @SerializedName("user_b_likes")
    private int userBLikes;

    // 클라이언트 측에서 createdAt과 updatedAt을 직접 만들 필요가 없는 경우라도,
    // 서버로부터 해당 정보를 받아와서 클라이언트 측에서 사용하고자 한다면 클래스의 멤버 변수로 선언해 주는 것이 좋습니다. (데이터 동기화)
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    //서버 응답에서 날짜와 시간을 문자열로 수신한 후, 클라이언트 측에서 문자열을 원하는 형식으로 파싱


    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String contents) {
        this.image = contents;
    }

    public int getUserALikes() {
        return userALikes;
    }

    public void setUserALikes(int userALikes) {
        this.userALikes = userALikes;
    }

    public int getUserBLikes() {
        return userBLikes;
    }

    public void setUserBLikes(int userBLikes) {
        this.userBLikes = userBLikes;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
