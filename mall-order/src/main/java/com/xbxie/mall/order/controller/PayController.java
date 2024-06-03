package com.xbxie.mall.order.controller;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.service.PayService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * created by xbxie on 2024/5/31
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Resource
    private PayService payService;

    @PostMapping("/status/{sn}")
    public R<Integer> wxPayStatus(@PathVariable("sn") String sn) {
        return payService.wxPayStatus(sn);
    }
}
