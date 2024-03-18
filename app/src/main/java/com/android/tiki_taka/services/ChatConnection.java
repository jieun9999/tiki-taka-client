package com.android.tiki_taka.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatConnection {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // 네트워크 연결을 담당하는 메서드
    public void setupConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public String receiveMessage() throws IOException {
        if(bufferedReader!= null){
            return bufferedReader.readLine();
        }
        return null;
    }

    public void sendMessage(String message) throws IOException{
        if(bufferedWriter != null){
            bufferedWriter.write(message);
            bufferedWriter.flush();
        }
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
