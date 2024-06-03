package com.xbxie.mall.cart.controller;

import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import com.xbxie.mall.cart.service.CartService;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody CartAddVo cartAddVo) {
        return cartService.add(cartAddVo);
    }
}
