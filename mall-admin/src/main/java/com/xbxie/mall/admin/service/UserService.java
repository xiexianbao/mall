package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.entity.CommonUserEntity;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;

public interface UserService {
    R<Void> add(UserAddVo userAddVo);
    R<Void> del(Long id);
    R<Void> updateUser(UserUpdateVo userUpdateVo);
    R<PageData<CommonUserEntity>> pageList(UserPageVo userPageVo);

    R<Void> changeStatus(UserStatusVo userStatusVo);

    R<UserDetailVo> getUser(Long id);
}
