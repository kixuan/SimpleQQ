package com.xxz.qqservice.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import com.xxz.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kixuan
 * @version 1.0
 */
public class QQServer {


    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { //在静态代码块，初始化 validUsers
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("xxz", new User("xxz", "123456"));
        validUsers.put("QQ", new User("QQ", "123456"));
        validUsers.put("小马", new User("小马", "123456"));
    }

    //验证用户是否有效的方法
    private boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        if (user == null) {//说明userId没有存在validUsers 的key中
            return false;
        }
        return user.getPassword().equals(passwd);
    }

    public QQServer() {
        System.out.println("服务端启动！9999端口...");
        try {
            ServerSocket serverSocket = new ServerSocket(9999);

            while (true) {
                Socket socket = serverSocket.accept();

                // 1. 拿到客户端那边传来的User对象
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User user = (User) objectInputStream.readObject();

                // 2. checkUser，并返回一条消息告诉客户端是否登陆成功
                //2.1 先得到socket关联的对象输出流
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                Message message = new Message();

                if (checkUser(user.getUserId(), user.getPassword())) {
                    // 3. 登录成功，返回相关的消息
                    System.out.println(user.getUserId() + " 登录成功！");
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    objectOutputStream.writeObject(message);
                    // 4. 创建线程和客户端保持通信

                } else {
                    // 5. 登录失败，返回相关的消息
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    socket.close();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
