package com.xbxie.mall.auth.service.xml;

import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.constant.AuthConstant;
import com.xbxie.mall.auth.service.LogoutService;
import com.xbxie.mall.common.utils.JwtUtils;
import com.xbxie.mall.common.utils.R;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * created by xbxie on 2024/5/23
 */
@Service("logoutService")
public class LogoutServiceImpl implements LogoutService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<Void> userLogout(HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);
        if (tokenBo == null) {
            return R.fail("退出登录失败");
        }

        Long id = tokenBo.getId();
        if (id == null) {
            return R.fail("退出登录失败");
        }

        String key = AuthConstant.REDIS_TOKEN_USER_PREFIX + id;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            return R.fail("退出登录失败");
        }

        if (Boolean.FALSE.equals(stringRedisTemplate.delete(key))) {
            return R.fail("退出登录失败");
        }

        return R.success("退出登录成功");
    }

    @Override
    public R<Void> memberLogout(HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);
        if (tokenBo == null) {
            return R.fail("退出登录失败");
        }

        Long id = tokenBo.getId();
        if (id == null) {
            return R.fail("退出登录失败");
        }

        String key = AuthConstant.REDIS_TOKEN_MEMBER_PREFIX + id;
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            return R.fail("退出登录失败");
        }

        if (Boolean.FALSE.equals(stringRedisTemplate.delete(key))) {
            return R.fail("退出登录失败");
        }

        return R.success("退出登录成功");
    }
}
