package com.xbxie.mall.admin.controller.role;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.AuthService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.LoginReqVo;
import com.xbxie.mall.admin.vo.LoginResVo;
import com.xbxie.mall.admin.vo.UserAddVo;
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
@DisplayName("测试获取角色详情接口")
public class RoleDetailTest {
    private final String url = "/role/";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private AuthService authService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private List<Long> userIds = new ArrayList<>();

    private List<Long> roleIds = new ArrayList<>();

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

        // 添加角色
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(random.nextLong() + "");
        roleService.save(roleEntity);
        roleIds.add(roleEntity.getId());
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user", userIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role", roleIds));
        for (Long userId : userIds) {
            stringRedisTemplate.delete(userId.toString());
        }
    }

    @DisplayName("获取角色详情")
    @Test
    void getRole() {
        R<RoleEntity> resData = testUtils.getResData(url + this.roleIds.get(0), null, httpHeaders, new TypeReference<R<RoleEntity>>() {
        });
        Assertions.assertNotNull(resData.getData());
        Assertions.assertNotNull(resData.getData().getId());
    }

    @DisplayName("角色不存在")
    @Test
    void notExists() {
        Random random = new Random();
        testUtils.assertFail(url + random.nextLong(), null, httpHeaders, "角色不存在");
    }
}
