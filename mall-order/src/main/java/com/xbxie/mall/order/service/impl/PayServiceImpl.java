package com.xbxie.mall.order.service.impl;

import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.constant.PayStatus;
import com.xbxie.mall.order.service.PayService;
import com.xbxie.mall.order.utils.WxPayUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by xbxie on 2024/5/31
 */
@Service("payService")
public class PayServiceImpl implements PayService {
    @Resource
    private WxPayUtils wxPayUtils;

    @Override
    public R<Integer> wxPayStatus(String sn) {
        PayStatus status = wxPayUtils.queryNativeOrder(sn);

        if (status == null) {
            throw new CustomException("支付失败");
        }

        return R.success(status.getCode());
    }
}
