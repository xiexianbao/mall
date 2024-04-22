package com.xbxie.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.vo.UserAddVo;

public interface UserService extends IService<UserEntity> {
    boolean add(UserAddVo userAddVo);
}
