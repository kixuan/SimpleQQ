package com.xxz.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author kixuan
 * @version 1.0
 */
@Data
// 添加这两个注解才会生成全参构造器和无参构造器
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String mesType;//消息类型[可以在接口定义消息类型]
    private String sender;//发送者
    private String getter;//接收者
    private String content;//消息内容
    private String sendTime;//发送时间

    //进行扩展 和文件相关的成员
    private byte[] fileBytes;
    private int fileLen = 0;
    private String dest; //文件传输路径
    private String src; //源文件路径

}
