package com.xbxie.mall.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * created by xbxie on 2024-05-23 18:28:12
 */
@Data
@TableName("ums_address")
public class CommonAddressEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 地址id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 省 格式：code_name
	 */
	private String province;

	/**
	 * 市 格式：code_name
	 */
	private String city;

	/**
	 * 区 格式：code_name
	 */
	private String county;

	/**
	 * 详细地址
	 */
	private String detail;

	/**
	 * 是否为默认地址，1：是，0：否
	 */
	private Integer isDefault;

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
