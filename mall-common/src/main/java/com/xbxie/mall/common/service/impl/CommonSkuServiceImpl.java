package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonSkuEntity;
import com.xbxie.mall.common.mapper.CommonSkuMapper;
import com.xbxie.mall.common.service.CommonSkuService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@Service("commonSkuService")
public class CommonSkuServiceImpl extends ServiceImpl<CommonSkuMapper, CommonSkuEntity> implements CommonSkuService {
}