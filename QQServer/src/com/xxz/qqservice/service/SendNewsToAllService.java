package com.xxz.qqservice.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;
import com.xxz.qqclient.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author kixuan
 * @version 1.0
 */
public class SendNewsToAllService implements Runnable {
    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        while (true) {
            System.out.println("请输入服务器要推送的新闻/消息[exit退出]:");
            String news = Utility.readString(100);
            if ("exit".equals(news)) {
                break;
            }

            // 构建一个消息，群发消息
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器【推送新闻】：" + news);

            // 遍历当前所有的通信线程，得到socket，并发送message
            String[] onlineUsers = ManageClientThreads.getOnlineUser().split(" ");
            for (String user : onlineUsers) {
                try {
                    // 根据message获取getter的id及对应线程
                    ServerConnectClientThread serverConnectClientThread = ManageClientThreads.getClientConnectServerThread(user);
                    // 得到对应socket的对象输出流，将message对象转发给指定的客户端
                    ObjectOutputStream oos = null;
                    oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
