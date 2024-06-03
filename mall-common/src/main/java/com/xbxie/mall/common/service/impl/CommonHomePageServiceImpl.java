package com.xbxie.mall.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.entity.CommonHomePageEntity;
import com.xbxie.mall.common.mapper.CommonHomePageMapper;
import com.xbxie.mall.common.service.CommonHomePageService;
import org.springframework.stereotype.Service;

/**
 * created by xbxie on 2024-05-20 15:33:52
 */
@Service("commonHomePageService")
public class CommonHomePageServiceImpl extends ServiceImpl<CommonHomePageMapper, CommonHomePageEntity> implements CommonHomePageService {
}