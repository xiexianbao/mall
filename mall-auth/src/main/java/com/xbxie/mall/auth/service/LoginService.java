package com.xbxie.mall.auth.service;

import com.xbxie.mall.auth.vo.LoginAdminReqVo;
import com.xbxie.mall.auth.vo.LoginAdminResVo;
import com.xbxie.mall.auth.vo.LoginMemberReqVo;
import com.xbxie.mall.auth.vo.LoginMemberResVo;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/5/23
 */
public interface LoginService {
    R<LoginAdminResVo> userLogin(LoginAdminReqVo loginAdminReqVo);

    R<LoginMemberResVo> memberLogin(LoginMemberReqVo loginMemberReqVo);
}
