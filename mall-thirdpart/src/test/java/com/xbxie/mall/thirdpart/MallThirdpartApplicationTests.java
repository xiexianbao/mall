package com.xbxie.mall.thirdpart;

import com.xbxie.mall.thirdpart.config.WxConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MallThirdpartApplicationTests {

    @Resource
    private WxConfig config;

    @Test
    void contextLoads() {
        System.out.println("config = " + config);
        System.out.println(1);
    }

}
