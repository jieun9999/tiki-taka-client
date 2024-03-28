package com.android.tiki_taka.services;

import android.util.Log;

import com.android.tiki_taka.listeners.MessageListener;

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

    public ChatClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
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
        }
    }

    public void listenMessage() throws IOException {

        while (socket.isConnected()){
            try {
                String msgFromGroupChat = bufferedReader.readLine();
//                Log.d("msgFromGroupChat", msgFromGroupChat);
                // {"createdAt":"2024-03-20 13:00:46","content":"으"}

                // 메세지 수신 시 콜백 호출
                if(messageListener != null){
                    messageListener.onMessageReceived(msgFromGroupChat);
                }

            } catch (IOException e) {
               closeConnection();
            }
        }
    }


    //연결 종료 및 리소스 해제
    //Closing a socket will also close the socket's InputStream and OutputStream:
    public void closeConnection() throws IOException {
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("closeConnection", "Error closing Socket", e);
            }
        }
    }
}
