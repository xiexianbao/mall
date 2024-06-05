package com.xbxie.mall.order.vo;

import lombok.Data;

/**
 * created by xbxie on 2024/5/21
 */
@Data
public class PrePayReqVo {
    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 微信订单号
     */
    private String wxNo;
}
