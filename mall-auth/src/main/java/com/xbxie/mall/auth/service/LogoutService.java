package com.xbxie.mall.auth.service;

import com.xbxie.mall.common.utils.R;
import javax.servlet.http.HttpServletRequest;

/**
 * created by xbxie on 2024/5/23
 */
public interface LogoutService {
    R<Void> userLogout(HttpServletRequest request);

    R<Void> memberLogout(HttpServletRequest request);
}
