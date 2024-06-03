package com.xbxie.mall.product.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * created by xbxie on 2024/5/17
 */
@Data
public class HomePageVo  {
    /**
     * id
     */
    private Long id;


    /**
     * 轮播图
     */
    private List<SwipeItem> swipeList;

    /**
     * 九宫格
     */
    private List<GridItem> gridList;

    /**
     * 商品列表
     */
    private List<GoodsItem> goodsList;

    @Data
    public static class SwipeItem {
        /**
         * 轮播图url
         */
        private String img;

        /**
         * 轮播图跳转地址
         */
        private String target;
    }

    @Data
    public static class GridItem {
        /**
         * 九宫格图片url
         */
        private String img;

        /**
         * 九宫格跳转地址
         */
        private String target;

        /**
         * 九宫格标题
         */
        private String title;
    }

    @Data
    public static class GoodsItem {
        /**
         * 商品id
         */
        private Long id;

        /**
         * 商品首图
         */
        private String firstImg;

        /**
         * 商品名称
         */
        private String name;

        /**
         * 商品价格
         */
        private BigDecimal price;

        /**
         * 更新时间
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         */
        private LocalDateTime updateTime;
    }
}