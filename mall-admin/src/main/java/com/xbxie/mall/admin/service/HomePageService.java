package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.HomePageAddVo;
import com.xbxie.mall.admin.vo.HomePageUpdateVo;
import com.xbxie.mall.admin.vo.HomePageVo;
import com.xbxie.mall.common.utils.R;

/**
 * created by xbxie on 2024-05-20 15:33:52
 */
public interface HomePageService {
    R<Void> add(HomePageAddVo homePageAddVo);

    R<Void> updateHomePage(HomePageUpdateVo homePageUpdateVo);

    R<HomePageVo> getHomePage();
}

