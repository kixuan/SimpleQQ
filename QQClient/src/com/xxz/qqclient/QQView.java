package com.xxz.qqclient;

import com.xxz.qqclient.service.FileClientService;
import com.xxz.qqclient.service.MessageClientService;
import com.xxz.qqclient.service.UserClientService;
import com.xxz.qqclient.utils.Utility;

/**
 * @author kixuan
 * @version 1.0
 * 客户端的菜单界面
 */
public class QQView {
    private boolean loop = true; //控制是否显示菜单

    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户
    private MessageClientService messageClientService = new MessageClientService();//对象是用于登录服务/注册用户
    private FileClientService fileClientService = new FileClientService();//对象是用于登录服务/注册用户


    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统.....");
    }

    private void mainMenu() {
        while (loop) {
            {
                System.out.println("===========欢迎登录SimpleQQ===========");
                System.out.println("1 登录系统");
                System.out.println("9 退出系统");
                System.out.print("请输入你的选择: ");
                // 接收用户的键盘输入
                String key = Utility.readString(1);

                //根据用户的输入，来处理不同的逻辑
                switch (key) {
                    case "1" -> {
                        System.out.print("请输入用户号: ");
                        String userId = Utility.readString(50);
                        System.out.print("请输入密  码: ");
                        String pwd = Utility.readString(50);
                        if (userClientService.checkUser(userId, pwd)) {
                            System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
                            //进入到二级菜单
                            while (loop) {
                                System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
                                System.out.println(" 1 显示在线用户列表");
                                System.out.println(" 2 群发消息");
                                System.out.println(" 3 私聊消息");
                                System.out.println(" 4 发送文件");
                                System.out.println(" 9 退出系统");
                                System.out.print("请输入你的选择: ");
                                key = Utility.readString(1);
                                switch (key) {
                                    case "1" -> userClientService.onlineFriendList();
                                    case "2" -> {
                                        System.out.println("你想要群发的话是：");
                                        String content = Utility.readString(100);
                                        messageClientService.sendMessageToAll(content, userId);
                                    }
                                    case "3" -> {
                                        System.out.print("请输入想聊天的用户号(在线): ");
                                        String getterId = Utility.readString(10);
                                        System.out.print("请输入想说的话: ");
                                        String content = Utility.readString(100);
                                        messageClientService.sendMessageToOne(content, userId, getterId);
                                    }
                                    case "4" -> {
                                        System.out.print("请输入想发送文件的用户号(在线): ");
                                        String getterId = Utility.readString(10);
                                        System.out.print("请输入想要发送的文件完整路径: ");
                                        String src = Utility.readString(100);
                                        System.out.print("请输入想要对方接收的文件完整路径: ");
                                        String dest = Utility.readString(100);
                                        fileClientService.sendFileToOne(src, dest, userId, getterId);
                                    }
                                    case "9" -> {
                                        //调用方法，给服务器发送一个退出系统的message
                                        userClientService.logout();
                                        loop = false;
                                    }
                                }
                            }
                        } else { //登录服务器失败
                            System.out.println("=========登录失败=========");
                        }
                    }
                    case "9" -> loop = false;
                }
            }
        }
    }
}
