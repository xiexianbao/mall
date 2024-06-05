package com.xbxie.mall.order.service;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.vo.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xbxie on 2024/5/21
 */
public interface OrderService {
    R<ConfirmOrderResVo> getConfirmInfo(ConfirmOrderReqVo confirmOrderReqVo, HttpServletRequest request);

    R<String> createOrder(CreateOrderReqVo createOrderReqVo, HttpServletRequest request);

    R<List<OrderListItemResVo>> list(OrderListReqVo orderListReqVo, HttpServletRequest request);
}
