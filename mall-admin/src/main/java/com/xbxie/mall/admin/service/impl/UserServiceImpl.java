package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.mapper.UserMapper;
import com.xbxie.mall.admin.service.UserRoleRelService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.common.utils.ValidationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
    @Resource
    private UserRoleRelService userRoleRelService;

    @Transactional
    @Override
    public R<Void> add(UserAddVo userAddVo) {
        // 用户名或者用户账号重复
        List<UserEntity> list = this.list(new QueryWrapper<UserEntity>().eq("name", userAddVo.getName()).or().eq("account", userAddVo.getAccount()));
        if (!CollectionUtils.isEmpty(list)) {
            if (list.stream().anyMatch(item -> Objects.equals(item.getName(), userAddVo.getName()))) {
                return R.fail("用户名重复");
            }
            if (list.stream().anyMatch(item -> Objects.equals(item.getAccount(), userAddVo.getAccount()))) {
                return R.fail("用户账号重复");
            }
        }

        // 合法化 status 的值
        if (!ValidationUtils.statusPass(userAddVo.getStatus())) {
            userAddVo.setStatus(1);
        }

        // 编码密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userAddVo.setPassword(encoder.encode(userAddVo.getPassword().trim()));

        // 插入用户
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userAddVo, userEntity);

        if (!this.save(userEntity)) {
            return R.fail("添加用户失败");
        }

        // 插入角色
        Long userId = userEntity.getId();
        List<Long> roleIdList = userAddVo.getRoleIdList();
        if (!CollectionUtils.isEmpty(roleIdList)) {
            if (
                !userRoleRelService.saveBatch(
                    roleIdList.stream().map(roleId -> {
                        UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
                        userRoleRelEntity.setUserId(userId);
                        userRoleRelEntity.setRoleId(roleId);
                        return userRoleRelEntity;
                    }).collect(Collectors.toList())
                )
            ) {
                throw  new CustomException("添加角色失败");
            }
        }

        return R.success("添加用户成功");
    }

    @Transactional
    @Override
    public R<Void> del(Long id) {
        // 用户不存在
        if (!this.exists(new QueryWrapper<UserEntity>().eq("id", id))) {
            return R.fail("用户不存在");
        }

        // 删除用户
        if (!this.removeById(id)) {
            return R.fail("删除用户失败");
        }


        // 删除用户角色关系
        QueryWrapper<UserRoleRelEntity> wrapper = new QueryWrapper<UserRoleRelEntity>().eq("user_id", id);
        if (userRoleRelService.exists(wrapper)) {
            if (!userRoleRelService.remove(wrapper)) {
                throw new CustomException("删除用户角色失败");
            }
        }


        return R.success("删除用户成功");
    }

    @Transactional
    @Override
    public R<Void> updateUser(UserUpdateVo userUpdateVo) {

        // 更新的用户不存在
        Long id = userUpdateVo.getId();
        if (this.getById(id) == null) {
            return R.fail("用户不存在");
        }

        // 用户名或者用户账号重复
        List<UserEntity> list = this.list(
            new QueryWrapper<UserEntity>()
                .ne("id", id)
                .and(i -> i.eq("name", userUpdateVo.getName()).or().eq("account", userUpdateVo.getAccount()))
        );
        if (!CollectionUtils.isEmpty(list)) {
            if (list.stream().anyMatch(item -> Objects.equals(item.getName(), userUpdateVo.getName()))) {
                return R.fail("用户名重复");
            }
            if (list.stream().anyMatch(item -> Objects.equals(item.getAccount(), userUpdateVo.getAccount()))) {
                return R.fail("用户账号重复");
            }
        }

        // 合法化status的值
        if (!ValidationUtils.statusPass(userUpdateVo.getStatus())) {
            userUpdateVo.setStatus(1);
        }

        // 设置密码
        if (userUpdateVo.getPassword().equals(this.getById(userUpdateVo.getId()).getPassword())) {
            userUpdateVo.setPassword(null);
        } else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userUpdateVo.setPassword(encoder.encode(userUpdateVo.getPassword().trim()));
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userUpdateVo, userEntity);

        if (!this.updateById(userEntity)) {
            R.fail("更新用户失败");
        }

        // 更新用户的角色
        // 先删除用户的旧角色
        this.userRoleRelService.remove(new QueryWrapper<UserRoleRelEntity>().eq("user_id", id));
        // 添加新角色
        List<Long> roleIdList = userUpdateVo.getRoleIdList();
        if (!CollectionUtils.isEmpty(roleIdList)) {
            if (
                !userRoleRelService.saveBatch(roleIdList.stream().map(roleId -> {
                    UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
                    userRoleRelEntity.setUserId(id);
                    userRoleRelEntity.setRoleId(roleId);
                    return userRoleRelEntity;
                }).collect(Collectors.toList()))
            ) {
                throw new CustomException("更新用户角色失败");
            }
        }

        return R.success("更新用户成功");
    }

    @Override
    public R<PageData<UserEntity>> pageList(UserPageVo userPageVo) {
        String name = userPageVo.getName();
        String account = userPageVo.getAccount();

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasLength(account)) {
            wrapper.like("account", account);
        }

        Page<UserEntity> res = this.page(new Page<>(userPageVo.getPageNum(), userPageVo.getPageSize()), wrapper);
        PageData<UserEntity> pageData = PageData.getPageData(res);
        pageData.getList().forEach(userEntity -> userEntity.setPassword(null));
        return R.success(pageData);
    }

    @Override
    public R<Void> changeStatus(UserStatusVo userStatusVo) {
        UserEntity userEntity = this.getById(userStatusVo.getId());
        if (userEntity == null) {
            return R.fail("用户不存在");
        }

        // 合法化 status 的值
        if (!ValidationUtils.statusPass(userStatusVo.getStatus())) {
            userStatusVo.setStatus(1);
        }

        BeanUtils.copyProperties(userStatusVo, userEntity);

        this.updateById(userEntity);

        return this.updateById(userEntity) ? R.success() : R.fail();
    }

    @Override
    public R<UserDetailVo> getUser(Long id) {
        UserEntity userEntity = this.getById(id);
        if (userEntity == null) {
            throw new CustomException("用户不存在");
        }

        UserDetailVo userDetailVo = new UserDetailVo();
        BeanUtils.copyProperties(userEntity, userDetailVo);
        // TODO: 设置 roleIdList 的值
        return R.success(userDetailVo);
    }
}
