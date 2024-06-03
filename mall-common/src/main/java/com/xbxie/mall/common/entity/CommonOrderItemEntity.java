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
@TableName("oms_order_item")
public class CommonOrderItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 子订单id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 父订单id
	 */
	private Long pid;

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
	 * 子订单号
	 */
	private String sn;

	/**
	 * 子订单金额
	 */
	private BigDecimal amount;

	/**
	 * 订单状态 1：待付款、2：待发货、3：待收货、4：售后中、5：已取消、6：完成
	 */
	private Integer status;

	/**
	 * 支付状态 1：未支付、2：已支付、3：已退款、4：已关闭
	 */
	private Integer payStatus;

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
