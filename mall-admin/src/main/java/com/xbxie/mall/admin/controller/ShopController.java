package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.service.ShopService;
import com.xbxie.mall.admin.vo.ShopVo;
import com.xbxie.mall.common.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

/**
 * 权限控制器
 * created by xbxie on 2024/5/13
 */
@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private ShopService shopService;

    @PostMapping("/list")
    public R<List<ShopVo>> list() {
        return shopService.list();
    }
}
