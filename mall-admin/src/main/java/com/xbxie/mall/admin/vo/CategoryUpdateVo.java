package com.xbxie.mall.admin.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryUpdateVo {
    /**
     * id
     */
    @NotNull(message = "请输入id")
    private Long id;

    /**
     * 父分类id
     */
    private Long pid;

    /**
     * 分类名称
     */
    @NotBlank(message = "请输入分类名称")
    private String name;


    /**
     * 分类图片
     */
    @NotBlank(message = "请输入分类图片")
    private String img;
}
