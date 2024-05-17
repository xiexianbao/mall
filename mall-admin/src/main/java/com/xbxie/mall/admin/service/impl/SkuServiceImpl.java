package com.xbxie.mall.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.mapper.SkuMapper;
import com.xbxie.mall.admin.entity.SkuEntity;
import com.xbxie.mall.admin.service.SkuService;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@Service("skuService")
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuEntity> implements SkuService {
}