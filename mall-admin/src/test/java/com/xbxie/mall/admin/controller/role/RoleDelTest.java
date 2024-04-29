package com.xbxie.mall.admin.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.service.RoleMenuRelService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.service.impl.RoleServiceImpl;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 测试删除角色接口
 * created by xbxie on 2024/4/25
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试删除角色接口")
public class RoleDelTest {
    private static final String url = "/role/del";

    @Resource
    private TestUtils testUtils;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuRelService roleMenuRelService;

    private List<Long> roleIds = new ArrayList<>();

    private List<Long> roleMenuRelIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        Random random = new Random();
        // 新增三条测试数据
        for (int i = 0; i < 3; i++) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setId(null);
            roleEntity.setName(random.nextLong() + "");
            roleEntity.setIsDel(null);
            roleEntity.setCreateTime(null);
            roleEntity.setUpdateTime(null);
            roleService.save(roleEntity);

            for (int j = 0; j < 3; j++) {
                RoleMenuRelEntity roleMenuRelEntity = new RoleMenuRelEntity();
                roleMenuRelEntity.setRoleId(roleEntity.getId());
                roleMenuRelEntity.setMenuId(random.nextLong());

                roleMenuRelService.save(roleMenuRelEntity);
                roleMenuRelIds.add(roleMenuRelEntity.getId());
            }

            roleIds.add(roleEntity.getId());
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role", roleIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role_menu_rel", roleMenuRelIds));
    }

    @DisplayName("删除角色")
    @Test
    void del() {
        Long roleId = this.roleIds.stream().findAny().get();

        R resData = testUtils.getResData(url + "/" + roleId, null);

        // 进行返回结果
        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("删除角色成功", resData.getMessage());


        // 验证数据库中的数据
        // 验证角色表
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<RoleEntity>().eq("id", roleId);
        Assertions.assertEquals(0, roleService.count(wrapper));

        // 验证角色菜单关系表
        QueryWrapper<RoleMenuRelEntity> wrapper1 = new QueryWrapper<RoleMenuRelEntity>().eq("role_id", roleId);
        Assertions.assertEquals(0, roleMenuRelService.count(wrapper1));
    }

    @DisplayName("删除的角色不存在")
    @Test
    void notExists() {
        long id = new Random().nextLong();
        testUtils.simplePerformAssert(url + "/" + id, null, 500, "角色不存在");
    }
}
