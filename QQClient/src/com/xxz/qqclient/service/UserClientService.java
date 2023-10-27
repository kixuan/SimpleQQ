package com.xxz.qqclient.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import com.xxz.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author kixuan
 * @version 1.0
 */
public class UserClientService {
    private User user = new User();

    private Socket socket;

    public boolean checkUser(String userId, String pwd) {
        boolean flag = false;
        user.setUserId(userId);
        user.setPassword(pwd);

        try {
            // 1. 连接到服务器端，并且发送user对象
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(user);

            // 2. 读取从服务器端回复的Message对象
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) objectInputStream.readObject();

            // 3. 判断Message对象的类型，如果是登录成功，就创建一个和服务器端保持通信的线程
            if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                // ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                // clientConnectServerThread.start();
                flag = true;
            } else {
                socket.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }
}
