package com.xbxie.mall.common.utils;

import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.constant.AuthConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * created by xbxie on 2024/5/13
 */
public class JwtUtils {
    private static final String SIGNING_KEY = "yG1RwgQhzAeQ57Z4g0hM";

    public static String createToken(TokenBo tokenBo) {
        Map<String, Object> map = new HashMap<>();

        map.put("id", tokenBo.getId());
        map.put("name", tokenBo.getName());
        map.put("account", tokenBo.getAccount());

        return Jwts.builder().setClaims(map).signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
    }

    public static TokenBo parseToken(String token) {
        TokenBo tokenBo = new TokenBo();
        Map<String, Object> map = Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();

        Object idObj = map.get("id");
        if (idObj != null) {
            Long id = idObj instanceof Integer ? Long.parseLong(idObj.toString()) : (Long)idObj;
            tokenBo.setId(id);
        }
        String name = (String) map.get("name");
        if (StringUtils.hasLength(name)) {
            tokenBo.setName(name);
        }
        String account = (String) map.get("account");
        if (StringUtils.hasLength(account)) {
            tokenBo.setAccount(account);
        }

        return tokenBo;
    }

    public static TokenBo parseToken(HttpServletRequest request) {

        String authorization = request.getHeader(AuthConstant.JWT_TOKEN_HEADER);
        if(!StringUtils.hasLength(authorization)) {
            return null;
        }

        String token = authorization.replace(AuthConstant.JWT_TOKEN_PREFIX, "");
        if(!StringUtils.hasLength(token)) {
            return null;
        }

        return parseToken(token);
    }
}
