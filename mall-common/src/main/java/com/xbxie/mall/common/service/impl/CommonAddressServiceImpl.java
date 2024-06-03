package com.xbxie.mall.common.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.common.mapper.CommonAddressMapper;
import com.xbxie.mall.common.entity.CommonAddressEntity;
import com.xbxie.mall.common.service.CommonAddressService;

/**
 * created by xbxie on 2024-05-23 18:28:12
 */
@Service("commonAddressService")
public class CommonAddressServiceImpl extends ServiceImpl<CommonAddressMapper, CommonAddressEntity> implements CommonAddressService {
}