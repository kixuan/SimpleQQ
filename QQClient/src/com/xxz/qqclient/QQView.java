package com.xxz.qqclient;

import com.xxz.qqclient.service.UserClientService;
import com.xxz.qqclient.utils.Utility;

/**
 * @author kixuan
 * @version 1.0
 * 客户端的菜单界面
 */
public class QQView {

    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService();//对象是用于登录服务/注册用户


    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端退出系统.....");
    }

    private void mainMenu() {
        System.out.println("===========欢迎登录SimpleQQ===========");
        System.out.println("1 登录系统");
        System.out.println("9 退出系统");
        System.out.print("请输入你的选择: ");
        key = Utility.readString(1);

        //根据用户的输入，来处理不同的逻辑
        switch (key) {
            case "1":
                System.out.print("请输入用户号: ");
                String userId = Utility.readString(50);
                System.out.print("请输入密  码: ");
                String pwd = Utility.readString(50);

                if (userClientService.checkUser(userId, pwd)) {
                    System.out.println("===========欢迎 (用户 " + userId + " 登录成功) ===========");
                    //进入到二级菜单
                    System.out.println("\n=========网络通信系统二级菜单(用户 " + userId + " )=======");
                    System.out.println("\t\t 1 显示在线用户列表");
                    System.out.println("\t\t 2 群发消息");
                    System.out.println("\t\t 3 私聊消息");
                    System.out.println("\t\t 4 发送文件");
                    System.out.println("\t\t 9 退出系统");
                    System.out.print("请输入你的选择: ");
                    key = Utility.readString(1);
                } else { //登录服务器失败
                    System.out.println("=========登录失败=========");
                }
                break;
            case "9":
                break;
        }
    }
}
