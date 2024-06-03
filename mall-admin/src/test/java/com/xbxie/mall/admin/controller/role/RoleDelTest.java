package com.xbxie.mall.admin.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.common.entity.CommonRoleEntity;
import com.xbxie.mall.common.entity.CommonRoleMenuRelEntity;
import com.xbxie.mall.common.service.CommonRoleMenuRelService;
import com.xbxie.mall.common.service.CommonRoleService;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private CommonRoleService commonRoleService;

    @Resource
    private CommonRoleMenuRelService commonRoleMenuRelService;

    private List<Long> roleIds = new ArrayList<>();

    private List<Long> roleMenuRelIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        Random random = new Random();
        // 新增三条测试数据
        for (int i = 0; i < 3; i++) {
            CommonRoleEntity roleEntity = new CommonRoleEntity();
            roleEntity.setId(null);
            roleEntity.setName(random.nextLong() + "");
            roleEntity.setIsDel(null);
            roleEntity.setCreateTime(null);
            roleEntity.setUpdateTime(null);
            commonRoleService.save(roleEntity);

            for (int j = 0; j < 3; j++) {
                CommonRoleMenuRelEntity roleMenuRelEntity = new CommonRoleMenuRelEntity();
                roleMenuRelEntity.setRoleId(roleEntity.getId());
                roleMenuRelEntity.setMenuId(random.nextLong());

                commonRoleMenuRelService.save(roleMenuRelEntity);
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

        R<Void> resData = testUtils.getResData(url + "/" + roleId, null);

        // 进行返回结果
        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("删除角色成功", resData.getMsg());


        // 验证数据库中的数据
        // 验证角色表
        QueryWrapper<CommonRoleEntity> wrapper = new QueryWrapper<CommonRoleEntity>().eq("id", roleId);
        Assertions.assertEquals(0, commonRoleService.count(wrapper));

        // 验证角色菜单关系表
        QueryWrapper<CommonRoleMenuRelEntity> wrapper1 = new QueryWrapper<CommonRoleMenuRelEntity>().eq("role_id", roleId);
        Assertions.assertEquals(0, commonRoleMenuRelService.count(wrapper1));
    }

    @DisplayName("删除的角色不存在")
    @Test
    void notExists() {
        long id = new Random().nextLong();
        testUtils.simplePerformAssert(url + "/" + id, null, 500, "角色不存在");
    }
}
