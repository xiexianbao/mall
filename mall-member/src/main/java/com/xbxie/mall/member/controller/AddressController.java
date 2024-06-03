package com.xbxie.mall.member.controller;

import com.xbxie.mall.common.utils.R;
import com.xbxie.mall.member.service.AddressService;
import com.xbxie.mall.member.vo.AddressAddVo;
import com.xbxie.mall.member.vo.AddressUpdateVo;
import com.xbxie.mall.member.vo.AddressVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * created by xbxie on 2024-05-23 18:28:12
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Resource
    private AddressService addressService;

    @PostMapping("/default")
    public R<AddressVo> getDefault(HttpServletRequest request) {
        return addressService.getDefault(request);
    }

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody AddressAddVo addressAddVo, HttpServletRequest request) {
        return addressService.add(addressAddVo, request);
    }

    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody AddressUpdateVo addressUpdateVo) {
        return addressService.updateAddress(addressUpdateVo);
    }

    @PostMapping("/{id}")
    public R<AddressVo> getAddress(@PathVariable("id") Long id) {
        return addressService.getAddress(id);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return addressService.del(id);
    }

    @PostMapping("/list")
    public R<List<AddressVo>> getList(HttpServletRequest request) {
        return addressService.getList(request);
    }
}
