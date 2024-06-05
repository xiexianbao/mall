package com.xbxie.mall.admin;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@AutoConfigureMockMvc
class MallAdminApplicationTests {
    @Test
    void foo() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "admin");
        map.put("password", "123456");
        String string = JSON.toJSONString(map);
        System.out.println("string = " + string);
    }
}
