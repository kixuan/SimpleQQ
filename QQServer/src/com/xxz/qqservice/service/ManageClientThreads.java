package com.xxz.qqservice.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author 韩顺平
 * @version 1.0
 * 管理和客户端通信的线程
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    //添加线程对象到 hm 集合
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    //根据userId 返回ServerConnectClientThread线程
    public static ServerConnectClientThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }


    /**
     * 遍历获取在线用户列表
     */
    public static String getOnlineUser() {
        //集合遍历 ，遍历 hashmap的key
        Iterator<String> iterator = hm.keySet().iterator();
        StringBuilder onlineUserList = new StringBuilder();
        while (iterator.hasNext()) {
            onlineUserList.append(iterator.next()).append(" ");
        }
        return onlineUserList.toString();
    }
}
