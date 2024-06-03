package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import com.xbxie.mall.admin.service.HomePageService;

/**
 * created by xbxie on 2024-05-20 15:33:52
 */
@RestController
@RequestMapping("/homepage")
public class HomePageController {
    @Resource
    private HomePageService homePageService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody HomePageAddVo homePageAddVo) {
        return homePageService.add(homePageAddVo);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody HomePageUpdateVo homePageUpdateVo) {
        return homePageService.updateHomePage(homePageUpdateVo);
    }

    @PostMapping("/detail")
    public R<HomePageVo> getHomePage() {
        return homePageService.getHomePage();
    }
}
