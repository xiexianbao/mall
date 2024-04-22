package com.xbxie.mall.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddVo {
    /**
     * 用户名
     */
    @NotBlank(message = "请输入用户名")
    private String name;

    /**
     * 用户账号
     */
    @NotBlank(message = "请输入用户账号")
    private String account;

    /**
     * 账号密码
     */
    @NotBlank(message = "请输入账号密码")
    private String password;

    /**
     * 账号状态 0：禁用，1：启用
     */
    private Integer status;
}
