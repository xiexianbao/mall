package com.xbxie.mall.admin.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.service.impl.RoleMenuRelServiceImpl;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.RoleUpdateVo;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试更新角色接口
 * created by xbxie on 2024/4/25
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试更新角色接口")
public class RoleUpdateTest {
    private static final String url = "/role/update";

    @Resource
    private TestUtils testUtils;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuRelServiceImpl roleMenuRelService;


    private List<Long> roleIds = new ArrayList<>();

    private List<Long> roleMenuRelIds = new ArrayList<>();

    private List<RoleEntity> roleEntities = new ArrayList<>();

    private List<RoleMenuRelEntity> roleMenuRelEntities = new ArrayList<>();

    private Map<RoleEntity, List<RoleMenuRelEntity>> roleMenuMap = new HashMap<>();

    public RoleUpdateVo getNewRoleUpdateVo() {
        Random random = new Random();
        Long id = System.currentTimeMillis();
        String name =  random.nextLong() + "";

        RoleUpdateVo roleUpdateVo = new RoleUpdateVo();
        roleUpdateVo.setId(id);
        roleUpdateVo.setName(name);
        return roleUpdateVo;
    }

    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        // 添加用户
        for (int i = 1; i <= 30; i++) {
            RoleEntity roleEntity = new RoleEntity();

            roleEntity.setId(null);
            roleEntity.setName(i + "");
            roleEntity.setIsDel(0);
            roleEntity.setCreateTime(null);
            roleEntity.setUpdateTime(null);

            roleService.save(roleEntity);
            RoleEntity roleEntity1 = roleService.getById(roleEntity.getId());
            roleEntities.add(roleEntity1);
            roleIds.add(roleEntity1.getId());
            roleMenuMap.put(roleEntity1, new ArrayList<>());
            // 添加用户角色
            for (int j = 1; j <= 3; j++) {
                RoleMenuRelEntity roleMenuRelEntity = new RoleMenuRelEntity();
                roleMenuRelEntity.setRoleId(roleEntity1.getId());
                roleMenuRelEntity.setMenuId((long) i);

                roleMenuRelService.save(roleMenuRelEntity);
                RoleMenuRelEntity roleMenuRelEntity1 = roleMenuRelService.getById(roleMenuRelEntity.getId());
                roleMenuMap.get(roleEntity1).add(roleMenuRelEntity1);
                roleMenuRelEntities.add(roleMenuRelEntity1);
                roleMenuRelIds.add(roleMenuRelEntity1.getId());
            }
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role", roleIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role_menu_rel", roleMenuRelIds));
    }

    @DisplayName("更新角色")
    @Test
    void update() throws Exception {

        Random random = new Random();
        // 从库里面取数据，制作请求参数
        RoleEntity roleEntityBefore = this.roleEntities.stream().findAny().get();
        List<RoleMenuRelEntity> roleMenuRelEntitiesBefore = roleMenuMap.get(roleEntityBefore);

        RoleUpdateVo roleUpdateVo = new RoleUpdateVo();
        RoleUpdateVo newRoleUpdateVo = getNewRoleUpdateVo();
        roleUpdateVo.setId(roleEntityBefore.getId());
        roleUpdateVo.setName(newRoleUpdateVo.getName());
        roleUpdateVo.setMenuIdList(java.util.Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong()));

        Thread.sleep(1000);
        testUtils.assertSuccess(url, roleUpdateVo,"更新角色成功");

        // 验证用户基本信息
        RoleEntity roleEntityAfter = roleService.getById(roleUpdateVo.getId());
        List<RoleMenuRelEntity> roleMenuRelEntitiesAfter = roleMenuRelService.list(new QueryWrapper<RoleMenuRelEntity>().eq("role_id", roleUpdateVo.getId()));

        // id
        Assertions.assertNotNull(roleEntityAfter.getId());
        // name
        Assertions.assertEquals(roleUpdateVo.getName(), roleEntityAfter.getName());
        // isDel
        Assertions.assertEquals(0, roleEntityAfter.getIsDel());
        // createTime
        Assertions.assertEquals(roleEntityBefore.getCreateTime(), roleEntityAfter.getCreateTime());
        // updateTime
        Assertions.assertNotEquals(roleEntityBefore.getUpdateTime(), roleEntityAfter.getUpdateTime());



        // 验证用户角色
        for (RoleMenuRelEntity roleMenuRelEntity : roleMenuRelEntitiesAfter) {
            // id
            Assertions.assertNotNull(roleMenuRelEntity.getRoleId());
            // roleId
            Assertions.assertEquals(roleUpdateVo.getId(), roleMenuRelEntity.getRoleId());
            // isDel
            Assertions.assertEquals(0, roleMenuRelEntity.getIsDel());
            // createTime
            Assertions.assertNotNull(roleMenuRelEntity.getCreateTime());
            // updateTime
            Assertions.assertNotNull(roleMenuRelEntity.getUpdateTime());

        }
        // 验证menuId
        Assertions.assertEquals(
            roleUpdateVo.getMenuIdList().stream().sorted().map(String::valueOf).collect(Collectors.joining()),
            roleMenuRelEntitiesAfter.stream().map(RoleMenuRelEntity::getMenuId).sorted().map(String::valueOf).collect(Collectors.joining())
        );
    }

    @DisplayName("角色名重复")
    @Test
    void nameRepeat() {
        RoleEntity r1 = this.roleService.getById(this.roleIds.get(0));
        RoleEntity r2 = this.roleService.getById(this.roleIds.get(1));
        r1.setName(r2.getName());

        RoleUpdateVo roleUpdateVo = new RoleUpdateVo();
        BeanUtils.copyProperties(r1, roleUpdateVo);

        testUtils.simplePerformAssert(url, roleUpdateVo, 500, "角色名重复");
    }

    @DisplayName("更新的角色不存在")
    @Test
    void notExists() {
        testUtils.simplePerformAssert(url, getNewRoleUpdateVo(), 500, "角色不存在");
    }

    @DisplayName("角色名非法，值为null/空字符串/包含多个空白字符的空字符串")
    @Test
    void nameIsBlank() {
        List<RoleUpdateVo> roleUpdateVos = new ArrayList<>();
        String[] names = Arrays.array(null, "", "  ");
        for (int i = 0; i < names.length; i++) {
            Long id = roleIds.get(i);
            String name = names[i];
            RoleUpdateVo roleUpdateVo = new RoleUpdateVo();
            roleUpdateVo.setId(id);
            roleUpdateVo.setName(name);
            roleUpdateVos.add(roleUpdateVo);
        }
        testUtils.assertFailBatch(url, roleUpdateVos, "请输入角色名");
    }

    @DisplayName("id为null")
    @Test
    void idIsNull() {
        RoleUpdateVo roleUpdateVo = getNewRoleUpdateVo();
        roleUpdateVo.setId(null);
        testUtils.simplePerformAssert(url, roleUpdateVo, 500, "请输入角色id");
    }
}
