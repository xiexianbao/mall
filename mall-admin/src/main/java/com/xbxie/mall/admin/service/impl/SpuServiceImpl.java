package com.xbxie.mall.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.mapper.SpuMapper;
import com.xbxie.mall.admin.entity.SpuEntity;
import com.xbxie.mall.admin.service.SpuService;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {
}