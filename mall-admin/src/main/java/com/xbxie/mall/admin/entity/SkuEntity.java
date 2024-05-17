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
@TableName("pms_sku")
public class SkuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 商品spuId
	 */
	private Long spuId;

	/**
	 * sku名称
	 */
	private String name;

	/**
	 * sku价格
	 */
	private BigDecimal price;

	/**
	 * sku图片
	 */
	private String img;

	/**
	 * sku属性
	 */
	private String attrs;

	/**
	 * sku库存
	 */
	private Integer stock;

	/**
	 * 是否删除 0：未删除，1：已删除
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
