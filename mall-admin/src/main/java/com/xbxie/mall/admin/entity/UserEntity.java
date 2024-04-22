package com.xbxie.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * created by xbxie on 2024/4/20
 */
@Data
@TableName("ums_user")
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.AUTO)
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

    /**
     * 账号状态 0：禁用，1：启用
     */
    private Integer status;

    /**
     * 是否删除 0：未删除，1：已删除
     */
    private Integer isDel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
