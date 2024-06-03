package com.xbxie.mall.auth.service;

import com.xbxie.mall.auth.vo.RegisterMemberReqVo;
import com.xbxie.mall.auth.vo.RegisterMemberResVo;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024/5/23
 */
public interface RegisterService {
    R<RegisterMemberResVo> memberRegister(RegisterMemberReqVo registerMemberReqVo);
}
