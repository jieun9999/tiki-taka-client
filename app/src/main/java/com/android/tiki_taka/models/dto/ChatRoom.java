package com.android.tiki_taka.models.dto;

import com.android.tiki_taka.services.ChatHandler;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    @SerializedName("room_id")
    private int roomId;
    @SerializedName("user1_id")
    private int user1Id;
    @SerializedName("user2_id")
    private int user2Id;
    @SerializedName("created_at")
    private String createdAt;
    private List<ChatHandler> userHandlers = new ArrayList<>();
    // 채팅방의 모든 사용자 관리

    public ChatRoom(int user1Id, int user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public synchronized void broadcastMessage(String message, ChatHandler sender) throws IOException {
        for (ChatHandler userHandler : userHandlers){
            if(userHandler != sender){
                userHandler.sendMessage(message);
            }
        }
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
