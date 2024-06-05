package com.xbxie.mall.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class OrderListItemResVo {

    private Long id;

    private List<ShopItem> shopList;

    @Data
    public static class ShopItem {

        private String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal price;

        private List<Goods> goodsList;
    }

    @Data
    public static class Goods {

        private Long id;

        private String name;

        private String attrs;

        private String img;

        private Integer num;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal price;
    }
}
