package com.xxz.qqservice.service;

import com.xxz.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kixuan
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class OffLineMessageService {
    //key->getterId value->contents
    public static ConcurrentHashMap<String, ArrayList> offlineMap = new ConcurrentHashMap();

    public static ConcurrentHashMap<String, ArrayList> getOfflineMap() {
        return offlineMap;
    }

    public static void setOfflineMap(ConcurrentHashMap<String, ArrayList> offlineMap) {
        OffLineMessageService.offlineMap = offlineMap;
    }

    //将离线消息存入到集合中
    //编写getter不在线 并将离线消息添加到offlineMap
    public static void addOfflineMap(Message message) {
        //如果getter不存在 那么创建一个ArrayList 并将message放入
        if (!offlineMap.containsKey(message.getGetter())) {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(message);
            offlineMap.put(message.getGetter(), messages);
        } else {
            ArrayList arrayList = offlineMap.get(message.getGetter());
            arrayList.add(message);
        }
        System.out.println(offlineMap.get(message.getGetter()));
        System.out.println("离线消息已经存放在offlineMap中");

    }

    //编写方法判断user是否存在于offlineMap中 如果存在 就获取对应getter的socket 将ArrayList中的所有内容发送
    public static boolean sendOfflineMessage(String userId, ConcurrentHashMap offlineMap) {
        if (offlineMap.containsKey(userId)) {
            try {
                ArrayList<Message> arrayList = (ArrayList<Message>) offlineMap.get(userId);
                OutputStream os = ManageClientThreads.getClientConnectServerThread(userId).getSocket().getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                for (Message message : arrayList) {
                    oos.writeObject(message);
                }
                System.out.println("发送成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            System.out.println("发送失败");
            return false;
        }
    }

    //将离线消息从offlineMap删除
    public static void deleteOfflineMessage(String getterId) {
        ArrayList remove = offlineMap.remove(getterId);
        System.out.println("删除离线消息成功" + remove);
    }
}
