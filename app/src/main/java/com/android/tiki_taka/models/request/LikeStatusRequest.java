package com.android.tiki_taka.models.request;

public class LikeStatusRequest {
    private int cardId;
    private int userId;
    private boolean isLiked;
    private int partnerId;

    public LikeStatusRequest(int cardId, int userId, boolean isLiked, int partnerId) {
        this.cardId = cardId;
        this.userId = userId;
        this.isLiked = isLiked;
        this.partnerId = partnerId;
    }

    // Getter and Setter methods
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }
}
