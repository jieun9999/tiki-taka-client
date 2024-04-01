package com.android.tiki_taka.models.request;

import com.google.gson.annotations.SerializedName;

public class ReadMessageRequest {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("message_id")
    private int messageId;

    public ReadMessageRequest(int userId, int messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
