package com.xbxie.mall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * created by xbxie on 2024-05-16 23:53:48
 */
@Data
@TableName("pms_spu")
public class SpuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 品牌id
	 */
	private Long brandId;

	/**
	 * 商品分类id
	 */
	private Long categoryId;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 商品价格
	 */
	private BigDecimal price;

	/**
	 * 商品详情
	 */
	private String detail;

	/**
	 * 商品轮播图
	 */
	private String imgs;

	/**
	 * 是否删除 0：未删除，1已删除
	 */
	@TableLogic
	private Integer isDel;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
