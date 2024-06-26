package com.xbxie.mall.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class ConfirmOrderResVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalPrice;

    private List<ShopItem> shopList;

    @Data
    public static class ShopItem {

        private Long id;

        private String name;

        private List<Goods> goodsList;
    }

    @Data
    public static class Goods {

        private Long spuId;

        private Long skuId;

        private String name;

        private String attrs;

        private String img;

        private Integer num;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private BigDecimal price;
    }
}
