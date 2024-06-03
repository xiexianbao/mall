package com.xbxie.mall.member.service;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.member.vo.AddressAddVo;
import com.xbxie.mall.member.vo.AddressUpdateVo;
import com.xbxie.mall.member.vo.AddressVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xbxie on 2024/5/23
 */
public interface AddressService {
    R<AddressVo> getDefault(HttpServletRequest request);

    R<Void> add(AddressAddVo addressAddVo, HttpServletRequest request);

    R<List<AddressVo>> getList(HttpServletRequest request);

    R<AddressVo> getAddress(Long id);

    R<Void> del(Long id);

    R<Void> updateAddress(AddressUpdateVo addressUpdateVo);
}
