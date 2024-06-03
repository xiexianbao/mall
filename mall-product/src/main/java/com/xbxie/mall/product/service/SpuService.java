package com.xbxie.mall.product.service;

import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.product.vo.SpuDetailVo;
import com.xbxie.mall.product.vo.SpuPageReqVo;
import com.xbxie.mall.product.vo.SpuPageResVo;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
public interface SpuService {
    R<PageData<SpuPageResVo>> pageList(SpuPageReqVo spuPageVo);

    R<SpuDetailVo> getSpu(Long id);
}

