package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonOrderGoodsEntity;
import com.xbxie.mall.common.mapper.CommonOrderItemMapper;
import com.xbxie.mall.common.service.CommonOrderGoodsService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-06-01 23:13:13
 */
@Service("commonOrderGoodsService")
public class CommonOrderGoodsServiceImpl extends ServiceImpl<CommonOrderItemMapper, CommonOrderGoodsEntity> implements CommonOrderGoodsService {
}