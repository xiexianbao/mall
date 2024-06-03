package com.xbxie.mall.member.vo;

import lombok.Data;

/**
 * created by xbxie on 2024/5/23
 */
@Data
public class AddressVo {
    /**
     * 地址id
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String phone;

    /**
     * 省 格式：code_name
     */
    private String province;

    /**
     * 市 格式：code_name
     */
    private String city;

    /**
     * 区 格式：code_name
     */
    private String county;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 是否为默认地址，1：是，0：否
     */
    private Integer isDefault;
}
