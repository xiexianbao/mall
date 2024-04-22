package com.xbxie.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xbxie.mall.admin.entity.UserEntity;
import com.xbxie.mall.admin.service.UserService;
import com.xbxie.mall.admin.utils.TestUtils;
import com.xbxie.mall.admin.vo.UserAddVo;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * created by xbxie on 2024/4/21
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Resource
    private TestUtils testUtils;

    @Resource
    private UserService userService;

    @DisplayName("添加用户")
    @Nested
    class AddUserTest{
        private final String url = "/user/add";

        @DisplayName("用户名为null/空字符串/包含多个空白字符的空字符串")
        @Test
        void nameIsBlank() {
            List<UserAddVo> collect = Stream.of(null, "", "  ").map(name -> {
                String account =  String.valueOf(System.currentTimeMillis());
                String password = String.valueOf(System.currentTimeMillis());
                return new UserAddVo(name, account, password, null);
            }).collect(Collectors.toList());
            testUtils.assertFailBatch(url, collect, "请输入用户名");
        }

        @DisplayName("用户账号为null/空字符串/包含多个空白字符的空字符串")
        @Test
        void accountIsBlank() {
            List<UserAddVo> collect = Stream.of(null, "", " ").map(account -> {
                String name =  String.valueOf(System.currentTimeMillis());
                String password = String.valueOf(System.currentTimeMillis());
                return new UserAddVo(name, account, password, null);
            }).collect(Collectors.toList());
            testUtils.assertFailBatch(url, collect, "请输入用户账号");
        }

        @DisplayName("账号密码为null/空字符串/包含多个空白字符的空字符串")
        @Test
        void passwordIsBlank() {
            List<UserAddVo> collect = Stream.of(null, "", "  ").map(password -> {
                String name =  String.valueOf(System.currentTimeMillis());
                String account = String.valueOf(System.currentTimeMillis());
                return new UserAddVo(name, account, password, null);
            }).collect(Collectors.toList());
            testUtils.assertFailBatch(url, collect, "请输入账号密码");
        }

        @DisplayName("用户名已存在")
        @Test
        void nameRepeat() {
            String name1 =  String.valueOf(System.currentTimeMillis());
            String account1 =  String.valueOf(System.currentTimeMillis());
            String password1 = String.valueOf(System.currentTimeMillis());

            String name2 =  name1;
            String account2 =  String.valueOf(System.currentTimeMillis());
            String password2 = String.valueOf(System.currentTimeMillis());

            UserAddVo u1 = new UserAddVo(name1, account1, password1, null);
            UserAddVo u2 = new UserAddVo(name2, account2, password2, null);
            testUtils.simplePerform(url, u1, 0, "添加用户成功");
            testUtils.simplePerform(url, u2, 500, "用户名或账号已存在");
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name1).eq("account", account1)));
        }

        @DisplayName("用户账号已存在")
        @Test
        void accountRepeat() {
            String name1 =  String.valueOf(System.currentTimeMillis());
            String account1 =  String.valueOf(System.currentTimeMillis());
            String password1 = String.valueOf(System.currentTimeMillis());
            
            String name2 =  String.valueOf(System.currentTimeMillis());
            String account2 =  account1;
            String password2 = String.valueOf(System.currentTimeMillis());
            
            UserAddVo u1 = new UserAddVo(name1, account1, password1, null);
            UserAddVo u2 = new UserAddVo(name2, account2, password2, null);
            testUtils.simplePerform(url, u1, 0, "添加用户成功");
            testUtils.simplePerform(url, u2, 500, "用户名或账号已存在");
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name1).eq("account", account1)));
        }

        @DisplayName("用户名和用户账号都已存在")
        @Test
        void nameAndAccountAllRepeat() {
            String name1 =  String.valueOf(System.currentTimeMillis());
            String account1 =  String.valueOf(System.currentTimeMillis());
            String password1 = String.valueOf(System.currentTimeMillis());

            String name2 =  name1;
            String account2 =  account1;
            String password2 = String.valueOf(System.currentTimeMillis());

            UserAddVo u1 = new UserAddVo(name1, account1, password1, null);
            UserAddVo u2 = new UserAddVo(name2, account2, password2, null);
            testUtils.simplePerform(url, u1, 0, "添加用户成功");
            testUtils.simplePerform(url, u2, 500, "用户名或账号已存在");
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name1).eq("account", account1)));
        }

        @DisplayName("status为null")
        @Test
        void statusIsNull() {
            String name =  String.valueOf(System.currentTimeMillis());
            String account =  String.valueOf(System.currentTimeMillis());
            String password = String.valueOf(System.currentTimeMillis());
            
            UserAddVo u = new UserAddVo(name, account, password, null);
            testUtils.simplePerform(url, u, 0, "添加用户成功");
            
            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account));
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account)));
            Assertions.assertEquals(userEntity.getStatus(), 1);

        }

        @DisplayName("status为-1")
        @Test
        void statusIsOutOfRange() {
            String name =  String.valueOf(System.currentTimeMillis());
            String account =  String.valueOf(System.currentTimeMillis());
            String password = String.valueOf(System.currentTimeMillis());

            UserAddVo u = new UserAddVo(name, account, password, -1);
            testUtils.simplePerform(url, u, 0, "添加用户成功");

            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account));
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account)));
            Assertions.assertEquals(userEntity.getStatus(), 1);

        }

        @DisplayName("status为0")
        @Test
        void statusEqual0() {
            String name =  String.valueOf(System.currentTimeMillis());
            String account =  String.valueOf(System.currentTimeMillis());
            String password = String.valueOf(System.currentTimeMillis());

            UserAddVo u = new UserAddVo(name, account, password, 0);
            testUtils.simplePerform(url, u, 0, "添加用户成功");

            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account));
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account)));
            Assertions.assertEquals(userEntity.getStatus(), 0);
        }

        @DisplayName("status为1")
        @Test
        void statusEqual1() {
            String name =  String.valueOf(System.currentTimeMillis());
            String account =  String.valueOf(System.currentTimeMillis());
            String password = String.valueOf(System.currentTimeMillis());

            UserAddVo u = new UserAddVo(name, account, password, 1);
            testUtils.simplePerform(url, u, 0, "添加用户成功");

            UserEntity userEntity = userService.getOne(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account));
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account)));
            Assertions.assertEquals(userEntity.getStatus(), 1);
        }

        @DisplayName("添加一个正常用户")
        @Test
        void addNormal() {
            String name =  String.valueOf(System.currentTimeMillis());
            String account =  String.valueOf(System.currentTimeMillis());
            String password = String.valueOf(System.currentTimeMillis());
            UserAddVo u = new UserAddVo(name, account, password, null);
            testUtils.simplePerform(url, u, 0, "添加用户成功");
            Assertions.assertTrue(userService.remove(new QueryWrapper<UserEntity>().eq("name", name).eq("account", account)));
        }
    }

    @Disabled
    @DisplayName("删除用户")
    @Nested
    class DelUserTest{
        private final String url = "/del/add";

        @DisplayName("删除用户")
        @Test
        void delUser() {}
    }
}
