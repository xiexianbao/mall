package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.ShopVo;
import com.xbxie.mall.common.utils.R;

import java.util.List;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
public interface ShopService {
    R<List<ShopVo>> list();
}

