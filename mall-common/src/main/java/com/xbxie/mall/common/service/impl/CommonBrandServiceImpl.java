package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonBrandEntity;
import com.xbxie.mall.common.mapper.CommonBrandMapper;
import com.xbxie.mall.common.service.CommonBrandService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@Service("commonBrandService")
public class CommonBrandServiceImpl extends ServiceImpl<CommonBrandMapper, CommonBrandEntity> implements CommonBrandService {
}