package com.xbxie.mall.product.controller;

import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.product.service.SpuService;
import com.xbxie.mall.product.vo.SpuDetailVo;
import com.xbxie.mall.product.vo.SpuPageReqVo;
import com.xbxie.mall.product.vo.SpuPageResVo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@RestController
@RequestMapping("/spu")
public class SpuController {
    @Resource
    private SpuService spuService;

    @PostMapping("/pageList")
    public R<PageData<SpuPageResVo>> pageList(@RequestBody SpuPageReqVo spuPageVo) {
        return spuService.pageList(spuPageVo);
    }

    @PostMapping("/{id}")
    public R<SpuDetailVo> getSpu(@PathVariable("id") Long id) {
        return spuService.getSpu(id);
    }
}
