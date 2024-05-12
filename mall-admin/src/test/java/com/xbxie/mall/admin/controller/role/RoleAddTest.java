package com.xbxie.mall.admin.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.service.RoleMenuRelService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.RoleAddVo;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试新增角色接口
 * created by xbxie on 2024/4/25
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试新增角色接口")
public class RoleAddTest {
    private static final String url = "/role/add";

    @Resource
    private TestUtils testUtils;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuRelService roleMenuRelService;

    public RoleAddVo getNewRoleAddVo() {
        Random random = new Random();
        RoleAddVo roleAddVo = new RoleAddVo();
        roleAddVo.setName(random.nextLong() + "");
        roleAddVo.setMenuIdList(java.util.Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong()));
        return roleAddVo;
    }

    @DisplayName("添加角色")
    @Test
    void add() {
        RoleAddVo roleAddVo = getNewRoleAddVo();
        testUtils.assertSuccess(url, roleAddVo, "添加角色成功");

        RoleEntity roleEntity = roleService.getOne(new QueryWrapper<RoleEntity>().eq("name", roleAddVo.getName()));
        List<RoleMenuRelEntity> roleMenuRelEntities = roleMenuRelService.list(new QueryWrapper<RoleMenuRelEntity>().eq("role_id", roleEntity.getId()));

        Assertions.assertTrue(testUtils.deletePhysical("ums_role", roleEntity.getId()));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role_menu_rel", roleMenuRelEntities.stream().map(RoleMenuRelEntity::getId).collect(Collectors.toList())));


        Assertions.assertNotNull(roleEntity);
        // id
        Assertions.assertNotNull(roleEntity.getId());
        // name
        Assertions.assertEquals(roleAddVo.getName(), roleEntity.getName());
        // isDel
        Assertions.assertEquals(0, roleEntity.getIsDel());
        // createTime
        Assertions.assertNotNull(roleEntity.getCreateTime());
        // updateTime
        Assertions.assertNotNull(roleEntity.getUpdateTime());


        if (!CollectionUtils.isEmpty(roleMenuRelEntities)) {
            Assertions.assertEquals(
                roleAddVo.getMenuIdList().stream().sorted().map(String::valueOf).collect(Collectors.joining()),
                roleMenuRelEntities.stream().map(RoleMenuRelEntity::getMenuId).sorted().map(String::valueOf).collect(Collectors.joining())
            );

            for (RoleMenuRelEntity roleMenuRelEntity : roleMenuRelEntities) {
                // id
                Assertions.assertNotNull(roleMenuRelEntity.getId());
                // roleId
                Assertions.assertEquals(roleEntity.getId(), roleMenuRelEntity.getRoleId());
                // isDel
                Assertions.assertEquals(0, roleMenuRelEntity.getIsDel());
                // createTime
                Assertions.assertNotNull(roleMenuRelEntity.getCreateTime());
                // updateTime
                Assertions.assertNotNull(roleMenuRelEntity.getUpdateTime());
            }
        }
    }

    @DisplayName("角色名重复")
    @Test
    void nameRepeat() {
        Random random = new Random();
        String r1r2Name = random.nextLong() + "";

        RoleAddVo roleAddVo1 = new RoleAddVo();
        roleAddVo1.setName(r1r2Name);

        RoleAddVo roleAddVo2 = new RoleAddVo();
        roleAddVo2.setName(r1r2Name);

        testUtils.simplePerformAssert(url, roleAddVo1, 0, "添加角色成功");
        testUtils.simplePerformAssert(url, roleAddVo2, 500, "角色名重复");

        RoleEntity roleEntity = roleService.getOne(new QueryWrapper<RoleEntity>().eq("name", r1r2Name));
        Assertions.assertTrue(testUtils.deletePhysical("ums_role", roleEntity.getId()));

        Assertions.assertNotNull(roleEntity);
    }

    @DisplayName("角色名非法，值为null/空字符串/包含多个空白字符的空字符串")
    @Test
    void nameIsBlank() {
        for (String name : Arrays.array(null, "", "  ")) {
            RoleAddVo roleAddVo = new RoleAddVo();
            roleAddVo.setName(name);
            testUtils.assertFail(url, roleAddVo, "请输入角色名");
        }
    }
}
