package com.xbxie.mall.admin.service;

import com.xbxie.mall.admin.vo.*;
import com.xbxie.mall.common.utils.R;
import java.util.List;

/**
 * created by xbxie on 2024-05-16 23:53:49
 */
public interface CategoryService {
    R<List<CategoryResVo>> listTree(CategoryReqVo categoryReqVo);

    R<Void> add(CategoryAddVo categoryAddVo);

    R<List<CategoryVo>> getList();

    R<Void> del(Long id);

    R<CategoryDetailVo> getCategory(Long id);

    R<Void> updateCategory(CategoryUpdateVo categoryUpdateVo);
}

