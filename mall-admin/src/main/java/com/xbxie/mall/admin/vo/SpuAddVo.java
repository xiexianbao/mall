package com.xbxie.mall.admin.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * created by xbxie on 2024/5/17
 */
@Data
public class SpuAddVo {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 商品分类id
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品首图
     */
    private String firstImg;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 商品轮播图
     */
    private List<String> imgList;

    /**
     * 商品属性集合
     */
    private List<Attr> attrs;

    /**
     * 商品sku集合
     */
    private List<Sku> skus;

    @Data
    public static class Attr {
        /**
         * 属性名称
         */
        private String name;

        /**
         * 子属性集合
         */
        private List<Attr> children;
    }

    @Data
    public static class Sku {

        /**
         * sku名称
         */
        private String name;

        /**
         * sku价格
         */
        private BigDecimal price;

        /**
         * sku图片
         */
        private String img;

        /**
         * sku属性
         */
        private String attrs;

        /**
         * sku库存
         */
        private Integer stock;
    }
}