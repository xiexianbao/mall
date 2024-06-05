package com.xbxie.mall.cart.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * created by xbxie on 2024/6/3
 */
@Data
public class ChangeSelectReqVo {
    /**
     * 购物车id
     */
    private Long id;

    /**
     * 商品选择状态 选中或不选中
     */
    private Boolean selected;
}
