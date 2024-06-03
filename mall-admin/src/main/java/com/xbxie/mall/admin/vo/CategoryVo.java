package com.xbxie.mall.admin.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * created by xbxie on 2024/5/17
 */
@Data
public class CategoryVo {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 父分类id
     */
    private Long pid;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图片
     */
    private String img;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
