package com.xbxie.mall.common.utils;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * created by xbxie on 2024/6/4
 */
public class GoodsUtils {
    public static String getAttrValues(String attrs) {
        Map<String, String> attrsMap = (Map<String, String>) JSON.parse(attrs);
        if(attrsMap == null) {
            return "";
        } else {
            return String.join(",", attrsMap.values());
        }
    }
}
