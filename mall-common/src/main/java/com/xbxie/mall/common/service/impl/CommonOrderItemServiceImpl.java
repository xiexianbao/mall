package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonOrderItemEntity;
import com.xbxie.mall.common.mapper.CommonOrderItemMapper;
import com.xbxie.mall.common.service.CommonOrderItemService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-06-01 23:13:13
 */
@Service("commonOrderItemService")
public class CommonOrderItemServiceImpl extends ServiceImpl<CommonOrderItemMapper, CommonOrderItemEntity> implements CommonOrderItemService {
}