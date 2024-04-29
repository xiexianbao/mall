package com.xbxie.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.vo.RoleAddVo;
import com.xbxie.mall.admin.vo.RolePageVo;
import com.xbxie.mall.admin.vo.RoleUpdateVo;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/4/25
 */
public interface RoleService extends IService<RoleEntity> {
    R add(RoleAddVo roleAddVo);

    R del(Long id);

    R updateRole(RoleUpdateVo roleUpdateVo);

    R<PageData<RoleEntity>> pageList(RolePageVo rolePageVo);
}
