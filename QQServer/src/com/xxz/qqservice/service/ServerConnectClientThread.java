package com.xxz.qqservice.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author kixuan
 * @version 1.0
 * 【服务端】该线程用于和客户端保持通信
 */
public class ServerConnectClientThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    private String userId;//连接到服务端的用户id


    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("this is 服务端线程，正在和" + userId + "保持通信...");
            try {
                // 1. 这个时候我们已经把User对象传到服务端了，要读取从服务器端回复的Message对象
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                // 2. 根据读取到的消息类型进行下一步操作
                switch (message.getMesType()) {
                    case MessageType.MESSAGE_GET_ONLINE_FRIEND:
                        String onlineUser = ManageClientThreads.getOnlineUser();
                        // 构建message把这个返回给客户端
                        Message message2 = new Message();
                        message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                        message2.setGetter(message.getSender());
                        message2.setContent(onlineUser);

                        // 返回给客户端
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message2);
                        System.out.println(userId + "需要在线用户列表，服务端返回成功");
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
