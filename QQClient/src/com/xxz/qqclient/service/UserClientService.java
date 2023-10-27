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

            // 3. 判断Message对象的类型
            if (message.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                // 3.1 如果是登录成功，就创建一个和服务器端保持通信的线程
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();

                // 4. 将线程放入到集合管理，方便后期获取用户列表、发送消息等
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                flag = true;
            } else {
                socket.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }


    /**
     * 向服务器端请求在线用户列表
     */
    public void onlineFriendList() {

        //new一个Message , 告诉服务器我们想要在线用户列表
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(user.getUserId());

        // 发送消息
        try {
            //从管理线程的集合中，通过userId, 得到这个线程对象
            ClientConnectServerThread clientConnectServerThread = ManageClientConnectServerThread.getClientConnectServerThread(user.getUserId());
            //通过这个线程得到关联的socket
            Socket socket = clientConnectServerThread.getSocket();

            // 向服务端发送消息，请求在线用户列表
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
