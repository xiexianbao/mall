package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.vo.UserAddVo;
import com.xbxie.mall.admin.vo.UserPageVo;
import com.xbxie.mall.admin.vo.UserUpdateVo;
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
    public R add(@Validated @RequestBody UserAddVo userAddVo) {
        return userService.add(userAddVo);
    }

    @PostMapping("/del/{id}")
    public R del(@PathVariable("id") Long id) {
        return userService.del(id);
    }

    @PostMapping("/update")
    public R update(@Validated @RequestBody UserUpdateVo userUpdateVo) {
        return userService.updateUser(userUpdateVo);
    }

    @PostMapping("/pageList")
    public R<PageData<UserEntity>> pageList(@Validated @RequestBody UserPageVo userPageVo) {
        return userService.pageList(userPageVo);
    }
}
