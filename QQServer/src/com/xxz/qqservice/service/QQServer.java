package com.xxz.qqservice.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import com.xxz.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import static com.xxz.qqservice.service.OffLineMessageService.offlineMap;

/**
 * @author kixuan
 * @version 1.0
 * 只处理登录请求
 */
public class QQServer {


    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static { //在静态代码块，初始化 validUsers
        validUsers.put("123", new User("123", "123123"));
        validUsers.put("200", new User("200", "123123"));
        validUsers.put("300", new User("300", "123123"));
        validUsers.put("xxz", new User("xxz", "123123"));
        validUsers.put("QQ", new User("QQ", "123123"));
        validUsers.put("小马", new User("小马", "123123"));
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
        // 启动推送新闻的线程
        new Thread(new SendNewsToAllService()).start();
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
                String userId = user.getUserId();
                if (checkUser(userId, user.getPassword())) {
                    // 3. 登录成功，返回相关的消息
                    System.out.println(userId + " 登录成功！");
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    objectOutputStream.writeObject(message);
                    // 4. 创建线程和客户端保持通信
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, userId);
                    serverConnectClientThread.start();
                    // 5. 将线程放入到集合管理，方便后期获取用户列表、发送消息等
                    ManageClientThreads.addClientThread(userId, serverConnectClientThread);

                    // 读取是否有留言
                    if (offlineMap.containsKey(userId)) {
                        boolean success = OffLineMessageService.sendOfflineMessage(userId, offlineMap);
                        if (success) {
                            OffLineMessageService.deleteOfflineMessage(userId);
                        }
                    }
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
