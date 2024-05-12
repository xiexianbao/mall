package com.xbxie.mall.admin.controller;


import com.xbxie.mall.admin.service.AuthService;
import com.xbxie.mall.admin.vo.LoginReqVo;
import com.xbxie.mall.admin.vo.LoginResVo;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限控制器
 * created by xbxie on 2024/5/13
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public R<LoginResVo> login(@Validated @RequestBody LoginReqVo loginReqVo) {
        return authService.login(loginReqVo);
    }

    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
