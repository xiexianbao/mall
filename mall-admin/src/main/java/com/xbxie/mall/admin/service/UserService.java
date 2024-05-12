package com.xbxie.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.vo.UserAddVo;
import com.xbxie.mall.admin.vo.UserPageVo;
import com.xbxie.mall.admin.vo.UserUpdateVo;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

public interface UserService extends IService<UserEntity> {
    R<Void> add(UserAddVo userAddVo);
    R<Void> del(Long id);
    R<Void> updateUser(UserUpdateVo userUpdateVo);
    R<PageData<UserEntity>> pageList(UserPageVo userPageVo);
}
