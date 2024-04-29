package com.xbxie.mall.admin.controller.user;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.service.UserRoleRelService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.UserPageVo;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * 测试用户分页接口
 * created by xbxie on 2024/4/24
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试用户分页接口")
public class UserPageTest {
    private static final String url = "/user/pageList";

    private static final String namePrefix = new Random().nextLong() + "";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private UserRoleRelService userRoleRelService;

    private List<Long> userIds = new ArrayList<>();

    private List<Long> userRoleRelIds = new ArrayList<>();

    private List<UserEntity> userEntities = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        Random random = new Random();

        // 添加用户
        for (int i = 1; i <= 30; i++) {
            UserEntity userEntity = new UserEntity();

            userEntity.setId(null);
            userEntity.setName(namePrefix + i);
            userEntity.setAccount(i + "");
            userEntity.setPassword(random.nextLong() + "");
            userEntity.setStatus(1);
            userEntity.setIsDel(0);
            userEntity.setCreateTime(null);
            userEntity.setUpdateTime(null);

            userService.save(userEntity);
            userEntities.add(userEntity);
            userIds.add(userEntity.getId());

            // 添加用户角色
            for (int j = 1; j <= 3; j++) {
                UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
                userRoleRelEntity.setUserId(userEntity.getId());
                userRoleRelEntity.setRoleId((long) i);

                userRoleRelService.save(userRoleRelEntity);
                userRoleRelIds.add(userRoleRelEntity.getId());
            }
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user", userIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user_role_rel", userRoleRelIds));
    }

    @DisplayName("分页数据")
    @Test
    void pageData() {
        UserPageVo userPageVo = new UserPageVo();
        userPageVo.setName(namePrefix + "1"); // 12
        userPageVo.setAccount(namePrefix + "2"); // 12
        userPageVo.setPageNum(1L);
        userPageVo.setPageSize(10L);
        R<PageData<UserEntity>> resData = testUtils.getResData(url, userPageVo, new TypeReference<R<PageData<UserEntity>>>() {});

        Assertions.assertEquals(0, resData.getCode());
        Assertions.assertEquals("success", resData.getMessage());
        Assertions.assertEquals(1, resData.getData().getPageNum());
        Assertions.assertEquals(10, resData.getData().getPageSize());
        Assertions.assertEquals(10, resData.getData().getList().size());
        Assertions.assertEquals(userEntities.stream().filter(userEntity -> Pattern.compile(".*" + namePrefix + "1.*").matcher(userEntity.getName()).matches() || Pattern.compile(".*" + namePrefix + "2.*").matcher(userEntity.getAccount()).matches()).count(), resData.getData().getTotal());

        for (UserEntity userEntity : resData.getData().getList()) {
            Assertions.assertTrue(
                Pattern.compile("^.*1.*$").matcher(userEntity.getName()).matches() || Pattern.compile("^.*2.*$").matcher(userEntity.getAccount()).matches()
            );

        }
    }

    @DisplayName("pageNum的值为1000000000，pageSize的值为1000000000")
    @Test
    void pageParamsTooLarge() {
        UserPageVo userPageVo = new UserPageVo();
        userPageVo.setName("1");
        userPageVo.setAccount("1");
        userPageVo.setPageNum(1000000000L);
        userPageVo.setPageSize(1000000000L);

        R<PageData<UserEntity>> pageDataR = testUtils.getResData(url, userPageVo, new TypeReference<R<PageData<UserEntity>>>(){});
        Assertions.assertNotNull(pageDataR.getData().getTotal());
        Assertions.assertEquals(0, pageDataR.getData().getList().size());
    }

    @DisplayName("pageNum的值为-1/0，pageSize的值为-1/0")
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
                String account = String.valueOf(random.nextLong());
                Long pageNum = 1L;
                Long pageSize = 10L;

                if (type.equals("pageNum")) {
                    pageNum = params;
                    pageSize = 10L;
                } else if (type.equals("pageSize")) {
                    pageNum = 1L;
                    pageSize = params;
                }

                UserPageVo userPageVo = new UserPageVo();
                userPageVo.setName(name);
                userPageVo.setAccount(account);
                userPageVo.setPageNum(pageNum);
                userPageVo.setPageSize(pageSize);

                testUtils.simplePerformAssert(url, userPageVo, code, message);
            }
        }
    }
}
