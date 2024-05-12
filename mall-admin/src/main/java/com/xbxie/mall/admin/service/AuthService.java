package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.LoginReqVo;
import com.xbxie.mall.admin.vo.LoginResVo;
import com.xbxie.mall.common.utils.R;
import javax.servlet.http.HttpServletRequest;

/**
 * created by xbxie on 2024/5/13
 */
public interface AuthService {
    R<LoginResVo> login(LoginReqVo loginReqVo);

    R<Void> logout(HttpServletRequest request);
}
