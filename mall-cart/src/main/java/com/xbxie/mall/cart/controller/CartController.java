package com.xbxie.mall.cart.controller;

import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.cart.vo.CartInfoResVo;
import com.xbxie.mall.cart.vo.ChangeQuantityReqVo;
import com.xbxie.mall.cart.vo.ChangeSelectReqVo;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import com.xbxie.mall.cart.service.CartService;
import java.util.List;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody CartAddVo cartAddVo, HttpServletRequest request) {
        return cartService.add(cartAddVo, request);
    }

    @PostMapping("/info")
    public R<CartInfoResVo> info(HttpServletRequest request) {
        return cartService.info(request);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return cartService.del(id);
    }

    @PostMapping("/changeSelect")
    public R<Void> changeSelect(@RequestBody List<ChangeSelectReqVo> changeSelectReqVos) {
        return cartService.changeSelect(changeSelectReqVos);
    }

    @PostMapping("/changeQuantity")
    public R<Void> changeQuantity(@Validated @RequestBody ChangeQuantityReqVo changeQuantityReqVo) {
        return cartService.changeQuantity(changeQuantityReqVo);
    }
}
