package com.company;

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {
        // 服务器的主机名或。
        // IP地址
        String hostName = "frp-can.top";
        // 服务器监听的端口号
        int portNumber = 34483;
        while(true){
            System.out.println("开始重新监听");
            try (Socket socket = new Socket(hostName, portNumber);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println("已连接到服务器");

                // 从标准输入读取一行数据并发送到服务器
                System.out.println("请输入要发送的消息:");
                String userInput;
                while ((userInput = stdIn.readLine()) != null) {
                    out.println(userInput); // 发送到服务器
                    System.out.println("服务器响应: " + in.readLine()); // 读取服务器的响应

                    System.out.println("请输入要发送的消息 (或输入'exit'以退出):");
                    if ("exit".equalsIgnoreCase(userInput)) {
                        break;
                    }
                }

            } catch (UnknownHostException e) {
                System.err.println("无法找到主机: " + hostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("无法获取I/O连接到 " + hostName);
                System.exit(1);
            }
        }

    }
}
