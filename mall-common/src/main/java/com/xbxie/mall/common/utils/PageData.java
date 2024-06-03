package com.xbxie.mall.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页数据工具类
 * created by xbxie on 2024/4/24
 */
@Data
public class PageData<E> {
    /**
     * 当前页码
     */
    private Long pageNum;

    /**
     * 每页包含的数据条数
     */
    private Long pageSize;

    /**
     * 当前页的数据
     */
    private List<E> list;

    /**
     * 数据总条数
     */
    private Long total;

    public static <T> PageData<T> getPageData(Page<T> page) {
        PageData<T> pageData = new PageData<>();

        pageData.setPageSize(page.getSize());
        pageData.setPageNum(page.getCurrent());
        pageData.setList(page.getRecords());
        pageData.setTotal(page.getTotal());

        return pageData;
    }

    public static <T> PageData<T> getPageData(Long pageNum, Long pageSize, Long total, List<T> list) {
        PageData<T> pageData = new PageData<>();

        pageData.setPageNum(pageNum);
        pageData.setPageSize(pageSize);
        pageData.setList(list);
        pageData.setTotal(total);

        return pageData;
    }
}
