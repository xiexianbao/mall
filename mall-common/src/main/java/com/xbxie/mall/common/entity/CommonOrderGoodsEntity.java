package com.xbxie.mall.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * created by xbxie on 2024-06-01 23:13:13
 */
@Data
@TableName("oms_order_goods")
public class CommonOrderGoodsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 订单商品id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 订单id
	 */
	private Long orderId;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 商品id
	 */
	private Long skuId;

	/**
	 * 商品数量
	 */
	private Integer skuNum;

	/**
	 * 商品总额
	 */
	private BigDecimal amount;

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
