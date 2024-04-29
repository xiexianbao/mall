package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.mapper.UserRoleRelMapper;
import com.xbxie.mall.admin.service.UserRoleRelService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024/4/27
 */
@Service("userRoleService")
public class UserRoleRelServiceImpl extends ServiceImpl<UserRoleRelMapper, UserRoleRelEntity> implements UserRoleRelService {
}
