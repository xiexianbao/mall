package com.xbxie.mall.admin.controller;

import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import com.xbxie.mall.admin.service.CategoryService;
import java.util.List;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @PostMapping("/add")
    public R<Void> add(@Validated @RequestBody CategoryAddVo categoryAddVo) {
        return categoryService.add(categoryAddVo);
    }

    @PostMapping("/del/{id}")
    public R<Void> del(@PathVariable("id") Long id) {
        return categoryService.del(id);
    }

    @PostMapping("/update")
    public R<Void> updateCategory(@Validated @RequestBody CategoryUpdateVo categoryUpdateVo) {
        return categoryService.updateCategory(categoryUpdateVo);
    }

    @PostMapping("/{id}")
    public R<CategoryDetailVo> getCategory(@PathVariable("id") Long id) {
        return categoryService.getCategory(id);
    }

    @PostMapping("/listTree")
    public R<List<CategoryResVo>> listTree(@Validated @RequestBody CategoryReqVo categoryReqVo) {
        return categoryService.listTree(categoryReqVo);
    }

    @PostMapping("/list")
    public R<List<CategoryVo>> getList() {
        return categoryService.getList();
    }
}
