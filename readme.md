# SimpleQQ

## 用户登录

1. 建立通用类User、Message、MessageType

   1. User类的话就Id + password
   2. Message类：mesType、sender、getter、content、sendTime
   3. MessageType类：返回服务端的信息
2. 创建QQServer【服务端】

   1. new一个ServerSocket
   2. 拿到客户端那边传来的User
   3. checkUser，并返回一条消息告诉客户端是否登陆成功
   4. 登录成功，创建线程和客户端保持通信
   4. 失败直接close
3. 创建UserClientService【客户端】
   1. new一个Socket连接到服务器端，并且发送user对象
   2. 读取从服务器端回复的Message对象
   3. 判断Message对象的类型
      1. 登录成功，创建线程和服务端保持通信
      2. 失败直接close
4. 创建对应的启动类
   1. 服务端QQFrame直接启动QQServer
   2. 客户端QQView还要画界面，传对象

![image-20231027150731286](https://cdn.jsdelivr.net/gh/kixuan/PicGo/images/image-20231027150731286.png)

## 拉取在线登录用户

【客户端】

1. 首先UserClientService写一个onlineFriendList方法

   1. 先new一条Message告诉服务器我们想要用户列表
   2. 发送消息的时候注意是要在userId对应线程的socket发送，所以我们还需要创建一个线程管理类
2. 创建**线程管理类**ClientConnectServerThread

   1. 该线程需要持有Socket
   1. 这个类用于处理从服务端返回的消息，具体线程就是userId的对应线程
3. 再新建一个**线程集合管理类**ManageClientThreads
   1. 创建HashMap，userId作key，线程管理类ClientConnectServerThread作value
   2. 添加add方法和get方法

4. QQView添加switch分支

【服务端】一样的啦

1. 首先创建**线程管理类**ServerConnectClientThread，和客户端保持通信
   1. 该线程需要持有Socket和userId
   2. 这个类用于处理从客户端端拿到的消息，并返回新消息给客户端。
   3. 添加switch分支处理不同的客户端消息类型
2. 再新建一个**线程集合管理类**ManageClientThreads
   1. 创建HashMap，userId作key，线程管理类ServerConnectClientThread作value
   2. 添加add方法和get方法
   3. 添加getOnlineUser方法遍历获取在线用户

## 退出系统

首先分析一下原因：之前的退出只是退出main方法，还有一个线程ClientConnectServerThread在继续运行，所以整个进程是没有退出的

所以现在我们就要在客户端调用System.exit(0)退出，同时告诉服务端那哪个线程退出了（easy啦( •̀ ω •́ )y

![image-20231027191718649](https://cdn.jsdelivr.net/gh/kixuan/PicGo/images/image-20231027191718649.png)

【客户端】

1. QQView增加switch分支处理MESSAGE_CLIENT_EXIT，实现logout方法
2. userClientService添加logout方法
   1. 和服务端发条消息
   2. 调用System.exit(0)退出


【服务端】

1. ServerConnectClientThread添加switch分支处理MESSAGE_CLIENT_EXIT

   1. 从集合ManageClientThreads中删除该线程
   2. 关闭socket

2. ManageClientThreads添加删除线程方法

## 私聊

也很简单滴啦，逻辑就是接收setter的message，在服务端转发到getter的socket，getter的客户端客户端收到这条message再进行处理就行【重点在于服务端由setter的socket转到getter的socket-->
所以为什么一定要线程管理和线程管理集合】

![image-20231027201324043](https://cdn.jsdelivr.net/gh/kixuan/PicGo/images/image-20231027201324043.png)

【客户端】

1. 新增MessageClientService类，新增sendMessageToOne方法

    1. 构建message
    2. 发送给服务端
2. ClientConnectServerThread添加switch分支处理MESSAGE_RET_ONLINE_FRIEND
2. QQView添加switch分支处理MESSAGE_RET_ONLINE_FRIEND，实现sendMessageToOne方法

【服务端】

1. ServerConnectClientThread添加switch分支处理MESSAGE_RET_ONLINE_FRIEND

    1. 根据接收到的message获取getter的id及对应线程（使用ManageClientThreads啦）

    2. 得到对应socket的对象输出流，将message对象转发给指定的客户端
