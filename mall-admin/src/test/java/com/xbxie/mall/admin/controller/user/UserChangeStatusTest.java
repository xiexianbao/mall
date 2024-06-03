package com.xbxie.mall.admin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entityback.UserEntity;
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
import java.util.*;

/**
 * created by xbxie on 2024/5/15
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试更新用户状态接口")
public class UserChangeStatusTest {
    private static final String url = "/user/status";

    private static final String loginUrl = "/auth/login";

    private static final String addUserUrl = "/user/add";

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

    @DisplayName("更新用户状态")
    @Test
    void changeStatus() {
        UserEntity userEntityOld = userService.getById(userIds.get(0));
        UserStatusVo userStatusVo = new UserStatusVo();
        userStatusVo.setId(userEntityOld.getId());
        userStatusVo.setStatus(userEntityOld.getStatus() == 1 ? 0 : 1);
        testUtils.assertSuccess(url, userStatusVo, httpHeaders, "success");


        UserEntity userEntityNew = userService.getById(userIds.get(0));
        Assertions.assertEquals(userEntityNew.getStatus(), userStatusVo.getStatus());
    }

    @DisplayName("用户不存在")
    @Test
    void notExists() {
        Random random = new Random();

        UserStatusVo userStatusVo = new UserStatusVo();
        userStatusVo.setId(random.nextLong());
        userStatusVo.setStatus(1);

        testUtils.assertSuccess(url, userStatusVo, httpHeaders, "success");
    }

    @DisplayName("用户id/用户启用的值为null")
    @Test
    void idStatusIsNull() {
        // 验证 id 为 null
        UserStatusVo userStatusVo1 = new UserStatusVo();
        userStatusVo1.setId(null);
        userStatusVo1.setStatus(1);

        testUtils.assertFail(url, userStatusVo1, httpHeaders, "请输入用户id");


        // 验证 status 为 null
        UserStatusVo userStatusVo2 = new UserStatusVo();
        userStatusVo2.setId(userIds.get(0));
        userStatusVo2.setStatus(null);
        testUtils.assertFail(url, userStatusVo2, httpHeaders,"请输入用户启用状态");
    }
}
