package com.android.tiki_taka.models.dto;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("message_id")
    private int messageId;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("profile_image")
    private String profileImageUrl;
    // 클라이언트 상으로 는 Message 클래스에 프로필 이미지 필드를 가지고 있는것이 편리
    // (message 테이블에는 프로필 이미지 칼럼이 존재하지 않더라도)

    @SerializedName("room_id")
    private int chatRoomId;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

    private boolean isSent; //사용자가 메세지를 보냈는지 여부

    private boolean dateMarker;



    // 받은 메세지를 보여줄때 생성
    //  profileImageUrl는 db table에서 가져옴
    //  content, createdAt는 서버 소켓에서 가져옴
    public Message(String profileImageUrl, String createdAt, String content){
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
        this.content = content;
    }

    //날짜 표시 객체
    public Message(String createdAt) {
        this.createdAt = createdAt;
        this.dateMarker = true;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isDateMarker() {
        return dateMarker;
    }

    public void setDateMarker(boolean dateMarker) {
        this.dateMarker = dateMarker;
    }
}
