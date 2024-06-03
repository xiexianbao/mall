package com.xbxie.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xbxie.mall.common.bo.TokenBo;
import com.xbxie.mall.common.entity.CommonAddressEntity;
import com.xbxie.mall.common.service.CommonAddressService;
import com.xbxie.mall.common.utils.CustomException;
import com.xbxie.mall.common.utils.JwtUtils;
import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.member.service.AddressService;
import com.xbxie.mall.member.vo.AddressAddVo;
import com.xbxie.mall.member.vo.AddressUpdateVo;
import com.xbxie.mall.member.vo.AddressVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by xbxie on 2024/5/23
 */
@Service("addressService")
public class AddressServiceImpl implements AddressService {
    @Resource
    private CommonAddressService commonAddressService;

    @Override
    public R<AddressVo> getDefault(HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);

        CommonAddressEntity commonAddressEntity = commonAddressService.getOne(new QueryWrapper<CommonAddressEntity>().eq("user_id", tokenBo.getId()).eq("is_default", 1));
        if (commonAddressEntity == null) {
            throw new CustomException("无默认地址");
        }

        AddressVo addressVo = new AddressVo();
        BeanUtils.copyProperties(commonAddressEntity, addressVo);

        return R.success(addressVo);
    }

    @Override
    public R<Void> add(AddressAddVo addressAddVo, HttpServletRequest request) {
        CommonAddressEntity commonAddressEntity = new CommonAddressEntity();
        TokenBo tokenBo = JwtUtils.parseToken(request);

        BeanUtils.copyProperties(addressAddVo, commonAddressEntity);
        commonAddressEntity.setUserId(tokenBo.getId());

        // 如果添加的地址是用户的第一个地址，将其设置为默认地址
        if (!commonAddressService.exists(new QueryWrapper<CommonAddressEntity>().eq("user_id", tokenBo.getId()))) {
            commonAddressEntity.setIsDefault(1);
        }

        if (!commonAddressService.save(commonAddressEntity)) {
            return R.fail("添加地址失败");
        }

        // 如果新增的地址为默认地址，则将其他存在的默认地址改为非默认
        if (commonAddressEntity.getIsDefault() == 1) {
            commonAddressService.update(new UpdateWrapper<CommonAddressEntity>().set("is_default", 0).eq("user_id", tokenBo.getId()).eq("is_default", 1).ne("id", commonAddressEntity.getId()));
        }

        return R.success("添加地址成功");
    }

    @Override
    public R<List<AddressVo>> getList(HttpServletRequest request) {
        TokenBo tokenBo = JwtUtils.parseToken(request);

        List<CommonAddressEntity> list = commonAddressService.list(new QueryWrapper<CommonAddressEntity>().eq("user_id", tokenBo.getId()));

        if(CollectionUtils.isEmpty(list)) {
            return R.success(new ArrayList<AddressVo>());
        }

        return R.success(
            list.stream().map(commonAddressEntity -> {
                AddressVo addressVo = new AddressVo();
                BeanUtils.copyProperties(commonAddressEntity, addressVo);
                return addressVo;
            }).collect(Collectors.toList())
        );
    }

    @Override
    public R<AddressVo> getAddress(Long id) {
        CommonAddressEntity commonAddressEntity = commonAddressService.getById(id);

        if (commonAddressEntity == null) {
            throw new CustomException("地址不存在");
        }

        AddressVo addressVo = new AddressVo();
        BeanUtils.copyProperties(commonAddressEntity, addressVo);

        return R.success(addressVo);
    }

    @Override
    public R<Void> del(Long id) {
        // 删除地址
        if (!commonAddressService.removeById(id)) {
            return R.fail("删除地址失败");
        }

        return R.success("删除地址成功");
    }

    @Override
    public R<Void> updateAddress(AddressUpdateVo addressUpdateVo) {
        // 更新的地址不存在
        Long id = addressUpdateVo.getId();
        if (commonAddressService.getById(id) == null) {
            return R.fail("地址不存在");
        }

        CommonAddressEntity commonAddressEntity = new CommonAddressEntity();
        BeanUtils.copyProperties(addressUpdateVo, commonAddressEntity);

        if (!commonAddressService.updateById(commonAddressEntity)) {
            return R.fail("更新地址失败");
        }

        // 如果更新后的地址为默认地址，则将其他存在的默认地址改为非默认
        if (commonAddressEntity.getIsDefault() == 1) {
            commonAddressService.update(new UpdateWrapper<CommonAddressEntity>().set("is_default", 0).eq("user_id", commonAddressEntity.getUserId()).eq("is_default", 1).ne("id", commonAddressEntity.getId()));
        }

        return R.success("更新地址成功");
    }
}
