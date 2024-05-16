package com.xbxie.mall.admin.controller.user;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.AuthService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.LoginReqVo;
import com.xbxie.mall.admin.vo.LoginResVo;
import com.xbxie.mall.admin.vo.UserAddVo;
import com.xbxie.mall.admin.vo.UserStatusVo;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * created by xbxie on 2024/5/16
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试获取用户详情接口")
public class UserDetailTest {
    private final String url = "/user/";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private AuthService authService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private List<Long> userIds = new ArrayList<>();

    private HttpHeaders httpHeaders = new HttpHeaders();


    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        Random random = new Random();

        // 添加用户
        String password = random.nextLong() + "";
        UserAddVo userAddVo = new UserAddVo();
        userAddVo.setName(random.nextLong() + "");
        userAddVo.setAccount(random.nextLong() + "");
        userAddVo.setPassword(password);
        userAddVo.setStatus(1);
        userAddVo.setRoleIdList(null);

        userService.add(userAddVo);

        UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", userAddVo.getName()).eq("account", userAddVo.getAccount()));
        userIds.add(userEntity.getId());

        // 登录获取 token
        LoginReqVo loginReqVo = new LoginReqVo();
        loginReqVo.setAccount(userAddVo.getAccount());
        loginReqVo.setPassword(password);
        R<LoginResVo> loginResVo = authService.login(loginReqVo);
        httpHeaders.add("token", loginResVo.getData().getToken());
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user", userIds));
        for (Long userId : userIds) {
            stringRedisTemplate.delete(userId.toString());
        }
    }

    @DisplayName("获取用户详情")
    @Test
    void getUser() {
        R<UserEntity> resData = testUtils.getResData(url + this.userIds.get(0), null, httpHeaders, new TypeReference<R<UserEntity>>() {
        });
        Assertions.assertNotNull(resData.getData());
        Assertions.assertNotNull(resData.getData().getId());
    }

    @DisplayName("用户不存在")
    @Test
    void notExists() {
        Random random = new Random();
        testUtils.assertFail(url + random.nextLong(), null, httpHeaders, "用户不存在");
    }
}
