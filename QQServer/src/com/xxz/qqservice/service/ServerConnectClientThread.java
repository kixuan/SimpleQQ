package com.xxz.qqservice.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import lombok.Getter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author kixuan
 * @version 1.0
 * 【服务端】该线程用于和客户端保持通信
 */
@Getter
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
        String onlineUser = "";
        label:
        while (true) {
            System.out.println("this is 服务端线程，正在和" + userId + "保持通信...");
            try {
                // 1. 这个时候我们已经把User对象传到服务端了，要读取从服务器端回复的Message对象
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                // 2. 根据读取到的消息类型进行下一步操作
                switch (message.getMesType()) {
                    // 显示在线列表用户信息
                    case MessageType.MESSAGE_GET_ONLINE_FRIEND:
                        onlineUser = ManageClientThreads.getOnlineUser();
                        // 构建message把这个返回给客户端
                        Message message2 = new Message();
                        message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                        message2.setGetter(message.getSender());
                        message2.setContent(onlineUser);

                        // 返回给客户端
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message2);
                        System.out.println(userId + "需要【在线用户列表】，服务端返回成功");
                        break;

                    // 私聊
                    case MessageType.MESSAGE_COMM_MES:
                        System.out.println(userId + "对 " + message.getGetter() + "【私聊】");
                        // 然后就要把这条消息转给getter服务器端的socket
                        // 根据message获取getter的id及对应线程
                        ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getClientConnectServerThread(message.getGetter());
                        // 如果该线程不存在，则给出提示
                        if (serverConnectClientThread == null) {
                            System.out.println(message.getGetter() + "不在线，离线留言功能暂未实现");
                            break;
                        }
                        // 得到对应socket的对象输出流，将message对象转发给指定的客户端
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message);
                        break;

                    // 群发
                    case MessageType.MESSAGE_TO_ALL_MES:
                        System.out.println(userId + "【群发】");
                        String[] onlineUsers = ManageClientThreads.getOnlineUser().split(" ");
                        for (String user : onlineUsers) {
                            // 排除群发消息的这个用户
                            if (!user.equals(message.getSender())) {
                                // 根据message获取getter的id及对应线程
                                serverConnectClientThread = ManageClientThreads.getClientConnectServerThread(user);
                                // 得到对应socket的对象输出流，将message对象转发给指定的客户端
                                ObjectOutputStream oos2 = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                                oos2.writeObject(message);
                            }
                        }
                        break;

                    //  退出系统
                    case MessageType.MESSAGE_CLIENT_EXIT:
                        System.out.println(userId + " 【退出系统】");
                        // 从集合中删除该线程
                        ManageClientThreads.removeServerConnectClientThread(userId);
                        // 注意这个socket关闭的就是这个线程的socket
                        socket.close();
                        // 退出线程（退出while）
                        break label;
                    default:
                        System.out.println("其他类型的message , 暂时不处理");
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
