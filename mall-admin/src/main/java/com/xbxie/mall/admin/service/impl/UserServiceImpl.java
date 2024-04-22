package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.mapper.UserMapper;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.vo.UserAddVo;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.ValidationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Override
    public boolean add(UserAddVo userAddVo) {
        String name = userAddVo.getName();
        String account = userAddVo.getAccount();

        // 插入的用户不能重名，不能重账号
        if (
            !CollectionUtils.isEmpty(
                this.list(new QueryWrapper<UserEntity>().eq("name", name).or().eq("account", account))
            )
        ) {
            throw new CustomException("用户名或账号已存在");
        }


        if (!ValidationUtils.statusPass(userAddVo.getStatus())) {
            userAddVo.setStatus(1);
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userAddVo, userEntity);

        return this.save(userEntity);
    }
}
