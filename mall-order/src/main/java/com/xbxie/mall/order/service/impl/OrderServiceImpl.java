package com.xbxie.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.entity.*;
import com.xbxie.mall.common.service.*;
import com.xbxie.mall.common.service.impl.CommonCartServiceImpl;
import com.xbxie.mall.common.utils.*;
import com.xbxie.mall.order.service.OrderService;
import com.xbxie.mall.order.utils.WxPayUtils;
import com.xbxie.mall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
 * created by xbxie on 2024/5/21
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Resource
    private CommonSkuService commonSkuService;

    @Resource
    private CommonSpuService commonSpuService;

    @Resource
    private CommonOrderService commonOrderService;

    @Resource
    private CommonOrderGoodsService commonOrderGoodsService;

    @Resource
    private CommonShopService commonShopService;

    @Resource
    private WxPayUtils wxPayUtils;

    @Resource
    private CommonCartService commonCartService;

    @Override
    public R<ConfirmOrderResVo> getConfirmInfo(ConfirmOrderReqVo confirmOrderReqVo, HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);
        if (confirmOrderReqVo.getByCart()) {
            // 根据购物车数据生成订单数据
            return confirmOrderByCart(tokenBo.getId());
        } else {
            // 根据商品信息生成订单数据
            return confirmOrderByGoods(confirmOrderReqVo);
        }
    }

    @Override
    public R<String> createOrder(CreateOrderReqVo createOrderReqVo, HttpServletRequest request) {
        // 一个店铺对应一个母订单
        // 一个母订单对应多个商品
        Long addressId = createOrderReqVo.getAddressId();
        List<CreateOrderReqVo.Shop> shopList = createOrderReqVo.getShopList();
        SnowflakeDistributeId idWorker = new SnowflakeDistributeId(0, 0);
        String batchNo = String.valueOf(idWorker.nextId());
        TokenBo tokenBo = JwtUtils.parseToken(request);


        for (CreateOrderReqVo.Shop shop : shopList) {
            // 订单
            CommonOrderEntity commonOrderEntity = new CommonOrderEntity();
            String wxNo = String.valueOf(idWorker.nextId());
            BigDecimal shopAmount = BigDecimal.ZERO;


            // 商品
            List<CreateOrderReqVo.Goods> goodsList = shop.getGoodsList();
            List<CommonOrderGoodsEntity> commonOrderGoodsEntities = new ArrayList<>();

            for (CreateOrderReqVo.Goods goods : goodsList) {
                CommonOrderGoodsEntity commonOrderGoodsEntity = new CommonOrderGoodsEntity();
                commonOrderGoodsEntity.setOrderId(commonOrderEntity.getId());
                commonOrderGoodsEntity.setUserId(tokenBo.getId());
                commonOrderGoodsEntity.setSkuId(goods.getSkuId());
                commonOrderGoodsEntity.setSkuNum(goods.getSkuNum());
                CommonSkuEntity commonSkuEntity = commonSkuService.getById(goods.getSkuId());
                commonOrderGoodsEntity.setAmount(commonSkuEntity.getPrice().multiply(BigDecimal.valueOf(goods.getSkuNum())).setScale(2, RoundingMode.HALF_UP));
                shopAmount = shopAmount.add(commonOrderGoodsEntity.getAmount());
                commonOrderGoodsEntities.add(commonOrderGoodsEntity);
            }

            commonOrderEntity.setUserId(tokenBo.getId());
            commonOrderEntity.setShopId(shop.getId());
            commonOrderEntity.setAddressId(addressId);
            commonOrderEntity.setBatchNo(batchNo);
            commonOrderEntity.setWxNo(wxNo);
            commonOrderEntity.setAmount(shopAmount.setScale(2, RoundingMode.HALF_UP));

            if (!commonOrderService.save(commonOrderEntity)) {
                throw new CustomException("创建订单失败");
            }

            for (CommonOrderGoodsEntity commonOrderGoodsEntity : commonOrderGoodsEntities) {
                commonOrderGoodsEntity.setOrderId(commonOrderEntity.getId());
            }

            if (!commonOrderGoodsService.saveBatch(commonOrderGoodsEntities)) {
                throw new CustomException("创建订单失败");
            }
        }

        return R.successData(batchNo);
    }

    @Override
    public R<List<OrderListItemResVo>> list(OrderListReqVo orderListReqVo, HttpServletRequest request) {
        String goodsName = orderListReqVo.getGoodsName();
        Integer status = orderListReqVo.getStatus();
        TokenBo tokenBo = JwtUtils.parseToken(request);
        Long userId = tokenBo.getId();

        // QueryWrapper<CommonOrderEntity> orderQuery = new QueryWrapper<CommonOrderEntity>().eq("user_id", userId);
        // Page<CommonUserEntity> res = commonOrderService.page(new Page<>(orderListReqVo.getPageNum(), orderListReqVo.getPageSize()), orderQuery);

        // 父订单
        List<CommonOrderEntity> commonOrderEntities = commonOrderService.list(new QueryWrapper<CommonOrderEntity>().eq("user_id", userId));

        // 子订单
        List<Long> orderIds = commonOrderEntities.stream().map(CommonOrderEntity::getId).collect(Collectors.toList());
        List<CommonOrderGoodsEntity> commonOrderItemEntities = commonOrderGoodsService.list(new QueryWrapper<CommonOrderGoodsEntity>().in("pid", orderIds));

        // 商品
        List<Long> skuIds = commonOrderItemEntities.stream().map(CommonOrderGoodsEntity::getSkuId).collect(Collectors.toList());
        List<CommonSkuEntity> commonSkuEntities = commonSkuService.listByIds(skuIds);

        // 店铺
        List<Long> shopIds = commonSkuEntities.stream().map(CommonSkuEntity::getShopId).collect(Collectors.toList());
        List<CommonShopEntity> commonShopEntities = commonShopService.listByIds(shopIds);


        List<OrderListItemResVo> orderListItemResVos = new ArrayList<>();

        for (CommonOrderEntity commonOrderEntity : commonOrderEntities) {
            OrderListItemResVo orderListItemResVo = new OrderListItemResVo();

            orderListItemResVo.setId(commonOrderEntity.getId());
            List<OrderListItemResVo.ShopItem> shopList = new ArrayList<>();

            // 设置 shopList
            for (CommonShopEntity commonShopEntity : commonShopEntities) {
                OrderListItemResVo.ShopItem shopItem = new OrderListItemResVo.ShopItem();
                BigDecimal shopPrice = BigDecimal.ZERO;
                List<OrderListItemResVo.Goods> goodsList = new ArrayList<>();

                shopItem.setName(commonShopEntity.getName());

                for (CommonSkuEntity commonSkuEntity : commonSkuEntities) {
                    for (CommonOrderGoodsEntity commonOrderGoodsEntity : commonOrderItemEntities) {
                        if (Objects.equals(commonShopEntity.getId(), commonSkuEntity.getShopId()) && Objects.equals(commonSkuEntity.getId(), commonOrderGoodsEntity.getSkuId())) {
                            CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());
                            OrderListItemResVo.Goods goods = new OrderListItemResVo.Goods();

                            goods.setId(commonOrderGoodsEntity.getId());
                            goods.setName(commonSpuEntity.getName());
                            goods.setAttrs(GoodsUtils.getAttrValues(commonSkuEntity.getAttrs()));
                            goods.setImg(commonSkuEntity.getImg());
                            goods.setNum(commonOrderGoodsEntity.getSkuNum());
                            goods.setPrice(commonSkuEntity.getPrice());

                            shopPrice = shopPrice.add(commonOrderGoodsEntity.getAmount());

                            goodsList.add(goods);
                        }
                    }
                }

                shopItem.setPrice(shopPrice);
                shopItem.setGoodsList(goodsList);
            }


            orderListItemResVo.setShopList(shopList);
            orderListItemResVos.add(orderListItemResVo);
        }

        return R.success(orderListItemResVos);
    }

    private R<ConfirmOrderResVo> confirmOrderByCart(Long userId) {
        List<CommonCartEntity> commonCartEntities = commonCartService.list(new QueryWrapper<CommonCartEntity>().eq("user_id", userId).eq("selected", 1));
        if (CollectionUtils.isEmpty(commonCartEntities)) {
            throw new CustomException("购物车无选中商品");
        }

        // 商品
        List<Long> skuIds = commonCartEntities.stream().map(CommonCartEntity::getSkuId).collect(Collectors.toList());
        List<CommonSkuEntity> commonSkuEntities = commonSkuService.listByIds(skuIds);

        // 店铺
        List<Long> shopIds = commonSkuEntities.stream().map(CommonSkuEntity::getShopId).collect(Collectors.toList());
        List<CommonShopEntity> commonShopEntities = commonShopService.listByIds(shopIds);

        ConfirmOrderResVo confirmOrderResVo = new ConfirmOrderResVo();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<ConfirmOrderResVo.ShopItem> shopList = new ArrayList<>();

        for (CommonShopEntity commonShopEntity : commonShopEntities) {
            ConfirmOrderResVo.ShopItem shopItem = new ConfirmOrderResVo.ShopItem();
            List<ConfirmOrderResVo.Goods> goodsList = new ArrayList<>();

            shopItem.setName(commonShopEntity.getName());
            shopItem.setId(commonShopEntity.getId());
            shopItem.setGoodsList(goodsList);

            for (CommonSkuEntity commonSkuEntity : commonSkuEntities) {

                if (Objects.equals(commonShopEntity.getId(), commonSkuEntity.getShopId())) {

                    for (CommonCartEntity commonCartEntity : commonCartEntities) {

                        if (Objects.equals(commonSkuEntity.getId(), commonCartEntity.getSkuId())) {
                            CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());

                            // 库存不足
                            if (commonSkuEntity.getStock() < commonCartEntity.getQuantity()) {
                                System.out.println("库存不足");
                                throw new CustomException(commonSpuEntity.getName() + "库存不足");
                            }

                            ConfirmOrderResVo.Goods goods = new ConfirmOrderResVo.Goods();
                            goods.setSkuId(commonSkuEntity.getId());

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

                            goods.setNum(commonCartEntity.getQuantity());
                            goods.setPrice(BigDecimal.valueOf(commonCartEntity.getQuantity()).multiply(commonSkuEntity.getPrice()).setScale(2, RoundingMode.DOWN));

                            totalPrice = totalPrice.add(goods.getPrice());

                            goodsList.add(goods);
                            break;
                        }
                    }
                }
            }


            shopList.add(shopItem);
        }


        confirmOrderResVo.setTotalPrice(totalPrice.setScale(2, RoundingMode.DOWN));
        confirmOrderResVo.setShopList(shopList);
        return R.success(confirmOrderResVo);
    }

    private R<ConfirmOrderResVo> confirmOrderByGoods(ConfirmOrderReqVo confirmOrderReqVo) {
        Long skuId = confirmOrderReqVo.getId();
        Integer skuNum = confirmOrderReqVo.getNum();
        if (skuId == null) {
            throw new CustomException("请输入商品id");
        }
        if (skuNum == null) {
            throw new CustomException("请输入商品商品数量");
        }
        if (skuNum < 1) {
            throw new CustomException("商品数量至少为1");
        }
        // sku 不存在
        CommonSkuEntity commonSkuEntity = commonSkuService.getById(skuId);
        if (commonSkuEntity == null) {
            throw new CustomException("商品不存在");
        }
        // sku 库存不足
        if (commonSkuEntity.getStock() < skuNum) {
            throw new CustomException("商品库存不足");
        }
        // spu 不存在
        CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());
        if (commonSpuEntity == null) {
            throw new CustomException("商品不存在");
        }
        // 商品未上架
        if (commonSpuEntity.getStatus() != 1) {
            throw new CustomException("商品未上架");
        }

        ConfirmOrderResVo confirmOrderResVo = new ConfirmOrderResVo();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<ConfirmOrderResVo.ShopItem> shopList = new ArrayList<>();

        // 设置shopList
        {
            ConfirmOrderResVo.ShopItem shopItem = new ConfirmOrderResVo.ShopItem();

            // 设置shopItem
            {
                // 设置店铺基本信息
                {
                    Long shopId = commonSpuEntity.getShopId();
                    if (shopId != null) {
                        CommonShopEntity commonShopEntity = commonShopService.getById(shopId);
                        if (commonShopEntity != null) {
                            shopItem.setId(commonShopEntity.getId());
                            shopItem.setName(commonShopEntity.getName());
                        }
                    }
                }



                // 设置店铺goodsList
                {
                    List<ConfirmOrderResVo.Goods> goodsList = new ArrayList<>();
                    ConfirmOrderResVo.Goods goods = new ConfirmOrderResVo.Goods();

                    // 设置店铺goods
                    {

                        goods.setSpuId(commonSpuEntity.getId());
                        goods.setSkuId(commonSkuEntity.getId());
                        goods.setName(commonSpuEntity.getName());
                        goods.setNum(skuNum);
                        goods.setPrice(commonSkuEntity.getPrice().multiply(BigDecimal.valueOf(skuNum)).setScale(2, RoundingMode.HALF_UP));

                        Map<String, String> attrsMap = (Map<String, String>)JSON.parse(commonSkuEntity.getAttrs());
                        if(attrsMap == null) {
                            goods.setAttrs("");
                        } else {
                            String str = String.join(",", attrsMap.values());
                            goods.setAttrs(str);
                        }

                        goods.setImg(commonSpuEntity.getFirstImg());
                        totalPrice = totalPrice.add(goods.getPrice());
                    }

                    goodsList.add(goods);
                    shopItem.setGoodsList(goodsList);
                }
            }

            shopList.add(shopItem);
        }


        confirmOrderResVo.setTotalPrice(totalPrice.setScale(2, RoundingMode.DOWN));
        confirmOrderResVo.setShopList(shopList);

        return R.success(confirmOrderResVo);
    }
}
