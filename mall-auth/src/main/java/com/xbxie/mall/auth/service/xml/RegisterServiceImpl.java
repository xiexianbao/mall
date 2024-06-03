package com.xbxie.mall.auth.service.xml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.auth.service.RegisterService;
import com.xbxie.mall.common.utils.JwtUtils;
import com.xbxie.mall.auth.vo.RegisterMemberReqVo;
import com.xbxie.mall.auth.vo.RegisterMemberResVo;
import com.xbxie.mall.common.entity.CommonMemberEntity;
import com.xbxie.mall.common.service.CommonMemberService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;

/**
 * created by xbxie on 2024/5/23
 */
@Service("registerService")
public class RegisterServiceImpl implements RegisterService {
    @Resource
    private CommonMemberService commonMemberService;

    @Override
    public R<RegisterMemberResVo> memberRegister(RegisterMemberReqVo registerMemberReqVo) {
        // 账号重复
        if (!CollectionUtils.isEmpty(
            commonMemberService.list(new QueryWrapper<CommonMemberEntity>().eq("account", registerMemberReqVo.getAccount()))
        )) {
            throw new CustomException("账号重复");
        }

        // 编码密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        registerMemberReqVo.setPassword(encoder.encode(registerMemberReqVo.getPassword().trim()));

        // 注册用户
        CommonMemberEntity commonMemberEntity = new CommonMemberEntity();
        BeanUtils.copyProperties(registerMemberReqVo, commonMemberEntity);

        if (!commonMemberService.save(commonMemberEntity)) {
            throw new CustomException("注册失败");
        }


        TokenBo tokenBo = new TokenBo();
        BeanUtils.copyProperties(commonMemberEntity, tokenBo);
        String token = JwtUtils.createToken(tokenBo);

        RegisterMemberResVo.User user = new RegisterMemberResVo.User();
        BeanUtils.copyProperties(commonMemberEntity, user);

        RegisterMemberResVo registerMemberResVo = new RegisterMemberResVo();
        registerMemberResVo.setToken(token);
        registerMemberResVo.setUser(user);

        return R.success(registerMemberResVo);
    }
}
