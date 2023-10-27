package com.xxz.qqclient.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author kixuan
 * @version 1.0
 * 【客户端】用于处理服务器返回的消息
 */
@Getter
public class ClientConnectServerThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("this is 【客户端】：处理服务器返回的消息线程");
            try {
                // 1. 这个时候我们已经把User对象传到服务端了，要读取从服务器端回复的Message对象
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                // 2. 根据读取到的消息类型进行下一步操作
                switch (message.getMesType()) {
                    //显示在线列表用户信息
                    case MessageType.MESSAGE_RET_ONLINE_FRIEND -> {
                        System.out.println("\n=======当前在线用户列表========");
                        String[] onlineUsers = message.getContent().split(" ");
                        for (String onlineUser : onlineUsers) {
                            System.out.println("用户: " + onlineUser);
                        }
                    }
                    case MessageType.MESSAGE_COMM_MES -> {
                        System.out.println("有人私聊你啦！" + message.getSender() + " 对你说: " + message.getContent());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
