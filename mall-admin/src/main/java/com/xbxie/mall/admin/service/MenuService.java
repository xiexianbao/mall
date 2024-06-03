package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.MenuAddVo;
import com.xbxie.mall.admin.vo.MenuDetailVo;
import com.xbxie.mall.admin.vo.MenuPageVo;
import com.xbxie.mall.admin.vo.MenuUpdateVo;
import com.xbxie.mall.common.entity.CommonMenuEntity;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/4/25
 */
public interface MenuService {
    R<Void> add(MenuAddVo menuAddVo);

    R<Void> del(Long id);

    R<PageData<CommonMenuEntity>> pageList(MenuPageVo menuPageVo);

    R<Void> updateMenu(MenuUpdateVo menuUpdateVo);

    R<MenuDetailVo> getMenu(Long id);
}
