package com.company;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        // 设置服务器监听的端口号
        int portNumber = 1234;
        while(true) {
            System.out.println("开始重新监听");
            // 创建ServerSocket实例以监听端口
            try (ServerSocket serverSocket = new ServerSocket(portNumber);
                 Socket clientSocket = serverSocket.accept(); // 接受客户端连接
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("客户端已连接");

                // 从客户端读取数据
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("接收到客户端消息: " + inputLine);
                    out.println(inputLine); // 将收到的消息发送回客户端
                }
            } catch (IOException e) {
                System.out.println("服务器异常: " + e.getMessage());
            }
        }
    }
}
