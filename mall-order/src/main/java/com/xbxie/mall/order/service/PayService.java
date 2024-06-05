package com.xbxie.mall.order.service;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.order.vo.PrePayReqVo;
import com.xbxie.mall.order.vo.PrePayResVo;

/**
 * created by xbxie on 2024/5/31
 */
public interface PayService {
    R<Integer> wxPayStatus(String sn);

    R<String> getCodeUrl(PrePayReqVo prePayReqVo);
}
