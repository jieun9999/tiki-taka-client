package com.android.tiki_taka.models.dto;

import com.google.gson.annotations.SerializedName;

public class CommentItem{
    //SerializedName 어노테이션을 사용하는 것은 서버에서 데이터를 받아올때,
    // 서버 칼럼명이 아닌, 클래스의 멤버변수 명을 그대로 사용하고 싶어서 사용하는 것임
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

    public CommentItem(int cardId, int userId, String commentText) {
        this.cardId = cardId;
        this.userId = userId;
        this.commentText = commentText;
    }

    // 코멘트 수정
    public CommentItem(int commentId,String commentText) {
        this.commentId = commentId;
        this.commentText = commentText;
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
