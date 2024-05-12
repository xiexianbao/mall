package com.xbxie.mall.admin.controller.menu;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.MenuEntity;
import com.xbxie.mall.admin.service.MenuService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 测试删除菜单接口
 * created by xbxie on 2024/4/28
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试删除菜单接口")
public class MenuDelTest {

    private static final String url = "/menu/del";

    @Resource
    private TestUtils testUtils;

    @Resource
    private MenuService menuService;

    private List<Long> menuIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        Random random = new Random();
        // 新增三条测试数据
        for (int i = 0; i < 3; i++) {
            MenuEntity menuEntity = new MenuEntity();
            menuEntity.setId(null);
            menuEntity.setPid(null);
            menuEntity.setName(random.nextLong() + "");
            menuEntity.setPath("/" + i);
            menuEntity.setIsDel(null);
            menuEntity.setCreateTime(null);
            menuEntity.setUpdateTime(null);
            
            menuService.save(menuEntity);
            menuIds.add(menuEntity.getId());
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_menu", menuIds));
    }

    @DisplayName("删除菜单")
    @Test
    void del() {
        Long menuId = this.menuIds.stream().findAny().get();

        R<Void> resData = testUtils.getResData(url + "/" + menuId, null);

        // 进行返回结果
        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("删除菜单成功", resData.getMessage());


        // 验证数据库中的数据
        // 验证菜单表
        QueryWrapper<MenuEntity> wrapper = new QueryWrapper<MenuEntity>().eq("id", menuId);
        Assertions.assertEquals(0, menuService.count(wrapper));

    }

    @DisplayName("删除的菜单不存在")
    @Test
    void notExists() {
        long id = new Random().nextLong();
        testUtils.simplePerformAssert(url + "/" + id, null, 500, "菜单不存在");
    }
}
