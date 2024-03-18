package com.android.tiki_taka.config;

import com.android.tiki_taka.services.ChatHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws IOException {
        // 클라이언트가 처음 연결을 시도할 때 보낸 userId를 기반으로 각 클라이언트의 ChatHandler 인스턴스를 생성하고 관리

        //장점
        //1. 사용자 식별 :  userId를 통해 연결된 각 클라이언트를 구별
        //2. 유연한 메세지 처리 : userId를 사용하여 특정 사용자에게 메시지를 보내거나, 브로드캐스트하기에 종흠

        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String userIdString = bufferedReader.readLine();
                int userId = Integer.parseInt(userIdString.trim());
                ChatHandler userHandler = new ChatHandler(socket, userId);
                Thread thread = new Thread(userHandler);
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
