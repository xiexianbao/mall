package com.xbxie.mall.cart.service.impl;

import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.common.entity.CommonCartEntity;
import com.xbxie.mall.common.service.CommonCartService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xbxie.mall.cart.service.CartService;

import javax.annotation.Resource;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
@Service("cartService")
public class CartServiceImpl implements CartService {
    @Resource
    private CommonCartService commonCartService;

    @Override
    public R<Void> add(CartAddVo cartAddVo) {
        Long skuId = cartAddVo.getSkuId();
        Integer quantity = cartAddVo.getQuantity();

        // 商品不存在
        // 商品库存不足

        CommonCartEntity commonCartEntity = new CommonCartEntity();
        BeanUtils.copyProperties(cartAddVo, commonCartEntity);

        if (!commonCartService.save(commonCartEntity)) {
            return R.fail("加入购物车失败");
        }

        return R.success("加入购物车成功");
    }
}