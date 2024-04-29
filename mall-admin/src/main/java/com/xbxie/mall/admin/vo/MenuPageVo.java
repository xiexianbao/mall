package com.xbxie.mall.admin.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * created by xbxie on 2024/4/24
 */
@Data
public class MenuPageVo {
    /**
     * 菜单名
     */
    private String name;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "pageNum需要大于等于1")
    private Long pageNum;

    /**
     * 每页包含的数据条数
     */
    @Min(value = 1, message = "pageSize需要大于等于1")
    private Long pageSize;
}
