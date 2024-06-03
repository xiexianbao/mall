package com.xbxie.mall.cart.service;

import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
public interface CartService {
    R<Void> add(CartAddVo cartAddVo);
}

