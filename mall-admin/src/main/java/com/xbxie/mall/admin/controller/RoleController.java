package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.entity.CommonRoleEntity;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 角色控制器
 * created by xbxie on 2024/4/24
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody RoleAddVo roleAddVo) {
        return roleService.add(roleAddVo);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return roleService.del(id);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody RoleUpdateVo roleUpdateVo) {
        return roleService.updateRole(roleUpdateVo);
    }

    @PostMapping("/pageList")
    public R<PageData<CommonRoleEntity>> pageList(@Validated @RequestBody RolePageVo rolePageVo) {
        return roleService.pageList(rolePageVo);
    }

    @PostMapping("/{id}")
    public R<RoleDetailVo> getRole(@PathVariable("id") Long id) {
        return roleService.getRole(id);
    }
}
