package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.entity.*;
import com.xbxie.mall.admin.mapper.MenuMapper;
import com.xbxie.mall.admin.service.MenuService;
import com.xbxie.mall.admin.service.RoleMenuRelService;
import com.xbxie.mall.admin.vo.MenuAddVo;
import com.xbxie.mall.admin.vo.MenuPageVo;
import com.xbxie.mall.admin.vo.MenuUpdateVo;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.common.utils.ValidationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024/4/25
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {
    @Resource
    private RoleMenuRelService roleMenuRelService;

    @Override
    public R add(MenuAddVo menuAddVo) {
        List<MenuEntity> list = this.list(new QueryWrapper<MenuEntity>().eq("name", menuAddVo.getName()).or().eq("url", menuAddVo.getUrl()));

        if (!CollectionUtils.isEmpty(list)) {
            if (list.stream().anyMatch(item -> Objects.equals(item.getName(), menuAddVo.getName()))) {
                return R.fail("菜单名重复");
            }
            if (list.stream().anyMatch(item -> Objects.equals(item.getUrl(), menuAddVo.getUrl()))) {
                return R.fail("菜单路径重复");
            }
        }

        MenuEntity menuEntity = new MenuEntity();
        BeanUtils.copyProperties(menuAddVo, menuEntity);
        return this.save(menuEntity) ? R.success("添加菜单成功") : R.fail("添加菜单失败");
    }

    @Transactional
    @Override
    public R del(Long id) {
        // 菜单不存在
        if (!this.exists(new QueryWrapper<MenuEntity>().eq("id", id))) {
            return R.fail("菜单不存在");
        }

        // 删除菜单
        if (!this.removeById(id)) {
            return R.fail("删除菜单失败");
        }


        // 删除菜单角色关系，这样之前拥有该菜单的角色就不在拥有该才当了
        QueryWrapper<RoleMenuRelEntity> wrapper = new QueryWrapper<RoleMenuRelEntity>().eq("menu_id", id);
        if (roleMenuRelService.exists(wrapper)) {
            if (!roleMenuRelService.remove(wrapper)) {
                throw new CustomException("删除菜单角色失败");
            }
        }


        return R.success("删除菜单成功");
    }

    @Override
    public R<PageData<MenuEntity>> pageList(MenuPageVo menuPageVo) {
        String name = menuPageVo.getName();

        QueryWrapper<MenuEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(name)) {
            wrapper.like("name", name);
        }

        Page<MenuEntity> res = this.page(new Page<>(menuPageVo.getPageNum(), menuPageVo.getPageSize()), wrapper);
        PageData<MenuEntity> pageData = PageData.getPageData(res);
        return R.success(pageData);
    }

    @Override
    public R updateMenu(MenuUpdateVo menuUpdateVo) {

        // 更新的菜单不存在
        Long id = menuUpdateVo.getId();
        if (this.getById(id) == null) {
            return R.fail("菜单不存在");
        }

        // 菜单名/菜单路径重复
        List<MenuEntity> list = this.list(
            new QueryWrapper<MenuEntity>()
                .ne("id", id)
                .and(i -> i.eq("name", menuUpdateVo.getName()).or().eq("url", menuUpdateVo.getUrl()))
        );
        if (!CollectionUtils.isEmpty(list)) {
            if (list.stream().anyMatch(item -> Objects.equals(item.getName(), menuUpdateVo.getName()))) {
                return R.fail("菜单名重复");
            }
            if (list.stream().anyMatch(item -> Objects.equals(item.getUrl(), menuUpdateVo.getUrl()))) {
                return R.fail("菜单路径重复");
            }
        }


        MenuEntity menuEntity = new MenuEntity();
        BeanUtils.copyProperties(menuUpdateVo, menuEntity);

        if (!this.updateById(menuEntity)) {
            R.fail("更新菜单失败");
        }

        return R.success("更新菜单成功");
    }
}
