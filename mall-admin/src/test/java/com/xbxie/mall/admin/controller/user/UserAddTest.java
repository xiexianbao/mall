package com.xbxie.mall.admin.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.entity.UserRoleRelEntity;
import com.xbxie.mall.admin.service.UserRoleRelService;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.UserAddVo;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试新增用户接口
 * created by xbxie on 2024/4/23
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("测试新增用户接口")
public class UserAddTest {
    private final String url = "/user/add";

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @Resource
    private UserRoleRelService userRoleRelService;

    public UserAddVo getNewUserAddVo() {
        Random random = new Random();
        UserAddVo userAddVo = new UserAddVo();
        userAddVo.setName(random.nextLong() + "");
        userAddVo.setAccount(random.nextLong() + "");
        userAddVo.setPassword(random.nextLong() + "");
        userAddVo.setStatus(null);
        userAddVo.setRoleIdList(java.util.Arrays.asList(random.nextLong(), random.nextLong(), random.nextLong()));
        return userAddVo;
    }

    @DisplayName("添加用户")
    @Test
    void add() {
        UserAddVo userAddVo = getNewUserAddVo();
        testUtils.simplePerformAssert(url, userAddVo,0, "添加用户成功");

        UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", userAddVo.getName()).eq("account", userAddVo.getAccount()));
        List<UserRoleRelEntity> userRoleRelEntities = userRoleRelService.list(new QueryWrapper<UserRoleRelEntity>().eq("user_id", userEntity.getId()));

        Assertions.assertTrue(testUtils.deletePhysical("ums_user", userEntity.getId()));
        Assertions.assertTrue(testUtils.deleteBatchPhysical("ums_user_role_rel", userRoleRelEntities.stream().map(UserRoleRelEntity::getId).collect(Collectors.toList())));

        Assertions.assertNotNull(userEntity);
        Assertions.assertNotNull(userEntity.getId());
        Assertions.assertEquals(userAddVo.getName(), userEntity.getName());
        Assertions.assertEquals(userAddVo.getAccount(), userEntity.getAccount());
        Assertions.assertEquals(userAddVo.getPassword(), userEntity.getPassword());
        Assertions.assertEquals(1, userEntity.getStatus());
        Assertions.assertEquals(0, userEntity.getIsDel());
        Assertions.assertNotNull(userEntity.getCreateTime());
        Assertions.assertNotNull(userEntity.getUpdateTime());


        if (!CollectionUtils.isEmpty(userRoleRelEntities)) {
            Assertions.assertEquals(
                userAddVo.getRoleIdList().stream().sorted().map(String::valueOf).collect(Collectors.joining()),
                userRoleRelEntities.stream().map(UserRoleRelEntity::getRoleId).sorted().map(String::valueOf).collect(Collectors.joining())
            );

            for (UserRoleRelEntity userRoleRelEntity : userRoleRelEntities) {
                Assertions.assertNotNull(userRoleRelEntity.getId());
                Assertions.assertEquals(userEntity.getId(), userRoleRelEntity.getUserId());
                Assertions.assertEquals(0, userRoleRelEntity.getIsDel());
                Assertions.assertNotNull(userRoleRelEntity.getCreateTime());
                Assertions.assertNotNull(userRoleRelEntity.getUpdateTime());
            }
        }
    }

    @DisplayName("用户名/用户账号重复")
    @Test
    void nameAccountRepeat() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "用户名重复");
        map.put("account", "用户账号重复");
        Random random = new Random();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String message = entry.getValue();
            {
                String name1 = "";
                String account1 =  "";
                String password1 = String.valueOf(random.nextLong());
                String name2 =  "";
                String account2 =  "";
                String password2 = String.valueOf(random.nextLong());

                switch (key) {
                    case "name":
                        name1 =  String.valueOf(random.nextLong());
                        account1 =  String.valueOf(random.nextLong());
                        name2 =  name1;
                        account2 =  String.valueOf(random.nextLong());
                        break;
                    case "account":
                        name1 =  String.valueOf(random.nextLong());
                        account1 =  String.valueOf(random.nextLong());
                        name2 =  String.valueOf(random.nextLong());
                        account2 =  account1;
                        break;
                }

                UserAddVo u1 = new UserAddVo();
                u1.setName(name1);
                u1.setAccount(account1);
                u1.setPassword(password1);
                u1.setStatus(null);

                UserAddVo u2 = new UserAddVo();
                u2.setName(name2);
                u2.setAccount(account2);
                u2.setPassword(password2);
                u2.setStatus(null);

                testUtils.simplePerformAssert(url, u1, 0, "添加用户成功");
                testUtils.simplePerformAssert(url, u2, 500, message);

                UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", name1).eq("account", account1));
                Assertions.assertNotNull(userEntity);
                Assertions.assertTrue(testUtils.deletePhysical("ums_user", userEntity.getId()));
            }
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
                List<UserAddVo> userAddVos = new ArrayList<>();
                String[] arr = Arrays.array(null, "", "  ");

                for (int i = 0; i < arr.length; i++) {
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


                    UserAddVo userAddVo = new UserAddVo();
                    userAddVo.setName(name);
                    userAddVo.setAccount(account);
                    userAddVo.setPassword(password);
                    userAddVo.setStatus(null);
                    userAddVos.add(userAddVo);
                }

                testUtils.assertFailBatch(url, userAddVos, message);
            }
        }
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

            UserAddVo userAddVo = getNewUserAddVo();
            userAddVo.setStatus(illegal);
            testUtils.simplePerformAssert(url, userAddVo, 0, "添加用户成功");

            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", userAddVo.getName()).eq("account", userAddVo.getAccount()));
            Assertions.assertTrue(testUtils.deletePhysical("ums_user", userEntity.getId()));
            Assertions.assertEquals(legal, userEntity.getStatus());
        }
    }
}
