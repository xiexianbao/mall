package com.xbxie.mall.order.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class ConfirmOrderReqVo {
    /**
     * 枚举值：cart、goods
     * 判断是根据购物车还是商品生成订单数据
     */
    private String source;

    /**
     * sku id
     */
    @NotNull(message = "请输入商品id")
    private Long id;

    /**
     * 商品数量
     */
    @Min(value = 1, message = "商品数量至少为1")
    @NotNull(message = "请输入商品商品数量")
    private Integer num;
}