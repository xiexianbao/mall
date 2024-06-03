package com.xbxie.mall.order.constant;

/**
 * created by xbxie on 2024/6/2
 */
public enum PayStatus {
    UN_PAY(1, "未支付"),
    PAID(2, "已支付"),
    REFUNDED(3, "已退款"),
    CLOSED(4, "已关闭");

    private final int code;

    private final String msg;

    PayStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
