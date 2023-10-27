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
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String userId;
    private String password;

}
