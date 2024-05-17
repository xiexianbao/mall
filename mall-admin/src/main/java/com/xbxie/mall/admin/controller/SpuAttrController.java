package com.xbxie.mall.admin.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import com.xbxie.mall.admin.service.SpuAttrService;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@RestController
@RequestMapping("/admin/spuattr")
public class SpuAttrController {
    @Resource
    private SpuAttrService spuAttrService;
}
