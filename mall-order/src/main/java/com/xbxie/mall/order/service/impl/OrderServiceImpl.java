package com.xbxie.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.entity.*;
import com.xbxie.mall.common.service.*;
import com.xbxie.mall.common.utils.*;
import com.xbxie.mall.order.service.OrderService;
import com.xbxie.mall.order.utils.WxPayUtils;
import com.xbxie.mall.order.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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
    private CommonOrderItemService commonOrderItemService;

    @Resource
    private CommonShopService commonShopService;

    @Resource
    private WxPayUtils wxPayUtils;

    @Override
    public R<ConfirmOrderResVo> getConfirmInfo(ConfirmOrderReqVo confirmOrderReqVo) {
        ConfirmOrderResVo confirmOrderResVo;

        if ("cart".equals(confirmOrderReqVo.getSource())) {
            // 根据购物车数据生成订单数据
            confirmOrderResVo = genOrderInfoByCart();
        } else {
            // 根据商品信息生成订单数据
            confirmOrderResVo = genOrderInfoByGoods(confirmOrderReqVo);
        }

        return R.success(confirmOrderResVo);
    }

    @Override
    public R<CreateOrderResVo> createOrder(CreateOrderReqVo createOrderReqVo, HttpServletRequest request) {
        Long skuId = createOrderReqVo.getId();
        Integer skuNum = createOrderReqVo.getNum();
        CreateOrderResVo createOrderResVo = new CreateOrderResVo();

        CommonSkuEntity commonSkuEntity = commonSkuService.getById(skuId);
        // sku 不存在
        if (commonSkuEntity == null) {
            throw new CustomException("商品不存在");
        }

        // sku 库存不够
        if (commonSkuEntity.getStock() < skuNum) {
            throw new CustomException("商品库存不足");
        }


        TokenBo tokenBo = JwtUtils.parseToken(request);
        if (tokenBo == null) {
            throw new CustomException("用户信息异常");
        }

        // 订单入库
        SnowflakeDistributeId idWorker = new SnowflakeDistributeId(0, 0);
        String orderSn = String.valueOf(idWorker.nextId());
        String orderItemSn = String.valueOf(idWorker.nextId());

        BigDecimal amount = BigDecimal.valueOf(skuNum).multiply(commonSkuEntity.getPrice()).setScale(2, RoundingMode.DOWN);
        CommonOrderEntity commonOrderEntity = new CommonOrderEntity();
        commonOrderEntity.setAmount(amount);
        commonOrderEntity.setSn(orderSn);
        commonOrderEntity.setUserId(tokenBo.getId());
        commonOrderService.save(commonOrderEntity);

        CommonOrderItemEntity commonOrderItemEntity = new CommonOrderItemEntity();
        commonOrderItemEntity.setPid(commonOrderEntity.getId());
        commonOrderItemEntity.setSkuId(createOrderReqVo.getId());
        commonOrderItemEntity.setSkuNum(createOrderReqVo.getNum());
        commonOrderItemEntity.setSn(orderItemSn);
        commonOrderItemEntity.setAmount(amount);
        commonOrderItemEntity.setUserId(tokenBo.getId());
        commonOrderItemService.save(commonOrderItemEntity);


        // 微信支付
        int total = amount.multiply(BigDecimal.valueOf(100)).intValue();
        String name = "";

        Long spuId = commonSkuEntity.getSpuId();
        if (spuId != null) {
            CommonSpuEntity commonSpuEntity = commonSpuService.getById(spuId);
            if (commonSpuEntity != null) {
                name = commonSpuEntity.getName();
            }
        }

        String codeUrl = wxPayUtils.createNativeOrder(total, orderSn, name);

        if (!StringUtils.hasLength(codeUrl)) {
            throw new CustomException("下单失败");
        }

        createOrderResVo.setCodeUrl(codeUrl);
        createOrderResVo.setSn(orderSn);
        return R.success(createOrderResVo);
    }

    @Override
    public R<List<OrderListItemResVo>> list(OrderListReqVo orderListReqVo, HttpServletRequest request) {
        List<OrderListItemResVo> empty = new ArrayList<>();
        String goodsName = orderListReqVo.getGoodsName();
        Integer status = orderListReqVo.getStatus();
        TokenBo tokenBo = JwtUtils.parseToken(request);
        Long userId = tokenBo.getId();

        // 查询用户的子订单
        QueryWrapper<CommonOrderItemEntity> orderItemQuery = new QueryWrapper<CommonOrderItemEntity>().eq("user_id", userId);
        if (status != null) {
            orderItemQuery.eq("status", status);
        }
        List<CommonOrderItemEntity> commonOrderItemEntities = commonOrderItemService.list(orderItemQuery);

        if (CollectionUtils.isEmpty(commonOrderItemEntities)) {
            return R.success(empty);
        }

        // 查询子订单的商品
        List<Long> skuIds = commonOrderItemEntities.stream().map(CommonOrderItemEntity::getSkuId).collect(Collectors.toList());
        QueryWrapper<CommonSkuEntity> skuQuery = new QueryWrapper<CommonSkuEntity>().in("id", skuIds);
        if(StringUtils.hasLength(goodsName)) {
            skuQuery.like("name", goodsName);
        }
        List<CommonSkuEntity> commonSkuEntities = commonSkuService.list(skuQuery);

        if (CollectionUtils.isEmpty(commonSkuEntities)) {
            return R.success(empty);
        }

        // 查询商品的店铺信息
        List<Long> shopIds = commonSkuEntities.stream().map(CommonSkuEntity::getShopId).collect(Collectors.toList());
        List<CommonShopEntity> commonShopEntities = commonShopService.listByIds(shopIds);

        if (CollectionUtils.isEmpty(commonShopEntities)) {
            return R.success(empty);
        }

        // 三层循环
        // 第一层：店铺
        // 第二层：商品
        // 第三层：子订单
        List<OrderListItemResVo> orderListItemResVos = new ArrayList<>();
        for (CommonShopEntity commonShopEntity : commonShopEntities) {
            OrderListItemResVo orderListItemResVo = new OrderListItemResVo();
            List<OrderListItemResVo.OrderItem> orderItems = new ArrayList<>();

            orderListItemResVo.setShopName(commonShopEntity.getName());
            orderListItemResVo.setOrderItems(orderItems);

            for (CommonSkuEntity commonSkuEntity : commonSkuEntities) {
                // 过滤商品
                if (Objects.equals(commonSkuEntity.getShopId(), commonShopEntity.getId())) {
                    for (CommonOrderItemEntity commonOrderItemEntity : commonOrderItemEntities) {
                        // 过滤订单
                        if (Objects.equals(commonOrderItemEntity.getSkuId(), commonSkuEntity.getId())) {
                            OrderListItemResVo.OrderItem orderItem = new OrderListItemResVo.OrderItem();

                            CommonSpuEntity commonSpuEntity = commonSpuService.getById(commonSkuEntity.getSpuId());
                            if (commonSpuEntity != null) {
                                orderItem.setName(commonSpuEntity.getName());
                                orderItem.setImg(commonSpuEntity.getFirstImg());
                            }

                            Map<String, String> attrsMap = (Map<String, String>)JSON.parse(commonSkuEntity.getAttrs());
                            if(attrsMap == null) {
                                orderItem.setAttrs("");
                            } else {
                                String str = String.join(",", attrsMap.values());
                                orderItem.setAttrs(str);
                            }

                            orderItem.setNum(commonOrderItemEntity.getSkuNum());
                            orderItem.setPrice(commonOrderItemEntity.getAmount());

                            orderItems.add(orderItem);
                            // 商品对应的订单
                            break;
                        }
                    }
                }
            }

            orderListItemResVos.add(orderListItemResVo);
        }

        // 分页

        return R.success(orderListItemResVos);
    }

    private ConfirmOrderResVo genOrderInfoByCart() {
        return null;
    }

    private ConfirmOrderResVo genOrderInfoByGoods(ConfirmOrderReqVo confirmOrderReqVo) {
        Long skuId = confirmOrderReqVo.getId();
        Integer skuNum = confirmOrderReqVo.getNum();
        ConfirmOrderResVo confirmOrderResVo = new ConfirmOrderResVo();

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
        Long spuId = commonSkuEntity.getSpuId();
        if (spuId == null) {
            throw new CustomException("商品不存在");
        }
        CommonSpuEntity commonSpuEntity = commonSpuService.getById(spuId);
        if (commonSpuEntity == null) {
            throw new CustomException("商品不存在");
        }

        // 设置商品名
        confirmOrderResVo.setName(commonSpuEntity.getName());

        // 店铺信息
        Long shopId = commonSpuEntity.getShopId();
        if (shopId != null) {
            CommonShopEntity commonShopEntity = commonShopService.getById(shopId);
            if (commonShopEntity != null) {
                confirmOrderResVo.setShopName(commonShopEntity.getName());
            }
        }


        // 设置 sku 信息
        confirmOrderResVo.setNum(skuNum);
        confirmOrderResVo.setPrice(commonSkuEntity.getPrice().multiply(BigDecimal.valueOf(skuNum)).setScale(2, RoundingMode.HALF_UP));

        Map<String, String> attrsMap = (Map<String, String>)JSON.parse(commonSkuEntity.getAttrs());
        if(attrsMap == null) {
            confirmOrderResVo.setAttrs("");
        } else {
            String str = String.join(",", attrsMap.values());
            confirmOrderResVo.setAttrs(str);
        }

        confirmOrderResVo.setImg(StringUtils.hasLength(commonSkuEntity.getImg()) ? commonSkuEntity.getImg() : commonSpuEntity.getFirstImg());

        return confirmOrderResVo;
    }
}
