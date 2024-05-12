package com.xbxie.mall.admin.utils;

import com.xbxie.mall.admin.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * created by xbxie on 2024/5/14
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试Jwt工具")
public class JwtUtilsTest {
    @DisplayName("生成token")
    @Test
    void createToken() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("aaa");
        String token = JwtUtils.createToken(userEntity);
        Assertions.assertNotNull(token);
    }

    @DisplayName("解析token")
    @Test
    void parseToken() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("aaa");
        String token = JwtUtils.createToken(userEntity);
        UserEntity userEntity1 = JwtUtils.parseToken(token);
        Assertions.assertEquals(userEntity1.getId(), 1L);
        Assertions.assertEquals(userEntity1.getName(), "aaa");
    }
}
