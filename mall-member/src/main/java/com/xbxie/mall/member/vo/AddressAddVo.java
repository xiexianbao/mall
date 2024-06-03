package com.xbxie.mall.member.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * created by xbxie on 2024/5/23
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressAddVo {
    /**
     * 姓名
     */
    @NotBlank(message = "请输入姓名")
    private String name;

    /**
     * 电话
     */
    @NotBlank(message = "请输入电话")
    private String phone;

    /**
     * 省 格式：code_name
     */
    @NotBlank(message = "请输入省")
    private String province;

    /**
     * 市 格式：code_name
     */
    @NotBlank(message = "请输入市")
    private String city;

    /**
     * 区 格式：code_name
     */
    @NotBlank(message = "请输入区")
    private String county;

    /**
     * 详细地址
     */
    @NotBlank(message = "请输入详细地址")
    private String detail;

    /**
     * 是否为默认地址，1：是，0：否
     */
    private Integer isDefault;
}
