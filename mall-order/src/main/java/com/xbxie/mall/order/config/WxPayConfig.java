package com.xbxie.mall.order.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.xbxie.mall.order.component.WxPayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * created by xbxie on 2024/5/31
 */
@Configuration
public class WxPayConfig {
    @Resource
    private WxPayProperties wxPayProperties;

    @Bean
    public NativePayService nativePayService() throws FileNotFoundException {
        URL resource = this.getClass().getResource("/");
        Config config = new RSAAutoCertificateConfig.Builder()
            .merchantId(wxPayProperties.getMchId())
            .privateKeyFromPath(resource.getPath() + wxPayProperties.getPrivateKeyPath())
            .merchantSerialNumber(wxPayProperties.getMchSerialNo())
            .apiV3Key(wxPayProperties.getApiV3Key())
            .build();
        return new NativePayService.Builder().config(config).build();
    }
}
