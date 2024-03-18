package com.android.tiki_taka.services;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatHandler implements Runnable {
    // 서버에서 개별 클라이언트의 연결을 처리하고, 메세지를 브로드 캐스트 합니다.

    //1. 서버가 각 클라이언트 연결을 독립적으로 처리하고,
    //2. 채팅방에 있는 다른 사용자들에게 메시지를 브로드캐스팅 (전파)

    private Socket socket;
    int userId;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public ChatHandler(Socket socket, int userId) throws IOException {
        this.socket = socket;
        this.userId = userId;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
//                chatRoom.broadcastMessage(messageFromClient, this);

            } catch (IOException e) {
                try {
                    closeConnection(socket, bufferedReader, bufferedWriter);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void sendMessage(String message) throws IOException{
        if(bufferedWriter != null){
            bufferedWriter.write(message);
            bufferedWriter.flush();
        }
    }

    public String receiveMessage() throws IOException {
        if(bufferedReader!= null){
            return bufferedReader.readLine();
        }
        return null;
    }

    //연결 종료 및 리소스 해제
    public void closeConnection(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException {
        try{
            if(socket != null){
                socket.close();
            }
            if (bufferedReader!= null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }




}
