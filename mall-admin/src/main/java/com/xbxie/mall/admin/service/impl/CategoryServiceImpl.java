package com.xbxie.mall.admin.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.mapper.CategoryMapper;
import com.xbxie.mall.admin.entity.CategoryEntity;
import com.xbxie.mall.admin.service.CategoryService;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {
}