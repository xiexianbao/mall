package com.xbxie.mall.admin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.service.UserRoleRelService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.*;

/**
 * 测试删除用户接口
 * created by xbxie on 2024/4/23
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试删除用户接口")
public class UserDelTest {

    private static final String url = "/user/del";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private UserRoleRelService userRoleRelService;

    private List<Long> userIds = new ArrayList<>();

    private List<Long> userRoleRelIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        Random random = new Random();
        // 新增三条测试数据
        for (int i = 0; i < 3; i++) {
            String name =  random.nextLong() + "";
            String account =  random.nextLong() + "";
            String password = random.nextLong() + "";

            UserEntity userEntity = new UserEntity();
            userEntity.setId(null);
            userEntity.setName(name);
            userEntity.setAccount(account);
            userEntity.setPassword(password);
            userEntity.setStatus(null);
            userEntity.setIsDel(null);
            userEntity.setCreateTime(null);
            userEntity.setUpdateTime(null);
            userService.save(userEntity);

            for (int j = 0; j < 3; j++) {
                UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
                userRoleRelEntity.setUserId(userEntity.getId());
                userRoleRelEntity.setRoleId(random.nextLong());

                userRoleRelService.save(userRoleRelEntity);
                userRoleRelIds.add(userRoleRelEntity.getId());
            }

            userIds.add(userEntity.getId());
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user", userIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user_role_rel", userRoleRelIds));
    }

    @DisplayName("删除用户")
    @Test
    void del() {
        Long userId = this.userIds.stream().findAny().get();

        R<Void> resData = testUtils.getResData(url + "/" + userId, null);

        // 进行返回结果
        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("删除用户成功", resData.getMessage());


        // 验证数据库中的数据
        // 验证用户表
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<UserEntity>().eq("id", userId);
        Assertions.assertEquals(0, userService.count(wrapper));

        // 验证用户角色关系表
        QueryWrapper<UserRoleRelEntity> wrapper1 = new QueryWrapper<UserRoleRelEntity>().eq("user_id", userId);
        Assertions.assertEquals(0, userRoleRelService.count(wrapper1));

    }

    @DisplayName("删除的用户不存在")
    @Test
    void notExists() {
        long id = new Random().nextLong();
        testUtils.simplePerformAssert(url + "/" + id, null, 500, "用户不存在");
    }
}
