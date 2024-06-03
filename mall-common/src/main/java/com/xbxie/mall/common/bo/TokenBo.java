package com.xbxie.mall.common.bo;

import lombok.Data;

/**
 * created by xbxie on 2024/5/23
 */
@Data
public class TokenBo {
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
}
