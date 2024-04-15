package com.android.tiki_taka.models.request;

import com.google.gson.annotations.SerializedName;

public class CommentRequest {
    //SerializedName 어노테이션을 사용하는 것은 서버에서 데이터를 받아올때,
    // 서버 칼럼명이 아닌, 클래스의 멤버변수 명을 그대로 사용하고 싶어서 사용하는 것임
    private int id;
    @SerializedName("comment_id")
    private int commentId;
    @SerializedName("card_id")
    private int cardId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("profile_image")
    private String userProfile;
    @SerializedName("comment_text")
    private String commentText;
    @SerializedName("created_at")
    private String createdAt;
    int partnerId;

    // 기본 생성자를 private로 선언하여 외부에서 직접 생성을 막음
    private CommentRequest(int id, int userId, String commentText, int partnerId) {
        this.id = id;
        this.userId = userId;
        this.commentText = commentText;
        this.partnerId = partnerId;
    }

    // 새 코멘트 생성을 위한 정적 팩토리 메서드
    public static CommentRequest forNewComment(int cardId, int userId, String commentText, int partnerId){
        return  new CommentRequest(cardId, userId, commentText, partnerId);
    }

    // 코멘트 수정을 위한 정적 팩토리 메서드
    public static CommentRequest forExistingComment(int commentId, int userId, String commentText, int partnerId){
        return new CommentRequest(commentId, userId, commentText, partnerId);
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
}
