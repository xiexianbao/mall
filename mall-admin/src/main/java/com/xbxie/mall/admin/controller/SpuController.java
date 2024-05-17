package com.xbxie.mall.admin.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import com.xbxie.mall.admin.service.SpuService;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@RestController
@RequestMapping("/admin/spu")
public class SpuController {
    @Resource
    private SpuService spuService;
}
