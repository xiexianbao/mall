package com.xbxie.mall.product;

import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.service.CommonShopService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class MallProductApplicationTests {
    @Resource
    private CommonShopService commonShopService;

    @Test
    void contextLoads() {
        CommonShopEntity commonShopEntity = commonShopService.getById(1111);
        System.out.println(commonShopEntity);
    }
}
