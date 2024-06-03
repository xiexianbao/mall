package com.xbxie.mall.auth.service.xml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.constant.AuthConstant;
import com.xbxie.mall.auth.service.LoginService;
import com.xbxie.mall.common.utils.JwtUtils;
import com.xbxie.mall.auth.vo.LoginAdminReqVo;
import com.xbxie.mall.auth.vo.LoginAdminResVo;
import com.xbxie.mall.auth.vo.LoginMemberReqVo;
import com.xbxie.mall.auth.vo.LoginMemberResVo;
import com.xbxie.mall.common.entity.CommonMemberEntity;
import com.xbxie.mall.common.entity.CommonUserEntity;
import com.xbxie.mall.common.service.CommonMemberService;
import com.xbxie.mall.common.service.CommonUserService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * created by xbxie on 2024/5/23
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {
    @Resource
    private CommonUserService commonUserService;

    @Resource
    private CommonMemberService commonMemberService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<LoginAdminResVo> userLogin(LoginAdminReqVo loginAdminReqVo) {
        // 根据用户账号查看数据库中是否有对应的用户
        CommonUserEntity commonUserEntity = commonUserService.getOne(new QueryWrapper<CommonUserEntity>().eq("account", loginAdminReqVo.getAccount()));
        if (commonUserEntity == null) {
            throw new CustomException("账号不存在");
        }

        if (commonUserEntity.getStatus() == 0) {
            throw new CustomException("账号被禁用");
        }

        // 判断密码是否相等
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (
            !encoder.matches(loginAdminReqVo.getPassword(), commonUserEntity.getPassword())
        ) {

            throw new CustomException("密码错误");
        }

        // 生成 token ，并存入 redis
        TokenBo tokenBo = new TokenBo();
        BeanUtils.copyProperties(commonUserEntity, tokenBo);
        String token = JwtUtils.createToken(tokenBo);

        LoginAdminResVo.User user = new LoginAdminResVo.User();
        BeanUtils.copyProperties(commonUserEntity, user);

        LoginAdminResVo loginAdminResVo = new LoginAdminResVo();
        loginAdminResVo.setToken(token);
        loginAdminResVo.setUser(user);
        stringRedisTemplate.opsForValue().set(AuthConstant.REDIS_TOKEN_USER_PREFIX + commonUserEntity.getId().toString(), token);

        return R.success(loginAdminResVo);
    }

    @Override
    public R<LoginMemberResVo> memberLogin(LoginMemberReqVo loginMemberReqVo) {
        // 根据用户账号查看数据库中是否有对应的用户
        CommonMemberEntity commonMemberEntity = commonMemberService.getOne(new QueryWrapper<CommonMemberEntity>().eq("account", loginMemberReqVo.getAccount()));
        if (commonMemberEntity == null) {
            throw new CustomException("账号不存在");
        }


        // 判断密码是否相等
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (
            !encoder.matches(loginMemberReqVo.getPassword(), commonMemberEntity.getPassword())
        ) {

            throw new CustomException("密码错误");
        }


        // 生成 token ，并存入 redis
        TokenBo tokenBo = new TokenBo();
        BeanUtils.copyProperties(commonMemberEntity, tokenBo);
        String token = JwtUtils.createToken(tokenBo);

        LoginMemberResVo.User user = new LoginMemberResVo.User();
        BeanUtils.copyProperties(commonMemberEntity, user);

        LoginMemberResVo loginMemberResVo = new LoginMemberResVo();
        loginMemberResVo.setToken(token);
        loginMemberResVo.setUser(user);
        stringRedisTemplate.opsForValue().set(AuthConstant.REDIS_TOKEN_MEMBER_PREFIX + commonMemberEntity.getId().toString(), token);

        return R.success(loginMemberResVo);
    }
}
