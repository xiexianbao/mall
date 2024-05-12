package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.mapper.RoleMapper;
import com.xbxie.mall.admin.service.RoleMenuRelService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.vo.RoleAddVo;
import com.xbxie.mall.admin.vo.RolePageVo;
import com.xbxie.mall.admin.vo.RoleUpdateVo;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024/4/25
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {
    @Resource
    private RoleMenuRelService roleMenuRelService;

    @Override
    public R<Void> add(RoleAddVo roleAddVo) {
        // 角色名重复
        if (this.exists(new QueryWrapper<RoleEntity>().eq("name", roleAddVo.getName()))) {
            return R.fail("角色名重复");
        }

        // 插入角色
        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleAddVo, roleEntity);
        if (!this.save(roleEntity)) {
            return R.fail("添加角色失败");
        }

        // 插入菜单
        Long roleId = roleEntity.getId();
        List<Long> menuIdList = roleAddVo.getMenuIdList();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            if (
                !roleMenuRelService.saveBatch(
                    menuIdList.stream().map(menuId -> {
                        RoleMenuRelEntity roleMenuRelEntity = new RoleMenuRelEntity();
                        roleMenuRelEntity.setRoleId(roleId);
                        roleMenuRelEntity.setMenuId(menuId);
                        return roleMenuRelEntity;
                    }).collect(Collectors.toList())
                )
            ) {
                throw  new CustomException("添加角色失败");
            }
        }

        return R.success("添加角色成功");
    }

    @Transactional
    @Override
    public R<Void> del(Long id) {
        // 角色不存在
        if (!this.exists(new QueryWrapper<RoleEntity>().eq("id", id))) {
            return R.fail("角色不存在");
        }

        // 删除角色
        if (!this.removeById(id)) {
            return R.fail("删除角色失败");
        }

        // 删除角色菜单关系
        QueryWrapper<RoleMenuRelEntity> wrapper = new QueryWrapper<RoleMenuRelEntity>().eq("role_id", id);
        if (roleMenuRelService.exists(wrapper)) {
            if (!roleMenuRelService.remove(wrapper)) {
                throw new CustomException("删除角色菜单失败");
            }
        }

        return R.success("删除角色成功");
    }

    @Override
    public R<Void> updateRole(RoleUpdateVo roleUpdateVo) {
        // 角色不存在
        Long id = roleUpdateVo.getId();
        if (this.getById(id) == null) {
            return R.fail("角色不存在");
        }

        // 角色名重复
        if (!CollectionUtils.isEmpty(
            this.list(
                new QueryWrapper<RoleEntity>()
                    .ne("id", id)
                    .and(i -> i.eq("name", roleUpdateVo.getName()))
            )
        )) {
            return R.fail("角色名重复");
        }

        RoleEntity roleEntity = new RoleEntity();
        BeanUtils.copyProperties(roleUpdateVo, roleEntity);

        if (!this.updateById(roleEntity)) {
            R.fail("更新角色失败");
        }

        // 更新角色的菜单
        // 先删除角色的旧菜单
        this.roleMenuRelService.remove(new QueryWrapper<RoleMenuRelEntity>().eq("role_id", id));
        // 添加新菜单
        List<Long> menuIdList = roleUpdateVo.getMenuIdList();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            if (
                !roleMenuRelService.saveBatch(menuIdList.stream().map(menuId -> {
                    RoleMenuRelEntity roleMenuRelEntity = new RoleMenuRelEntity();
                    roleMenuRelEntity.setRoleId(id);
                    roleMenuRelEntity.setMenuId(menuId);
                    return roleMenuRelEntity;
                }).collect(Collectors.toList()))
            ) {
                throw new CustomException("更新角色菜单失败");
            }
        }

        return R.success("更新角色成功");
    }

    @Override
    public R<PageData<RoleEntity>> pageList(RolePageVo rolePageVo) {
        String name = rolePageVo.getName();

        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(name)) {
            wrapper.like("name", name);
        }

        Page<RoleEntity> res = this.page(new Page<>(rolePageVo.getPageNum(), rolePageVo.getPageSize()), wrapper);
        PageData<RoleEntity> pageData = PageData.getPageData(res);
        return R.success(pageData);
    }
}
