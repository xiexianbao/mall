package com.xbxie.mall.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.xbxie.mall.admin.vo.HomePageAddVo;
import com.xbxie.mall.admin.vo.HomePageUpdateVo;
import com.xbxie.mall.admin.vo.HomePageVo;
import com.xbxie.mall.common.entity.CommonHomePageEntity;
import com.xbxie.mall.common.service.CommonHomePageService;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xbxie.mall.admin.service.HomePageService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * created by xbxie on 2024-05-20 15:33:52
 */
@Service("homePageService")
public class HomePageServiceImpl  implements HomePageService {
    @Resource
    private CommonHomePageService commonHomePageService;
    
    @Override
    public R<Void> add(HomePageAddVo homePageAddVo) {
        CommonHomePageEntity homePageEntity = new CommonHomePageEntity();
        homePageEntity.setDetail(JSON.toJSONString(homePageAddVo));
        if (!commonHomePageService.save(homePageEntity)) {
            return R.fail("添加首页失败");
        }

        return R.success("添加首页成功");
    }

    @Override
    public R<Void> updateHomePage(HomePageUpdateVo homePageUpdateVo) {
        CommonHomePageEntity homePageEntity = commonHomePageService.getById(homePageUpdateVo.getId());
        if (homePageEntity == null) {
            return R.fail("id非法");
        }

        HomePageAddVo homePageAddVo = new HomePageAddVo();
        BeanUtils.copyProperties(homePageUpdateVo, homePageAddVo);
        homePageEntity.setDetail(JSON.toJSONString(homePageAddVo));

        if (!commonHomePageService.updateById(homePageEntity)) {
            return R.fail("更新首页失败");
        }

        return R.success("更新首页成功");
    }

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