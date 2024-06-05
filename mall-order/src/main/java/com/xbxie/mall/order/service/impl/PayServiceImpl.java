package com.xbxie.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.common.entity.CommonOrderEntity;
import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.service.CommonOrderService;
import com.xbxie.mall.common.service.CommonShopService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.constant.PayStatus;
import com.xbxie.mall.order.service.PayService;
import com.xbxie.mall.order.utils.WxPayUtils;
import com.xbxie.mall.order.vo.PrePayReqVo;
import com.xbxie.mall.order.vo.PrePayResVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024/5/31
 */
@Service("payService")
public class PayServiceImpl implements PayService {
    @Resource
    private WxPayUtils wxPayUtils;

    @Resource
    private CommonOrderService commonOrderService;

    @Resource
    private CommonShopService commonShopService;

    @Override
    public R<Integer> wxPayStatus(String sn) {
        PayStatus status = wxPayUtils.queryNativeOrder(sn);

        if (status == null) {
            throw new CustomException("支付失败");
        }

        return R.success(status.getCode());
    }

    @Override
    public R<String> getCodeUrl(PrePayReqVo prePayReqVo) {
        String batchNo = prePayReqVo.getBatchNo();
        String wxNo = prePayReqVo.getWxNo();

        // 合并支付
        if (StringUtils.hasLength(batchNo)) {
            BigDecimal amount = BigDecimal.ZERO;

            List<CommonOrderEntity> commonOrderEntities = commonOrderService.list(new QueryWrapper<CommonOrderEntity>().eq("batch_no", batchNo));

            for (CommonOrderEntity commonOrderEntity : commonOrderEntities) {
                amount = amount.add(commonOrderEntity.getAmount());
            }

            List<Long> shopIds = commonOrderEntities.stream().map(CommonOrderEntity::getShopId).collect(Collectors.toList());
            String name = commonShopService.listByIds(shopIds).stream().map(CommonShopEntity::getName).collect(Collectors.joining(","));
            int total = amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN).intValue();
            String codeUrl = wxPayUtils.createNativeOrder(total, batchNo, name);

            return R.successData(codeUrl);
        }

        if (!StringUtils.hasLength(wxNo)) {
            throw new CustomException("请输入订单号");
        }

        // 拆单支付
        CommonOrderEntity commonOrderEntity = commonOrderService.getOne(new QueryWrapper<CommonOrderEntity>().eq("wx_no", wxNo));
        CommonShopEntity commonShopEntity = commonShopService.getById(commonOrderEntity.getShopId());
        int total = commonOrderEntity.getAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN).intValue();;
        String codeUrl = wxPayUtils.createNativeOrder(total, wxNo, commonShopEntity.getName());

        return R.successData(codeUrl);
    }
}
