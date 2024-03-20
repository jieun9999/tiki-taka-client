package com.android.tiki_taka.services;

import com.android.tiki_taka.listeners.MessageListener;
import com.android.tiki_taka.models.dto.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {
    // 클라이언트에서 서버로 데이터를 전송하고, 서버로부터 데이터를 받기 위한 연결을 관리합니다.

    //1.서버와의 소켓 연결을 설정합니다.
    //2.서버로 메시지를 전송합니다 (sendMessage 메소드).
    //3.서버로부터 오는 메시지를 수신합니다.
    //4.네트워크 연결 관련 예외 처리를 수행합니다.

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private MessageListener messageListener;

    public ChatClient(String host, int port, MessageListener messageListener) throws IOException {
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        messageListener = messageListener;
    }

    public void sendUserId(int userId){
        try {
            bufferedWriter.write(String.valueOf(userId));// userId를 String으로 변환하여 전송
            bufferedWriter.newLine(); // 메시지의 끝을 나타냄
            bufferedWriter.flush(); // 버퍼에 있는 데이터를 즉시 전송
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void sendMessage(String data){
        try {
            bufferedWriter.write(data);
            bufferedWriter.newLine(); // 메시지의 끝을 나타냄
            bufferedWriter.flush(); // 버퍼에 있는 데이터를 즉시 전송
        } catch (IOException e) {
            e.printStackTrace();
            // 오류 처리 로직 (예: 연결 종료, 재시도, 로깅 등)
        }
    }

    public void listenMessage() throws IOException {
        String msgFromGroupChat;
        while (socket.isConnected()){
            try {
                msgFromGroupChat = bufferedReader.readLine();
                final String message = msgFromGroupChat;

                // 메세지 수신 시 콜백 호출
                if(messageListener != null){
                    messageListener.onMessageReceived(message);
                }

            } catch (IOException e) {
               closeConnection();
            }
        }
    }

    private void updateUI(String createdAt, String message){
        //리사이클러뷰에 메세지 추가
        Message newMessage = new Message("파트너의 프로필", createdAt, message);
    }


    //연결 종료 및 리소스 해제
    public void closeConnection() throws IOException {
        if(bufferedReader != null){
            bufferedReader.close();
        }
        if(bufferedWriter != null){
            bufferedReader.close();
        }
        if(socket != null){
            socket.close();
        }
    }
}
