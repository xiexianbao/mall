package com.xbxie.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.xbxie.mall.common.entity.CommonHomePageEntity;
import com.xbxie.mall.common.service.CommonHomePageService;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.product.vo.HomePageVo;
import org.springframework.stereotype.Service;
import com.xbxie.mall.product.service.HomePageService;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.util.List;

/**
 * created by xbxie on 2024-05-20 23:07:48
 */
@Service("homePageService")
public class HomePageServiceImpl  implements HomePageService {
    @Resource
    private CommonHomePageService commonHomePageService;
    
    @Override
    public R<HomePageVo> getHomePage() {
        List<CommonHomePageEntity> list = commonHomePageService.list();
        if(CollectionUtils.isEmpty(list)) {
            return R.success(new HomePageVo());
        }

        CommonHomePageEntity homePageEntity = list.get(0);
        HomePageVo homePageVo = JSON.parseObject(homePageEntity.getDetail(), HomePageVo.class);
        homePageVo.setId(homePageEntity.getId());

        return R.success(homePageVo);
    }
}