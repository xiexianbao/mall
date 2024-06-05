package com.xbxie.mall.order.vo;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class CreateOrderReqVo {
    /**
     * 收获地址 id
     */
    @NotNull(message = "请输入收获地址")
    private Long addressId;

    /**
     * 店铺列表
     */
    private List<Shop> shopList;

    @Data
    public static class Shop {
        /**
         * 店铺 id
         */
        private Long id;

        /**
         * 商品列表
         */
        private List<Goods> goodsList;
    }

    @Data
    public static class Goods {
        /**
         * sku id
         */
        @NotNull(message = "请输入商品id")
        private Long skuId;

        /**
         * 商品数量
         */
        @Min(value = 1, message = "商品数量至少为1")
        @NotNull(message = "请输入商品商品数量")
        private Integer skuNum;
    }
}
