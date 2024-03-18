package com.android.tiki_taka.models.dto;


public class ChatUser {
    private int userId;
    private int chatRoomId;

    public ChatUser(int userId, int chatRoomId) {
        this.userId = userId;
        this.chatRoomId = chatRoomId;
    }
    // ChatUser 클래스에서 소켓과 스트림을 직접 초기화하는 대신, 네트워킹을 담당하는 별도의 메서드나 클래스에서 이를 수행하는 것이 일반적이다
    // 이유 : ChatUser가 사용자의 정보를 관리하는데 집중하고, 네트워킹 로직은 분리한다

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

}

