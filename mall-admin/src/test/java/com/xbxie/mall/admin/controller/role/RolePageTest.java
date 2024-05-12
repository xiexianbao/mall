package com.xbxie.mall.admin.controller.role;

import com.alibaba.fastjson.TypeReference;
import com.xbxie.mall.admin.entity.RoleEntity;
import com.xbxie.mall.admin.entity.RoleMenuRelEntity;
import com.xbxie.mall.admin.service.RoleMenuRelService;
import com.xbxie.mall.admin.service.RoleService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.RolePageVo;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 测试角色分页接口
 * created by xbxie on 2024/4/25
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试角色分页接口")
public class RolePageTest {
    private static final String url = "/role/pageList";

    private static final String namePrefix = new Random().nextLong() + "";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMenuRelService roleMenuRelService;

    private List<Long> roleIds = new ArrayList<>();

    private List<Long> roleMenuRelIds = new ArrayList<>();

    private List<RoleEntity> roleEntities = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        Random random = new Random();

        // 添加用户
        for (int i = 1; i <= 30; i++) {
            RoleEntity roleEntity = new RoleEntity();

            roleEntity.setId(null);
            roleEntity.setName(namePrefix + i);
            roleEntity.setIsDel(0);
            roleEntity.setCreateTime(null);
            roleEntity.setUpdateTime(null);

            roleService.save(roleEntity);
            roleEntities.add(roleEntity);
            roleIds.add(roleEntity.getId());

            // 添加用户角色
            for (int j = 1; j <= 3; j++) {
                RoleMenuRelEntity roleMenuRelEntity = new RoleMenuRelEntity();
                roleMenuRelEntity.setRoleId(roleEntity.getId());
                roleMenuRelEntity.setMenuId((long) i);

                roleMenuRelService.save(roleMenuRelEntity);
                roleMenuRelIds.add(roleMenuRelEntity.getId());
            }
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role", roleIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_role_menu_rel", roleMenuRelIds));
    }

    @DisplayName("分页数据")
    @Test
    void pageData() {
        RolePageVo rolePageVo = new RolePageVo();
        rolePageVo.setName(namePrefix + "1"); // 12
        rolePageVo.setPageNum(1L);
        rolePageVo.setPageSize(10L);
        R<PageData<RoleEntity>> resData = testUtils.getResData(url, rolePageVo, new TypeReference<R<PageData<RoleEntity>>>() {});

        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("success", resData.getMessage());
        Assertions.assertEquals(1, resData.getData().getPageNum());
        Assertions.assertEquals(10, resData.getData().getPageSize());
        Assertions.assertEquals(10, resData.getData().getList().size());
        Assertions.assertEquals(roleEntities.stream().filter(roleEntity -> Pattern.compile(".*" + namePrefix + "1.*").matcher(roleEntity.getName()).matches()).count(), resData.getData().getTotal());

        for (RoleEntity roleEntity : resData.getData().getList()) {
            Assertions.assertTrue(
                Pattern.compile(".*" + namePrefix + "1.*").matcher(roleEntity.getName()).matches()
            );

        }
    }

    @DisplayName("pageNum的值为1000000000，pageSize的值为1000000000")
    @Test
    void pageParamsTooLarge() {
        RolePageVo rolePageVo = new RolePageVo();
        rolePageVo.setName(namePrefix + "30");
        rolePageVo.setPageNum(1000000000L);
        rolePageVo.setPageSize(1000000000L);
        R<PageData<RoleEntity>> pageDataR = testUtils.getResData(url, rolePageVo, new TypeReference<R<PageData<RoleEntity>>>(){});
        Assertions.assertEquals(1, pageDataR.getData().getTotal());
        Assertions.assertEquals(0, pageDataR.getData().getList().size());
    }

    @DisplayName("pageNum/pageSize的值为-1/0")
    @Test
    void pageParamsIllegal() {
        Map<Long, String> pageNumMap = new HashMap<>();
        pageNumMap.put(-1L, "pageNum需要大于等于1");
        pageNumMap.put(0L, "pageNum需要大于等于1");

        Map<Long, String> pageSizeMap = new HashMap<>();
        pageSizeMap.put(-1L, "pageSize需要大于等于1");
        pageSizeMap.put(0L, "pageSize需要大于等于1");

        Map<String, Map<Long, String>> map = new HashMap<>();
        map.put("pageNum", pageNumMap);
        map.put("pageSize", pageSizeMap);

        Random random = new Random();

        for (Map.Entry<String, Map<Long, String>> mapEntry : map.entrySet()) {
            String type = mapEntry.getKey();
            for (Map.Entry<Long, String> entry : mapEntry.getValue().entrySet()) {
                Long params = entry.getKey();
                String message = entry.getValue();
                int code = message.equals("success") ? 0 : 500;

                String name = String.valueOf(random.nextLong());
                Long pageNum = 1L;
                Long pageSize = 10L;

                if (type.equals("pageNum")) {
                    pageNum = params;
                    pageSize = 10L;
                } else if (type.equals("pageSize")) {
                    pageNum = 1L;
                    pageSize = params;
                }

                RolePageVo rolePageVo = new RolePageVo();
                rolePageVo.setName(name);
                rolePageVo.setPageNum(pageNum);
                rolePageVo.setPageSize(pageSize);
                testUtils.simplePerformAssert(url, rolePageVo, code, message);
            }
        }
    }
}
