package com.xbxie.mall.common.utils;

/**
 * 验证实体类中某些字段值是否在（数据库字段的）指定范围之内
 * created by xbxie on 2024/4/20
 */
public class ValidationUtils {
    public static boolean statusPass(Integer status) {
        return status != null && (status == 0 || status == 1);
    }
}
