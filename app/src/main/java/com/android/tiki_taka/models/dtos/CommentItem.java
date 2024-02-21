package com.android.tiki_taka.models.dtos;

import com.google.gson.annotations.SerializedName;

public class CommentItem{

    @SerializedName("comment_id")
    private int commentId;
    @SerializedName("card_id")
    private int cardId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("comment_text")
    private String commentText;
    @SerializedName("created_at")
    private String createdAt;

    public CommentItem(String commentText, int commentId, int cardId, int userId, String commentText1, String createdAt) {
        this.commentId = commentId;
        this.cardId = cardId;
        this.userId = userId;
        this.commentText = commentText1;
        this.createdAt = createdAt;
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
}
