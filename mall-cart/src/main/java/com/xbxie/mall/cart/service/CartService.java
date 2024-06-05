package com.xbxie.mall.cart.service;

import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.cart.vo.CartInfoResVo;
import com.xbxie.mall.cart.vo.ChangeQuantityReqVo;
import com.xbxie.mall.cart.vo.ChangeSelectReqVo;
import com.xbxie.mall.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
public interface CartService {
    R<Void> add(CartAddVo cartAddVo, HttpServletRequest request);

    R<CartInfoResVo> info(HttpServletRequest request);

    R<Void> changeSelect(List<ChangeSelectReqVo> changeSelectReqVos);

    R<Void> del(Long id);

    R<Void> changeQuantity(ChangeQuantityReqVo changeQuantityReqVos);
}

