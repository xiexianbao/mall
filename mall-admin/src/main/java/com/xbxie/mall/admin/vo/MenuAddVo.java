package com.xbxie.mall.admin.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * created by xbxie on 2024/4/25
 */
@Data
public class MenuAddVo {
    /**
     * 父菜单id
     */
    private Long pid;

    /**
     * 菜单名称
     */
    @NotBlank(message = "请输入菜单名称")
    private String name;

    /**
     * 菜单路径
     */
    @Pattern(regexp = "^(\\s*)|(/.*)$", message = "菜单路径需要以斜杠/开头")
    @NotBlank(message = "请输入菜单路径")
    private String url;
}

//