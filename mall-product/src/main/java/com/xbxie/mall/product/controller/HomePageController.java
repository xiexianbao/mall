package com.xbxie.mall.product.controller;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.product.vo.HomePageVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import com.xbxie.mall.product.service.HomePageService;

/**
 * created by xbxie on 2024-05-20 23:07:48
 */
@RestController
@RequestMapping("/homepage")
public class HomePageController {
    @Resource
    private HomePageService homePageService;

    @PostMapping("/detail")
    public R<HomePageVo> getHomePage() {
        return homePageService.getHomePage();
    }
}
