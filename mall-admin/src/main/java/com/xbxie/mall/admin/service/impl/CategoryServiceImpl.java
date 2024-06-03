package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.entity.CommonCategoryEntity;
import com.xbxie.mall.common.service.CommonCategoryService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xbxie.mall.admin.service.CategoryService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CommonCategoryService commonCategoryService;
    
    @Override
    public R<List<CategoryResVo>> listTree(CategoryReqVo categoryReqVo) {
        String name = categoryReqVo.getName();

        if (StringUtils.hasLength(name)) {
            List<CommonCategoryEntity> categoryEntities = commonCategoryService.list(new QueryWrapper<CommonCategoryEntity>().like("name", name));

            if (CollectionUtils.isEmpty(categoryEntities)) {
                return R.success(new ArrayList<>());
            }

            return R.success(categoryEntities.stream().map(CommonCategoryEntity -> {
                CategoryResVo categoryResVo = new CategoryResVo();
                BeanUtils.copyProperties(CommonCategoryEntity, categoryResVo);
                return categoryResVo;
            }).collect(Collectors.toList()));
        }

        return R.success(recursion(commonCategoryService.list(), null));
    }

    @Override
    public R<Void> add(CategoryAddVo categoryAddVo) {
        String name = categoryAddVo.getName();

        // 判断分类名是否已存在
        if (commonCategoryService.exists(new QueryWrapper<CommonCategoryEntity>().eq("name", name))) {
            return R.fail("分类名重复");
        }

        // 插入分类
        CommonCategoryEntity CommonCategoryEntity = new CommonCategoryEntity();
        BeanUtils.copyProperties(categoryAddVo, CommonCategoryEntity);

        if (!commonCategoryService.save(CommonCategoryEntity)) {
            return R.fail("添加分类失败");
        }

        return R.success("添加分类成功");
    }

    @Override
    public R<List<CategoryVo>> getList() {
        List<CommonCategoryEntity> categoryEntities = commonCategoryService.list();
        if (CollectionUtils.isEmpty(categoryEntities)) {
            return R.success(new ArrayList<>());
        }

        return R.success(categoryEntities.stream().map(CommonCategoryEntity -> {
            CategoryVo categoryVo = new CategoryVo();
            BeanUtils.copyProperties(CommonCategoryEntity, categoryVo);
            return categoryVo;
        }).collect(Collectors.toList()));
    }

    @Override
    public R<Void> del(Long id) {
        // 分类不存在
        if (commonCategoryService.getById(id) == null) {
            return R.fail("分类不存在");
        }

        // 删除自身及子分类
        List<Object> delIds = new ArrayList<>(Arrays.asList(id));
        delIds.addAll(getChildrenIds(id, commonCategoryService.list()));
        if (!commonCategoryService.removeByIds(delIds)) {
            return R.fail("删除分类失败");
        }

        return R.success("删除分类成功");
    }

    @Override
    public R<CategoryDetailVo> getCategory(Long id) {
        CommonCategoryEntity CommonCategoryEntity = commonCategoryService.getById(id);
        if (CommonCategoryEntity == null) {
            throw new CustomException("分类不存在");
        }

        CategoryDetailVo categoryDetailVo = new CategoryDetailVo();
        BeanUtils.copyProperties(CommonCategoryEntity, categoryDetailVo);
        return R.success(categoryDetailVo);
    }

    @Override
    public R<Void> updateCategory(CategoryUpdateVo categoryUpdateVo) {
        // 更新的分类不存在
        Long id = categoryUpdateVo.getId();
        if (commonCategoryService.getById(id) == null) {
            return R.fail("分类不存在");
        }

        List<CommonCategoryEntity> categoryEntities = commonCategoryService.list();
        Long pid = categoryUpdateVo.getPid();

        // 禁止将将自身或后代设置为父分类，否则会形成环
        if (pid != null) {
            List<Long> childrenIds = getChildrenIds(categoryUpdateVo.getId(), categoryEntities);
            if (childrenIds.contains(pid) || Objects.equals(id, pid)) {
                return R.fail("无法将自身或后代设置为父分类");
            }
        }

        // 分类名重复
        if (
            categoryEntities.stream().anyMatch(CommonCategoryEntity -> !Objects.equals(CommonCategoryEntity.getId(), id) && Objects.equals(CommonCategoryEntity.getName(), categoryUpdateVo.getName()))
        ) {
            return R.fail("分类名重复");
        }

        CommonCategoryEntity CommonCategoryEntity = new CommonCategoryEntity();
        BeanUtils.copyProperties(categoryUpdateVo, CommonCategoryEntity);

        UpdateWrapper<CommonCategoryEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("pid", categoryUpdateVo.getPid());
        updateWrapper.set("name", categoryUpdateVo.getName());
        updateWrapper.set("img", categoryUpdateVo.getImg());
        if (!commonCategoryService.update(updateWrapper)) {
            return R.fail("更新分类失败");
        }

        return R.success("更新分类成功");
    }

    private List<Long> getChildrenIds(Long pid, List<CommonCategoryEntity> categoryEntities) {
        List<Long> list = new ArrayList<>();

        for (CommonCategoryEntity CommonCategoryEntity : categoryEntities) {
            if (Objects.equals(pid, CommonCategoryEntity.getPid())) {
                list.add(CommonCategoryEntity.getId());
                list.addAll(getChildrenIds(CommonCategoryEntity.getId(), categoryEntities));
            }
        }

        return list;
    }

    private List<CategoryResVo> recursion(List<CommonCategoryEntity> categoryEntities, Long pid) {
        List<CategoryResVo> categoryResVos = new ArrayList<>();

        List<CommonCategoryEntity> collect = categoryEntities.stream().filter(CommonCategoryEntity -> Objects.equals(CommonCategoryEntity.getPid(), pid)).collect(Collectors.toList());
        for (CommonCategoryEntity CommonCategoryEntity : collect) {
            CategoryResVo categoryResVo = new CategoryResVo();
            BeanUtils.copyProperties(CommonCategoryEntity, categoryResVo);
            List<CategoryResVo> children = recursion(categoryEntities, CommonCategoryEntity.getId());
            categoryResVo.setChildren(CollectionUtils.isEmpty(children) ? null : children);
            categoryResVos.add(categoryResVo);
        }

        return categoryResVos;
    }
}