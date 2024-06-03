package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonOrderEntity;
import com.xbxie.mall.common.mapper.CommonOrderMapper;
import com.xbxie.mall.common.service.CommonOrderService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-06-01 23:13:13
 */
@Service("commonOrderService")
public class CommonOrderServiceImpl extends ServiceImpl<CommonOrderMapper, CommonOrderEntity> implements CommonOrderService {
}