package com.xbxie.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xbxie.mall.admin.entity.MenuEntity;
import com.xbxie.mall.admin.vo.MenuAddVo;
import com.xbxie.mall.admin.vo.MenuPageVo;
import com.xbxie.mall.admin.vo.MenuUpdateVo;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/4/25
 */
public interface MenuService extends IService<MenuEntity> {
    R add(MenuAddVo menuAddVo);

    R del(Long id);

    R<PageData<MenuEntity>> pageList(MenuPageVo menuPageVo);

    R updateMenu(MenuUpdateVo menuUpdateVo);
}
