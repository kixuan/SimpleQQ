package com.xxz.qqclient.service;

import com.xxz.common.Message;
import com.xxz.common.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author kixuan
 * @version 1.0
 */
public class FileClientService {

    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
        // 发送message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        // 读取文件
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            // 将文件读取到字节数组中
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println(senderId + " 给 " + getterId + " 发送文件: " + " 到对方的电脑的目录 " + dest);

        //发送message给服务端
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
