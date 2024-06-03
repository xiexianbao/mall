package com.xbxie.mall.order.constant;

/**
 * created by xbxie on 2024/6/2
 */
public enum OrderStatus {
    UN_PAY(1, "待付款"),
    UN_DELIVER(2, "待发货"),
    UN_RECEIVE(3, "待收货"),
    CLOSED(4, "售后中"),
    CANCELLED(5, "已取消"),
    COMPLETED(6, "完成");

    OrderStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final int code;

    private final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
