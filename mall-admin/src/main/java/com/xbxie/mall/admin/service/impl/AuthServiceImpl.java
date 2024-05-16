package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.AuthService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.JwtUtils;
import com.xbxie.mall.admin.vo.LoginReqVo;
import com.xbxie.mall.admin.vo.LoginResVo;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * created by xbxie on 2024/5/13
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<LoginResVo> login(LoginReqVo loginReqVo) {
        // 根据用户账号查看数据库中是否有对应的用户
        UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("account", loginReqVo.getAccount()));
        if (userEntity == null) {
            throw new CustomException("账号不存在");
        }

        if (userEntity.getStatus() == 0) {
            throw new CustomException("账号被禁用");
        }

        // 判断密码是否相等
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (
            !encoder.matches(loginReqVo.getPassword(), userEntity.getPassword())
        ) {

            throw new CustomException("密码错误");
        }


        // 生成 token ，并存入 redis
        LoginResVo loginResVo = new LoginResVo();

        String token = JwtUtils.createToken(userEntity);
        LoginResVo.User user = new LoginResVo.User();
        BeanUtils.copyProperties(userEntity, user);

        loginResVo.setToken(token);
        loginResVo.setUser(user);
        stringRedisTemplate.opsForValue().set(userEntity.getId().toString(), token);

        return R.success(loginResVo);
    }

    @Override
    public R<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if(!StringUtils.hasLength(token)) {
            return R.fail("退出登录失败");
        }

        UserEntity userEntity = JwtUtils.parseToken(token);
        Long id = userEntity.getId();
        if (id == null) {
            return R.fail("退出登录失败");
        }

        String key = id.toString();
        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            return R.fail("退出登录失败");
        }

        if (Boolean.FALSE.equals(stringRedisTemplate.delete(key))) {
            return R.fail("退出登录失败");
        }

        return R.success("退出登录成功");
    }
}
