package com.xbxie.mall.order.controller;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.service.OrderService;
import com.xbxie.mall.order.vo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xbxie on 2024/5/21
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @PostMapping("/confirmInfo")
    public R<ConfirmOrderResVo> getConfirmInfo(@Validated @RequestBody ConfirmOrderReqVo confirmOrderReqVo) {
        return orderService.getConfirmInfo(confirmOrderReqVo);
    }

    @PostMapping("/create")
    public R<CreateOrderResVo> createOrder(@Validated @RequestBody CreateOrderReqVo createOrderReqVo, HttpServletRequest request) {
        return orderService.createOrder(createOrderReqVo, request);
    }

    @PostMapping("/list")
    public R<List<OrderListItemResVo>> list(OrderListReqVo orderListReqVo, HttpServletRequest request) {
        return orderService.list(orderListReqVo, request);
    }
}
