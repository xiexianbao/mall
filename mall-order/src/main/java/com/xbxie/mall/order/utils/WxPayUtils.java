package com.xbxie.mall.order.utils;

import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.*;
import com.xbxie.mall.order.component.WxPayProperties;
import com.xbxie.mall.order.constant.PayStatus;
import org.springframework.stereotype.Component;
import com.wechat.pay.java.service.payments.model.Transaction;
import javax.annotation.Resource;

/**
 * created by xbxie on 2024/6/1
 */
@Component
public class WxPayUtils {
    @Resource
    private WxPayProperties wxPayProperties;

    @Resource
    private NativePayService nativePayService;

    // 创建 native 订单
    public String createNativeOrder(Integer total, String orderNo, String desc) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();

        amount.setTotal(total);
        request.setAmount(amount);
        request.setAppid(wxPayProperties.getAppId());
        request.setMchid(wxPayProperties.getMchId());
        request.setDescription(desc);
        request.setNotifyUrl(wxPayProperties.getNotifyDomain());
        request.setOutTradeNo(orderNo);

        PrepayResponse response = nativePayService.prepay(request);
        return response.getCodeUrl();
    }

    // 查询 native 状态
    public PayStatus queryNativeOrder(String orderNo) {
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(wxPayProperties.getMchId());
        queryRequest.setOutTradeNo(orderNo);

        PayStatus status = null;
        Transaction transaction = nativePayService.queryOrderByOutTradeNo(queryRequest);
        Transaction.TradeStateEnum tradeState = transaction.getTradeState();

        switch (tradeState) {
            case SUCCESS:
                status = PayStatus.PAID;
                break;
            case REFUND:
                status = PayStatus.REFUNDED;
                break;
            case NOTPAY:
                status = PayStatus.UN_PAY;
                break;
            case CLOSED:
                status = PayStatus.CLOSED;
                break;
        }

        return status;
    }
}
