package com.android.tiki_taka.models.dto;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("message_id")
    private int messageId;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("room_id")
    private int chatRoomId;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

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
}
