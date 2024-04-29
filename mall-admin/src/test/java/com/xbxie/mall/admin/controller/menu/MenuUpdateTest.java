package com.xbxie.mall.admin.controller.menu;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.xbxie.mall.admin.entity.MenuEntity;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.MenuService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.service.impl.RoleMenuRelServiceImpl;
import com.xbxie.mall.admin.service.impl.RoleServiceImpl;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.*;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试更新菜单接口
 * created by xbxie on 2024/4/25
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试更新菜单接口")
public class MenuUpdateTest {
    private static final String url = "/menu/update";

    @Resource
    private TestUtils testUtils;

    @Resource
    private MenuService menuService;

    private List<Long> menuIds = new ArrayList<>();

    private List<MenuEntity> menuEntities = new ArrayList<>();

    public MenuUpdateVo getNewMenuUpdateVo() {
        Random random = new Random();

        MenuUpdateVo menuUpdateVo = new MenuUpdateVo();
        menuUpdateVo.setId(random.nextLong());
        menuUpdateVo.setPid(null);
        menuUpdateVo.setName(random.nextLong() + "");
        menuUpdateVo.setUrl("/" + random.nextLong());

        return menuUpdateVo;
    }

    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        // 添加用户
        for (int i = 1; i <= 30; i++) {
            MenuEntity menuEntity = new MenuEntity();

            menuEntity.setId(null);
            menuEntity.setPid(null);
            menuEntity.setName(i + "");
            menuEntity.setUrl("/" + i);
            menuEntity.setIsDel(0);
            menuEntity.setCreateTime(null);
            menuEntity.setUpdateTime(null);

            menuService.save(menuEntity);
            MenuEntity menuEntity1 = menuService.getById(menuEntity.getId());
            menuEntities.add(menuEntity1);
            menuIds.add(menuEntity1.getId());
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_menu", menuIds));
    }

    @DisplayName("更新菜单")
    @Test
    void update() throws Exception {
        MenuEntity menuEntity1Before = this.menuEntities.get(0);
        MenuEntity menuEntity2Before = this.menuEntities.get(1);

        MenuUpdateVo menuUpdateVo = new MenuUpdateVo();
        MenuUpdateVo newMenuUpdateVo = getNewMenuUpdateVo();
        menuUpdateVo.setId(menuEntity1Before.getId());
        menuUpdateVo.setPid(menuEntity2Before.getId());
        menuUpdateVo.setName(newMenuUpdateVo.getName());
        menuUpdateVo.setUrl(newMenuUpdateVo.getUrl());

        Thread.sleep(1000);
        testUtils.assertSuccess(url, menuUpdateVo,"更新菜单成功");

        MenuEntity menuEntity1After = menuService.getById(menuUpdateVo.getId());
        // id
        Assertions.assertEquals(menuUpdateVo.getId(), menuEntity1After.getId());
        // pid
        Assertions.assertEquals(menuUpdateVo.getPid(), menuEntity1After.getPid());
        // name
        Assertions.assertEquals(menuUpdateVo.getName(), menuEntity1After.getName());
        // url
        Assertions.assertEquals(menuUpdateVo.getUrl(), menuEntity1After.getUrl());
        // idDel
        Assertions.assertEquals(menuEntity1Before.getIsDel(), menuEntity1After.getIsDel());
        // createTime
        Assertions.assertEquals(menuEntity1Before.getCreateTime(), menuEntity1After.getCreateTime());
        // updateTime
        Assertions.assertNotEquals(menuEntity1Before.getUpdateTime(), menuEntity1After.getUpdateTime());
    }

    @DisplayName("菜单路径重复")
    @Test
    void pathRepeat() {
        MenuEntity menuEntity1 = this.menuEntities.get(0);
        MenuEntity menuEntity2 = this.menuEntities.get(1);
        MenuUpdateVo menuUpdateVo = new MenuUpdateVo();
        BeanUtils.copyProperties(menuEntity1, menuUpdateVo);
        menuUpdateVo.setUrl(menuEntity2.getUrl());

        testUtils.simplePerformAssert(url, menuUpdateVo, 500, "菜单路径重复");
    }

    @DisplayName("菜单名重复")
    @Test
    void nameRepeat() {
        MenuEntity menuEntity1 = this.menuEntities.get(0);
        MenuEntity menuEntity2 = this.menuEntities.get(1);
        MenuUpdateVo menuUpdateVo = new MenuUpdateVo();
        BeanUtils.copyProperties(menuEntity1, menuUpdateVo);
        menuUpdateVo.setName(menuEntity2.getName());

        testUtils.simplePerformAssert(url, menuUpdateVo, 500, "菜单名重复");
    }

    @DisplayName("更新的菜单不存在")
    @Test
    void notExists() {
        testUtils.simplePerformAssert(url, getNewMenuUpdateVo(), 500, "菜单不存在");
    }

    @DisplayName("菜单名/菜单路径非法，值为null/空字符串/包含多个空白字符的空字符串")
    @Test
    void nameUrlIsBlank() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "请输入菜单名称");
        map.put("url", "请输入菜单路径");

        Random random = new Random();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String message = entry.getValue();
            {
                List<MenuUpdateVo> menuUpdateVos = new ArrayList<>();
                String[] arr = Arrays.array(null, "", "  ");

                for (String s : arr) {
                    String name = "";
                    String url = "";
                    {
                        switch (key) {
                            case "name":
                                name = s;
                                url = "/" + random.nextLong();
                                break;
                            case "url":
                                name = random.nextLong() + "";
                                url = s;
                                break;
                        }
                    }
                    MenuUpdateVo menuUpdateVo = new MenuUpdateVo();
                    menuUpdateVo.setId(this.menuIds.get(0));
                    menuUpdateVo.setPid(null);
                    menuUpdateVo.setName(name);
                    menuUpdateVo.setUrl(url);
                    menuUpdateVos.add(menuUpdateVo);
                }

                testUtils.assertFailBatch(url, menuUpdateVos, message);
            }
        }
    }

    @DisplayName("id为null")
    @Test
    void idIsNull() {
        MenuUpdateVo menuUpdateVo = getNewMenuUpdateVo();
        menuUpdateVo.setId(null);
        testUtils.simplePerformAssert(url, menuUpdateVo, 500, "请输入菜单id");
    }
}