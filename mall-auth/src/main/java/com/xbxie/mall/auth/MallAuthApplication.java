package com.xbxie.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xbxie.mall")
public class MallAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthApplication.class, args);
    }

}
