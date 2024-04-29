package com.xbxie.mall.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * created by xbxie on 2024/4/25
 */
@Data
public class RoleUpdateVo {
    /**
     * 角色id
     */
    @NotNull(message = "请输入角色id")
    private Long id;

    /**
     * 角色名
     */
    @NotBlank(message = "请输入角色名")
    private String name;

    /**
     * 角色菜单id集合
     */
    private List<Long> menuIdList;
}
