package com.xbxie.mall.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xbxie.mall.admin.service.SpuService;
import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.entity.CommonSkuEntity;
import com.xbxie.mall.common.entity.CommonSpuAttrEntity;
import com.xbxie.mall.common.entity.CommonSpuEntity;
import com.xbxie.mall.common.entity.CommonUserEntity;
import com.xbxie.mall.common.service.CommonSkuService;
import com.xbxie.mall.common.service.CommonSpuAttrService;
import com.xbxie.mall.common.service.CommonSpuService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.PageData;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.common.utils.ValidationUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private CommonSkuService commonSkuService;

    @Resource
    private CommonSpuAttrService commonSpuAttrService;

    @Transactional
    @Override
    public R<Void> add(SpuAddVo spuAddVo) {
        // 添加 spu
        CommonSpuEntity spuEntity = new CommonSpuEntity();
        BeanUtils.copyProperties(spuAddVo, spuEntity);
        // 新增商品默认为未上架
        spuEntity.setStatus(0);
        if (!CollectionUtils.isEmpty(spuAddVo.getImgList())) {
            spuEntity.setImgs(String.join(",", spuAddVo.getImgList()));
        }
        if (!commonSpuService.save(spuEntity)) {
            throw new  CustomException("添加商品失败");
        }

        // 添加 attr
        List<SpuAddVo.Attr> attrs = spuAddVo.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            for (SpuAddVo.Attr attr : attrs) {
                List<SpuAddVo.Attr> children = attr.getChildren();
                // 添加父级 attr
                CommonSpuAttrEntity spuAttrEntity = new CommonSpuAttrEntity();

                spuAttrEntity.setSpuId(spuEntity.getId());
                spuAttrEntity.setName(attr.getName());

                if (!commonSpuAttrService.save(spuAttrEntity)) {
                    throw new CustomException("添加属性失败");
                }

                // 添加子级 attr
                if (!CollectionUtils.isEmpty(children)) {
                    List<CommonSpuAttrEntity> spuAttrEntities = new ArrayList<>();

                    for (SpuAddVo.Attr attrChild : children) {
                        CommonSpuAttrEntity spuAttrEntityChild = new CommonSpuAttrEntity();

                        spuAttrEntityChild.setSpuId(spuEntity.getId());
                        spuAttrEntityChild.setPid(spuAttrEntity.getId());
                        spuAttrEntityChild.setName(attrChild.getName());

                        spuAttrEntities.add(spuAttrEntityChild);
                    }

                    if (!commonSpuAttrService.saveBatch(spuAttrEntities)) {
                        throw new CustomException("添加属性失败");
                    }
                }
            }
        }

        // 添加 sku
        List<SpuAddVo.Sku> skus = spuAddVo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            List<CommonSkuEntity> skuEntities = skus.stream().map(sku -> {
                CommonSkuEntity skuEntity = new CommonSkuEntity();

                BeanUtils.copyProperties(sku, skuEntity);
                skuEntity.setSpuId(spuEntity.getId());
                skuEntity.setShopId(spuAddVo.getShopId());

                return skuEntity;
            }).collect(Collectors.toList());

            if (!commonSkuService.saveBatch(skuEntities)) {
                throw new  CustomException("添加商品失败");
            }
        }

        return R.success("添加商品成功");
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

    @Override
    public R<PageData<SpuPageResVo>> pageList(SpuPageReqVo spuPageReqVo) {
        QueryWrapper<CommonSpuEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasLength(spuPageReqVo.getName())) {
            wrapper.like("name", spuPageReqVo.getName());
        }
        if (spuPageReqVo.getBrandId() != null) {
            wrapper.eq("brand_id", spuPageReqVo.getBrandId());
        }
        if (spuPageReqVo.getCategoryId() != null) {
            wrapper.eq("category_id", spuPageReqVo.getCategoryId());
        }

        Page<CommonSpuEntity> res = commonSpuService.page(new Page<>(spuPageReqVo.getPageNum(), spuPageReqVo.getPageSize()), wrapper);
        PageData<SpuPageResVo> pageData = PageData.getPageData(res.getCurrent(), res.getSize(), res.getTotal(), res.getRecords().stream().map(record -> {
            SpuPageResVo spuPageResVo = new SpuPageResVo();
            BeanUtils.copyProperties(record, spuPageResVo);
            return spuPageResVo;
        }).collect(Collectors.toList()));

        return R.success(pageData);
    }

    @Transactional()
    @Override
    public R<Void> updateSpu(SpuUpdateVo spuUpdateVo) {
        // spu
        CommonSpuEntity spuEntity = commonSpuService.getById(spuUpdateVo.getId());
        if (spuEntity == null) {
            return R.fail("商品不存在");
        }

        BeanUtils.copyProperties(spuUpdateVo, spuEntity);
        if (!CollectionUtils.isEmpty(spuUpdateVo.getImgList())) {
            spuEntity.setImgs(String.join(",", spuUpdateVo.getImgList()));
        }

        if (!commonSpuService.updateById(spuEntity)) {
            return R.fail("更新商品失败");
        }

        // attr
        // 先删除、后添加
        QueryWrapper<CommonSpuAttrEntity> attrQueryWrapper = new QueryWrapper<CommonSpuAttrEntity>().eq("spu_id", spuUpdateVo.getId());
        if (commonSpuAttrService.exists(attrQueryWrapper)) {
            if (!commonSpuAttrService.remove(attrQueryWrapper)) {
                return R.fail("更新商品失败");
            }
        }

        List<SpuUpdateVo.Attr> attrs = spuUpdateVo.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            for (SpuUpdateVo.Attr attr : attrs) {
                List<SpuUpdateVo.Attr> children = attr.getChildren();
                // 添加父级 attr
                CommonSpuAttrEntity spuAttrEntity = new CommonSpuAttrEntity();

                spuAttrEntity.setSpuId(spuEntity.getId());
                spuAttrEntity.setName(attr.getName());

                if (!commonSpuAttrService.save(spuAttrEntity)) {
                    return R.fail("更新商品失败");
                }

                // 添加子级 attr
                if (!CollectionUtils.isEmpty(children)) {
                    List<CommonSpuAttrEntity> spuAttrEntities = new ArrayList<>();

                    for (SpuUpdateVo.Attr attrChild : children) {
                        CommonSpuAttrEntity spuAttrEntityChild = new CommonSpuAttrEntity();

                        spuAttrEntityChild.setSpuId(spuEntity.getId());
                        spuAttrEntityChild.setPid(spuAttrEntity.getId());
                        spuAttrEntityChild.setName(attrChild.getName());

                        spuAttrEntities.add(spuAttrEntityChild);
                    }

                    if (!commonSpuAttrService.saveBatch(spuAttrEntities)) {
                        return R.fail("更新商品失败");
                    }
                }
            }
        }

        // sku 先删除、后添加
        QueryWrapper<CommonSkuEntity> skuQueryWrapper = new QueryWrapper<CommonSkuEntity>().eq("spu_id", spuUpdateVo.getId());
        if (commonSkuService.exists(skuQueryWrapper)) {
            if (!commonSkuService.remove(skuQueryWrapper)) {
                return R.fail("更新商品失败");
            }
        }
        List<SpuUpdateVo.Sku> skus = spuUpdateVo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            List<CommonSkuEntity> skuEntities = skus.stream().map(sku -> {
                CommonSkuEntity skuEntity = new CommonSkuEntity();

                BeanUtils.copyProperties(sku, skuEntity);
                skuEntity.setSpuId(spuEntity.getId());
                skuEntity.setShopId(spuUpdateVo.getShopId());

                return skuEntity;
            }).collect(Collectors.toList());

            if (!commonSkuService.saveBatch(skuEntities)) {
                return R.fail("更新商品失败");
            }
        }

        return R.success("更新商品成功");
    }

    @Override
    public R<Void> changeStatus(UpdateSpuStatusReqVo updateSpuStatusReqVo) {
        CommonSpuEntity commonSpuEntity = commonSpuService.getById(updateSpuStatusReqVo.getId());
        if (commonSpuEntity == null) {
            return R.fail("商品不存在");
        }


        BeanUtils.copyProperties(updateSpuStatusReqVo, commonSpuEntity);

        if (!commonSpuService.updateById(commonSpuEntity)) {
            return R.fail("更新状态失败");
        }

        return R.success("更新状态成功");
    }
}