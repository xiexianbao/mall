package com.xbxie.mall.order;

import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.service.CommonShopService;
import com.xbxie.mall.common.utils.SnowflakeDistributeId;
import com.xbxie.mall.order.utils.WxPayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
class MallOrderApplicationTests {

    @Resource
    private WxPayUtils wxPayUtils;

    @Resource
    private CommonShopService commonShopService;

    @Test
    void listByIds() {
        List<CommonShopEntity> commonShopEntities = commonShopService.listByIds(Arrays.asList(1, 1));
        System.out.println("commonShopEntities = " + commonShopEntities);
    }

    @Test
    void idWorker() {
        SnowflakeDistributeId idWorker = new SnowflakeDistributeId(0, 0);
        long l = idWorker.nextId();
        System.out.println("l = " + l);
    }

    @Test
    public void scale() {
        BigDecimal bigDecimal = BigDecimal.valueOf(100.19).setScale(0, BigDecimal.ROUND_DOWN);
        System.out.println("bigDecimal = " + bigDecimal);
    }

    @Test
    public void createOrder() {
        String orderNo = System.currentTimeMillis() + "";
        String codeUrl = wxPayUtils.createNativeOrder(1, orderNo, "测试商品名称");
        System.out.println("codeUrl = " + codeUrl);
        System.out.println("orderNo = " + orderNo);
    }

    @Test
    public void queryOrder() {
        wxPayUtils.queryNativeOrder("1717239722697");
    }
}
