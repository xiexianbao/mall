package com.xbxie.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xbxie.mall.cart.vo.CartAddVo;
import com.xbxie.mall.cart.vo.CartInfoResVo;
import com.xbxie.mall.cart.vo.ChangeQuantityReqVo;
import com.xbxie.mall.cart.vo.ChangeSelectReqVo;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.entity.CommonCartEntity;
import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.entity.CommonSkuEntity;
import com.xbxie.mall.common.entity.CommonSpuEntity;
import com.xbxie.mall.common.service.CommonCartService;
import com.xbxie.mall.common.service.CommonShopService;
import com.xbxie.mall.common.service.CommonSkuService;
import com.xbxie.mall.common.service.CommonSpuService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.JwtUtils;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xbxie.mall.cart.service.CartService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
@Service("cartService")
public class CartServiceImpl implements CartService {
    @Resource
    private CommonCartService commonCartService;

    @Resource
    private CommonSkuService commonSkuService;

    @Resource
    private CommonSpuService commonSpuService;

    @Resource
    private CommonShopService commonShopService;

    @Override
    public R<Void> add(CartAddVo cartAddVo, HttpServletRequest request) {
        Long skuId = cartAddVo.getSkuId();
        TokenBo tokenBo = JwtUtils.parseToken(request);

        // 商品不存在
        CommonSkuEntity commonSkuEntity = commonSkuService.getById(skuId);
        if (commonSkuEntity == null) {
            return R.fail("商品不存在");
        }

        // 商品已下架
        CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());
        if (Objects.equals(commonSpuEntity.getStatus(), 0)) {
            return R.fail("商品已下架");
        }

        CommonCartEntity commonCartEntity = commonCartService.getOne(new QueryWrapper<CommonCartEntity>().eq("sku_id", skuId));

        // 购物车不存在该商品
        if (commonCartEntity == null) {
            if (commonSkuEntity.getStock() < cartAddVo.getQuantity()) {
                return R.fail("商品库存不足");
            }

            CommonCartEntity commonNewCartEntity = new CommonCartEntity();
            BeanUtils.copyProperties(cartAddVo, commonNewCartEntity);
            commonNewCartEntity.setUserId(tokenBo.getId());
            commonNewCartEntity.setSelected(1);

            return commonCartService.save(commonNewCartEntity) ? R.success("加入购物车成功") : R.fail("加入购物车失败");
        }

        // 购物车已存在该商品
        if (commonSkuEntity.getStock() < (commonCartEntity.getQuantity() + cartAddVo.getQuantity())) {
            return R.fail("商品库存不足");
        }

        commonCartEntity.setQuantity(commonCartEntity.getQuantity() + cartAddVo.getQuantity());
        return commonCartService.updateById(commonCartEntity) ? R.success("加入购物车成功") : R.fail("加入购物车失败");
    }

    @Override
    public R<CartInfoResVo> info(HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);
        CartInfoResVo cartInfoResVo = new CartInfoResVo();

        // 购物车
        List<CommonCartEntity> commonCartEntities = commonCartService.list(new QueryWrapper<CommonCartEntity>().eq("user_id", tokenBo.getId()));
        if (CollectionUtils.isEmpty(commonCartEntities)) {
            return R.success(cartInfoResVo);
        }

        // 商品
        List<Long> skuIds = commonCartEntities.stream().map(CommonCartEntity::getSkuId).collect(Collectors.toList());
        List<CommonSkuEntity> commonSkuEntities = commonSkuService.listByIds(skuIds);
        if (CollectionUtils.isEmpty(commonSkuEntities)) {
            return R.success(cartInfoResVo);
        }

        // 店铺
        List<Long> shopIds = commonSkuEntities.stream().map(CommonSkuEntity::getShopId).collect(Collectors.toList());
        List<CommonShopEntity> commonShopEntities = commonShopService.listByIds(shopIds);
        if (CollectionUtils.isEmpty(commonShopEntities)) {
            return R.success(cartInfoResVo);
        }


        List<Boolean> totalSelected = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartInfoResVo.ShopItem> shopList = new ArrayList<>();

        for (CommonShopEntity commonShopEntity : commonShopEntities) {
            CartInfoResVo.ShopItem shopItem = new CartInfoResVo.ShopItem();
            List<Boolean> shopSelected = new ArrayList<>();
            List<CartInfoResVo.Goods> goodsList = new ArrayList<>();

            shopItem.setName(commonShopEntity.getName());
            shopItem.setId(commonShopEntity.getId());
            shopItem.setGoodsList(goodsList);

            for (CommonSkuEntity commonSkuEntity : commonSkuEntities) {

                if (Objects.equals(commonShopEntity.getId(), commonSkuEntity.getShopId())) {

                    for (CommonCartEntity commonCartEntity : commonCartEntities) {

                        if (Objects.equals(commonSkuEntity.getId(), commonCartEntity.getSkuId())) {
                            CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());
                            CartInfoResVo.Goods goods = new CartInfoResVo.Goods();
                            goods.setCartId(commonCartEntity.getId());
                            goods.setSkuId(commonSkuEntity.getId());
                            goods.setSelected(commonCartEntity.getSelected() == 1);
                            goods.setNum(commonCartEntity.getQuantity());
                            goods.setStock(commonSkuEntity.getStock());
                            goods.setPrice(BigDecimal.valueOf(commonCartEntity.getQuantity()).multiply(commonSkuEntity.getPrice()).setScale(2, RoundingMode.DOWN));

                            Map<String, String> attrsMap = (Map<String, String>) JSON.parse(commonSkuEntity.getAttrs());
                            if(attrsMap == null) {
                                goods.setAttrs("");
                            } else {
                                String str = String.join(",", attrsMap.values());
                                goods.setAttrs(str);
                            }

                            if (commonSpuEntity != null) {
                                goods.setSpuId(commonSpuEntity.getId());
                                goods.setImg(commonSpuEntity.getFirstImg());
                                goods.setName(commonSpuEntity.getName());
                            }

                            if (goods.getSelected()) {
                                totalPrice = totalPrice.add(goods.getPrice());
                            }

                            totalSelected.add(goods.getSelected());
                            shopSelected.add(goods.getSelected());
                            goodsList.add(goods);
                            break;
                        }
                    }
                }
            }


            shopItem.setSelected(shopSelected.stream().allMatch(selected -> selected));
            shopItem.setIndeterminate(shopSelected.stream().anyMatch(selected -> selected) && shopSelected.stream().anyMatch(selected -> !selected));

            shopList.add(shopItem);
        }

        cartInfoResVo.setTotalPrice(totalPrice.setScale(2, RoundingMode.DOWN));
        cartInfoResVo.setTotalSelected(totalSelected.stream().allMatch(selected -> selected));
        cartInfoResVo.setIndeterminate(totalSelected.stream().anyMatch(selected -> selected) && totalSelected.stream().anyMatch(selected -> !selected));
        cartInfoResVo.setShopList(shopList);

        return R.success(cartInfoResVo);
    }

    @Transactional
    @Override
    public R<Void> changeSelect(List<ChangeSelectReqVo> changeSelectReqVos) {
        for (ChangeSelectReqVo changeSelectReqVo : changeSelectReqVos) {
            Long id = changeSelectReqVo.getId();
            Boolean selected = changeSelectReqVo.getSelected();
            if (id == null) {
                throw new CustomException("购物车id不能为空");
            }
            if (selected == null) {
                throw new CustomException("商品选择状态不能为空");
            }
            if (!commonCartService.update(new UpdateWrapper<CommonCartEntity>().eq("id", id).set("selected", selected ? 1 : 0))) {
                throw new CustomException("更新失败");
            }
        }
        return R.success();
    }

    @Override
    public R<Void> del(Long id) {
        return commonCartService.removeById(id) ? R.success("删除成功") : R.fail("删除失败");
    }

    @Override
    public R<Void> changeQuantity(ChangeQuantityReqVo changeQuantityReqVo) {
        Long id = changeQuantityReqVo.getId();
        Integer quantity = changeQuantityReqVo.getQuantity();

        // 库存不足
        CommonCartEntity commonCartEntity = commonCartService.getById(changeQuantityReqVo.getId());
        CommonSkuEntity commonSkuEntity = commonSkuService.getById(commonCartEntity.getSkuId());
        if (commonSkuEntity != null && (commonSkuEntity.getStock() < changeQuantityReqVo.getQuantity())) {
            throw new CustomException("商品库存不足");
        }

        if (!commonCartService.update(new UpdateWrapper<CommonCartEntity>().eq("id", id).set("quantity", quantity))) {
            throw new CustomException("更新失败");
        }

        return R.success();
    }
}