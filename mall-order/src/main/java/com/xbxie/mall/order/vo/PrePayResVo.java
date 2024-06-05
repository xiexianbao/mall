package com.xbxie.mall.order.vo;

import lombok.Data;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class PrePayResVo {
    /**
     * 支付二维码链接
     */
    private String codeUrl;

    /**
     * 微信订单号
     */
    private String no;
}
