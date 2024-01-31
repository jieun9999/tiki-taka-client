package com.android.tiki_taka.models.dtos;

import java.time.LocalDateTime;

public class StoryCardDto {
    private int cardId;
    private int folderId;
    private int userId;
    private String contents; //이미지라면 url이, 메모라면 텍스트가 들어감
    private int userGood;
    private int partnerGood;

    // 클라이언트 측에서 createdAt과 updatedAt을 직접 만들 필요가 없는 경우라도,
    // 서버로부터 해당 정보를 받아와서 클라이언트 측에서 사용하고자 한다면 클래스의 멤버 변수로 선언해 주는 것이 좋습니다. (데이터 동기화)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public StoryCardDto(int cardId, int folderId, int userId, String contents, int userGood, int partnerGood) {
        this.cardId = cardId;
        this.folderId = folderId;
        this.userId = userId;
        this.contents = contents;
        this.userGood = userGood;
        this.partnerGood = partnerGood;
    }

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

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getUserGood() {
        return userGood;
    }

    public void setUserGood(int userGood) {
        this.userGood = userGood;
    }

    public int getPartnerGood() {
        return partnerGood;
    }

    public void setPartnerGood(int partnerGood) {
        this.partnerGood = partnerGood;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
