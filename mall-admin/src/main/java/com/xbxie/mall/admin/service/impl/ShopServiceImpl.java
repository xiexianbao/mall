package com.xbxie.mall.admin.service.impl;

import com.xbxie.mall.admin.service.ShopService;
import com.xbxie.mall.admin.vo.BrandDetailVo;
import com.xbxie.mall.admin.vo.ShopVo;
import com.xbxie.mall.common.entity.CommonBrandEntity;
import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.service.CommonShopService;
import com.xbxie.mall.common.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Resource
    private CommonShopService commonShopService;

    @Override
    public R<List<ShopVo>> list() {
        List<CommonShopEntity> list = commonShopService.list();
        if (CollectionUtils.isEmpty(list)) {
            return R.success(new ArrayList<>());
        }

        return R.success(list.stream().map(item -> {
            ShopVo shopVo = new ShopVo();
            BeanUtils.copyProperties(item, shopVo);
            return shopVo;
        }).collect(Collectors.toList()));
    }
}