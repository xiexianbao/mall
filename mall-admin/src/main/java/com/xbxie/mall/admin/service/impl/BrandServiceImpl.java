package com.xbxie.mall.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.mapper.BrandMapper;
import com.xbxie.mall.admin.entity.BrandEntity;
import com.xbxie.mall.admin.service.BrandService;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements BrandService {
}