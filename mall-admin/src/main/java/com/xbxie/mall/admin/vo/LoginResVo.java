package com.xbxie.mall.admin.vo;

import lombok.Data;


/**
 * created by xbxie on 2024/5/13
 */
@Data
public class LoginResVo {
    /**
     * token
     */
    private String token;


    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 账号密码
     */
    private String password;
}
