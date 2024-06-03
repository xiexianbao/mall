package com.xbxie.mall.admin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entityback.UserEntity;
import com.xbxie.mall.admin.entityback.UserRoleRelEntity;
import com.xbxie.mall.admin.service.UserRoleRelService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.UserUpdateVo;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试更新用户接口
 * created by xbxie on 2024/4/23
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试更新用户接口")
public class UserUpdateTest {

    private static final String url = "/user/update";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private UserRoleRelService userRoleRelService;

    private List<Long> userIds = new ArrayList<>();

    private List<Long> userRoleRelIds = new ArrayList<>();

    private List<UserEntity> userEntities = new ArrayList<>();

    private List<UserRoleRelEntity> userRoleRelEntities = new ArrayList<>();

    private Map<UserEntity, List<UserRoleRelEntity>> userRoleMap = new HashMap<>();

    public UserUpdateVo getNewUserUpdateVo() {
        Random random = new Random();
        Long id = System.currentTimeMillis();
        String name =  random.nextLong() + "";
        String account =  random.nextLong() + "";
        String password = random.nextLong() + "";

        UserUpdateVo userUpdateVo = new UserUpdateVo();
        userUpdateVo.setId(id);
        userUpdateVo.setName(name);
        userUpdateVo.setAccount(account);
        userUpdateVo.setPassword(password);
        userUpdateVo.setStatus(1);
        return userUpdateVo;
    }



    @BeforeEach
    public void beforeEach() {
        // 初始化测试数据
        Random random = new Random();

        // 添加用户
        for (int i = 1; i <= 30; i++) {
            UserEntity userEntity = new UserEntity();

            userEntity.setId(null);
            userEntity.setName(i + "");
            userEntity.setAccount(i + "");
            userEntity.setPassword(random.nextLong() + "");
            userEntity.setStatus(1);
            userEntity.setIsDel(0);
            userEntity.setCreateTime(null);
            userEntity.setUpdateTime(null);

            userService.save(userEntity);
            UserEntity userEntity1 = userService.getById(userEntity.getId());
            userEntities.add(userEntity1);
            userIds.add(userEntity1.getId());
            userRoleMap.put(userEntity1, new ArrayList<>());
            // 添加用户角色
            for (int j = 1; j <= 3; j++) {
                UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
                userRoleRelEntity.setUserId(userEntity1.getId());
                userRoleRelEntity.setRoleId((long) i);

                userRoleRelService.save(userRoleRelEntity);
                UserRoleRelEntity userRoleRelEntity1 = userRoleRelService.getById(userRoleRelEntity.getId());
                userRoleMap.get(userEntity1).add(userRoleRelEntity1);
                userRoleRelEntities.add(userRoleRelEntity1);
                userRoleRelIds.add(userRoleRelEntity1.getId());
            }
        }
    }

    @AfterEach
    public void afterEach() {
        // 删除测试数据，通过原生sql绕过逻辑删除
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user", userIds));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user_role_rel", userRoleRelIds));
    }

    @DisplayName("更新用户")
    @Test
    void update() throws Exception {
        Random random = new Random();
        // 从库里面取数据，制作请求参数
        UserEntity userEntityBefore = this.userEntities.stream().findAny().get();
        List<UserRoleRelEntity> userRoleRelEntitiesBefore = userRoleMap.get(userEntityBefore);

        UserUpdateVo userUpdateVo = new UserUpdateVo();
        UserUpdateVo newUserUpdateVo = getNewUserUpdateVo();
        userUpdateVo.setId(userEntityBefore.getId());
        userUpdateVo.setName(newUserUpdateVo.getName());
        userUpdateVo.setAccount(newUserUpdateVo.getAccount());
        userUpdateVo.setPassword(newUserUpdateVo.getPassword());
        userUpdateVo.setStatus(newUserUpdateVo.getStatus());
        userUpdateVo.setRoleIdList(java.util.Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong()));

        Thread.sleep(1000);
        testUtils.assertSuccess(url, userUpdateVo,"更新用户成功");

        // 验证用户基本信息
        UserEntity userEntityAfter = userService.getById(userUpdateVo.getId());
        List<UserRoleRelEntity> userRoleRelEntitiesAfter = userRoleRelService.list(new QueryWrapper<UserRoleRelEntity>().eq("user_id", userUpdateVo.getId()));

        // id
        Assertions.assertNotNull(userEntityAfter.getId());
        // name
        Assertions.assertEquals(userUpdateVo.getName(), userEntityAfter.getName());
        // account
        Assertions.assertEquals(userUpdateVo.getAccount(), userEntityAfter.getAccount());
        // password
        Assertions.assertEquals(userUpdateVo.getPassword(), userEntityAfter.getPassword());
        // status
        Assertions.assertEquals(userUpdateVo.getStatus(), userEntityAfter.getStatus());
        // isDel
        Assertions.assertEquals(0, userEntityAfter.getIsDel());
        // createTime
        Assertions.assertEquals(userEntityBefore.getCreateTime(), userEntityAfter.getCreateTime());
        // updateTime
        Assertions.assertNotEquals(userEntityBefore.getUpdateTime(), userEntityAfter.getUpdateTime());



        // 验证用户角色
        for (UserRoleRelEntity userRoleRelEntity : userRoleRelEntitiesAfter) {
            // id
            Assertions.assertNotNull(userRoleRelEntity.getRoleId());
            // userId
            Assertions.assertEquals(userUpdateVo.getId(), userRoleRelEntity.getUserId());
            // roleId：在下面统一处理，然后再比较
            // isDel
            Assertions.assertEquals(0, userRoleRelEntity.getIsDel());
            // createTime
            Assertions.assertNotNull(userRoleRelEntity.getCreateTime());
            // updateTime
            Assertions.assertNotNull(userRoleRelEntity.getUpdateTime());

        }
        // 验证roleId
        Assertions.assertEquals(
            userUpdateVo.getRoleIdList().stream().sorted().map(String::valueOf).collect(Collectors.joining()),
            userRoleRelEntitiesAfter.stream().map(UserRoleRelEntity::getRoleId).sorted().map(String::valueOf).collect(Collectors.joining())
        );
    }

    @DisplayName("用户名/用户账号重复")
    @Test
    void nameAccountRepeat() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "用户名重复");
        map.put("account", "用户账号重复");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String message = entry.getValue();

            {
                UserEntity u1 = this.userService.getById(this.userIds.get(0));
                UserEntity u2 = this.userService.getById(this.userIds.get(1));

                switch (key) {
                    case "name":
                        u1.setName(u2.getName());
                        break;
                    case "account":
                        u1.setAccount(u2.getAccount());
                        break;
                }

                UserUpdateVo userUpdateVo = new UserUpdateVo();
                BeanUtils.copyProperties(u1, userUpdateVo);

                testUtils.simplePerformAssert(url, userUpdateVo, 500, message);
            }
        }
    }

    @DisplayName("更新的用户不存在")
    @Test
    void notExists() {
        testUtils.simplePerformAssert(url, getNewUserUpdateVo(), 500, "用户不存在");
    }

    @DisplayName("status的值为null/-1")
    @Test
    void status() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(null, 1);
        map.put(-1, 1);

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Integer illegal = entry.getKey();
            Integer legal = entry.getValue();

            UserUpdateVo oldUser = new UserUpdateVo();
            BeanUtils.copyProperties(this.userService.getById(this.userIds.get(0)), oldUser);

            UserUpdateVo newUser = getNewUserUpdateVo();

            oldUser.setName(newUser.getName());
            oldUser.setAccount(newUser.getAccount());
            oldUser.setPassword(newUser.getPassword());
            oldUser.setStatus(illegal);
            testUtils.simplePerformAssert(url, oldUser, 0, "更新用户成功");

            UserEntity userEntity = userService.getById(oldUser.getId());
            Assertions.assertEquals(legal, userEntity.getStatus());
        }
    }

    @DisplayName("用户名/用户账号/账号密码非法，值为null/空字符串/包含多个空白字符的空字符串")
    @Test
    void nameAccountPasswordIsBlank() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "请输入用户名");
        map.put("account", "请输入用户账号");
        map.put("password", "请输入账号密码");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String message = entry.getValue();
            {
                List<UserUpdateVo> userUpdateVos = new ArrayList<>();
                String[] arr = Arrays.array(null, "", "  ");

                for (int i = 0; i < arr.length; i++) {
                    Long id = userIds.get(i);
                    String name = "";
                    String account = "";
                    String password = "";
                    {
                        switch (key) {
                            case "name":
                                name = arr[i];
                                account =  String.valueOf(System.currentTimeMillis());
                                password = String.valueOf(System.currentTimeMillis());
                                break;
                            case "account":
                                name =  String.valueOf(System.currentTimeMillis());
                                account = arr[i];
                                password = String.valueOf(System.currentTimeMillis());
                                break;
                            case "password":
                                name =  String.valueOf(System.currentTimeMillis());
                                account = String.valueOf(System.currentTimeMillis());
                                password = arr[i];
                                break;
                        }
                    }

                    UserUpdateVo userUpdateVo = new UserUpdateVo();
                    userUpdateVo.setId(id);
                    userUpdateVo.setName(name);
                    userUpdateVo.setAccount(account);
                    userUpdateVo.setPassword(password);
                    userUpdateVo.setStatus(null);
                    userUpdateVos.add(userUpdateVo);
                }

                testUtils.assertFailBatch(url, userUpdateVos, message);
            }
        }
    }

    @DisplayName("id为null")
    @Test
    void idIsNull() {
        UserUpdateVo userUpdateVo = getNewUserUpdateVo();
        userUpdateVo.setId(null);
        testUtils.simplePerformAssert(url, userUpdateVo, 500, "请输入用户id");
    }
}
