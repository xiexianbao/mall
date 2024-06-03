package com.xbxie.mall.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class ConfirmOrderResVo {
    private String name;

    private String shopName;

    private String attrs;

    private String img;

    private Integer num;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
