package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 用户控制器
 * created by xbxie on 2024/4/19
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody UserAddVo userAddVo) {
        return userService.add(userAddVo);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return userService.del(id);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody UserUpdateVo userUpdateVo) {
        return userService.updateUser(userUpdateVo);
    }

    @PostMapping("/pageList")
    public R<PageData<UserEntity>> pageList(@Validated @RequestBody UserPageVo userPageVo) {
        return userService.pageList(userPageVo);
    }

    @PostMapping("/status")
    public R<Void> changeStatus(@Validated @RequestBody UserStatusVo userStatusVo) {
        return userService.changeStatus(userStatusVo);
    }

    @PostMapping("/{id}")
    public R<UserDetailVo> getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }
}
