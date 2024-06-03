package com.xbxie.mall.order.service;

import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/5/31
 */
public interface PayService {
    R<Integer> wxPayStatus(String sn);
}
