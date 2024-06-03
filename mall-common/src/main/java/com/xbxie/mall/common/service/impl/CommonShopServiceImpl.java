package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.mapper.CommonShopMapper;
import com.xbxie.mall.common.service.CommonShopService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-05-23 18:28:12
 */
@Service("commonShopService")
public class CommonShopServiceImpl extends ServiceImpl<CommonShopMapper, CommonShopEntity> implements CommonShopService {
}