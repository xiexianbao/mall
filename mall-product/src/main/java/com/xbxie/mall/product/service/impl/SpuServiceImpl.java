package com.xbxie.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xbxie.mall.common.entity.CommonShopEntity;
import com.xbxie.mall.common.entity.CommonSkuEntity;
import com.xbxie.mall.common.entity.CommonSpuAttrEntity;
import com.xbxie.mall.common.entity.CommonSpuEntity;
import com.xbxie.mall.common.service.CommonShopService;
import com.xbxie.mall.common.service.CommonSkuService;
import com.xbxie.mall.common.service.CommonSpuAttrService;
import com.xbxie.mall.common.service.CommonSpuService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.product.service.SpuService;
import com.xbxie.mall.product.vo.SpuDetailVo;
import com.xbxie.mall.product.vo.SpuPageReqVo;
import com.xbxie.mall.product.vo.SpuPageResVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@Service("spuService")
public class SpuServiceImpl implements SpuService {
    @Resource
    private CommonSpuService commonSpuService;
    
    @Resource
    private CommonSpuAttrService commonSpuAttrService;

    @Resource
    private CommonSkuService commonSkuService;

    @Resource
    private CommonShopService commonShopService;

    @Override
    public R<PageData<SpuPageResVo>> pageList(SpuPageReqVo spuPageReqVo) {
        QueryWrapper<CommonSpuEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(spuPageReqVo.getName())) {
            wrapper.like("name", spuPageReqVo.getName());
        }

        Page<CommonSpuEntity> res = commonSpuService.page(new Page<>(spuPageReqVo.getPageNum(), spuPageReqVo.getPageSize()), wrapper);
        PageData<SpuPageResVo> pageData = PageData.getPageData(res.getCurrent(), res.getSize(), res.getTotal(), res.getRecords().stream().map(record -> {
            SpuPageResVo spuPageResVo = new SpuPageResVo();
            BeanUtils.copyProperties(record, spuPageResVo);
            return spuPageResVo;
        }).collect(Collectors.toList()));

        return R.success(pageData);
    }

    @Override
    public R<SpuDetailVo> getSpu(Long id) {
        CommonSpuEntity spuEntity = commonSpuService.getById(id);
        if (spuEntity == null) {
            throw new CustomException("商品不存在");
        }

        SpuDetailVo spuDetailVo = new SpuDetailVo();

        // 基本信息
        BeanUtils.copyProperties(spuEntity, spuDetailVo);
        String imgs = spuEntity.getImgs();
        if (StringUtils.hasLength(imgs)) {
            spuDetailVo.setImgList(Arrays.asList(imgs.split(",")));
        }

        // 店铺信息
        Long shopId = spuEntity.getShopId();
        if (shopId != null) {
            CommonShopEntity commonShopEntity = commonShopService.getById(shopId);
            if (commonShopEntity != null) {
                spuDetailVo.setShopName(commonShopEntity.getName());
            }
        }

        // attr
        List<CommonSpuAttrEntity> spuAttrEntities = commonSpuAttrService.list(new QueryWrapper<CommonSpuAttrEntity>().eq("spu_id", id));
        if (!CollectionUtils.isEmpty(spuAttrEntities)) {
            ArrayList<SpuDetailVo.Attr> attrs = new ArrayList<>();

            for (CommonSpuAttrEntity spuAttrEntity : spuAttrEntities) {
                // 一级属性
                if (spuAttrEntity.getPid() == null) {
                    SpuDetailVo.Attr attr = new SpuDetailVo.Attr();

                    BeanUtils.copyProperties(spuAttrEntity, attr);
                    ArrayList<SpuDetailVo.Attr> children = new ArrayList<>();
                    attr.setChildren(children);

                    // 二级属性
                    for (CommonSpuAttrEntity childAttrEntity : spuAttrEntities) {
                        if (Objects.equals(childAttrEntity.getPid(), attr.getId())) {
                            SpuDetailVo.Attr childAttr = new SpuDetailVo.Attr();

                            BeanUtils.copyProperties(childAttrEntity, childAttr);

                            children.add(childAttr);
                        }
                    }


                    attrs.add(attr);
                }
            }

            spuDetailVo.setAttrs(attrs);
        }

        // sku
        List<CommonSkuEntity> skuEntities = commonSkuService.list(new QueryWrapper<CommonSkuEntity>().eq("spu_id", id));
        if (!CollectionUtils.isEmpty(skuEntities)) {
            spuDetailVo.setSkus(skuEntities.stream().map(skuEntity -> {
                SpuDetailVo.Sku sku = new SpuDetailVo.Sku();
                BeanUtils.copyProperties(skuEntity, sku);
                return sku;
            }).collect(Collectors.toList()));
        }

        return R.success(spuDetailVo);
    }
}