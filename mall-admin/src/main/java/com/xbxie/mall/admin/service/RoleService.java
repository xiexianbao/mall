package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.RoleAddVo;
import com.xbxie.mall.admin.vo.RoleDetailVo;
import com.xbxie.mall.admin.vo.RolePageVo;
import com.xbxie.mall.admin.vo.RoleUpdateVo;
import com.xbxie.mall.common.entity.CommonRoleEntity;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/4/25
 */
public interface RoleService {
    R<Void> add(RoleAddVo roleAddVo);

    R<Void> del(Long id);

    R<Void> updateRole(RoleUpdateVo roleUpdateVo);

    R<PageData<CommonRoleEntity>> pageList(RolePageVo rolePageVo);

    R<RoleDetailVo> getRole(Long id);
}
