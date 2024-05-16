package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.entity.MenuEntity;
import com.xbxie.mall.admin.service.MenuService;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 菜单控制器
 * created by xbxie on 2024/4/25
 */
@RestController
@RequestMapping("/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody MenuAddVo menuAddVo) {
        return menuService.add(menuAddVo);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return menuService.del(id);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody MenuUpdateVo menuUpdateVo) {
        return menuService.updateMenu(menuUpdateVo);
    }

    @PostMapping("/pageList")
    public R<PageData<MenuEntity>> pageList(@Validated @RequestBody MenuPageVo menuPageVo) {
        return menuService.pageList(menuPageVo);
    }

    @PostMapping("/{id}")
    public R<MenuDetailVo> getMenu(@PathVariable("id") Long id) {
        return menuService.getMenu(id);
    }
}
