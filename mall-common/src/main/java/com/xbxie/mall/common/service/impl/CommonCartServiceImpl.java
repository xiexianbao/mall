package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonCartEntity;
import com.xbxie.mall.common.mapper.CommonCartMapper;
import com.xbxie.mall.common.service.CommonCartService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-06-03 12:31:38
 */
@Service("commonCartService")
public class CommonCartServiceImpl extends ServiceImpl<CommonCartMapper, CommonCartEntity> implements CommonCartService {
}