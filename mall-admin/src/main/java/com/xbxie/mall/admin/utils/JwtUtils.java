package com.xbxie.mall.admin.utils;

import com.xbxie.mall.admin.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * created by xbxie on 2024/5/13
 */
public class JwtUtils {
    public static String createToken(UserEntity userEntity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", userEntity.getId());
        map.put("name", userEntity.getName());
        return Jwts.builder().setClaims(map).signWith(SignatureAlgorithm.HS256, "yG1RwgQhzAeQ57Z4g0hM").compact();
    }

    public static UserEntity parseToken(String token) {
        UserEntity userEntity = new UserEntity();
        Map<String, Object> map = (Map<String, Object>)Jwts.parser().setSigningKey("yG1RwgQhzAeQ57Z4g0hM").parseClaimsJws(token).getBody();
        userEntity.setId(Long.valueOf(map.get("id").toString()));
        userEntity.setName(map.get("name").toString());
        return userEntity;
    }
}
