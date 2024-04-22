package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.vo.UserAddVo;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 用户控制器
 * created by xbxie on 2024/4/19
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public R add(@Validated @RequestBody UserAddVo userAddVo) {
        boolean success = userService.add(userAddVo);
        return success ? R.success("添加用户成功") : R.fail("添加用户失败");
    }

    @RequestMapping("/add1")
    public @ResponseBody R addUser1() {
        System.out.println("addUser");
        return R.success("test11");
    }

    @RequestMapping("/add2")
    public String addUser2() {
        return "test22";
    }
}
