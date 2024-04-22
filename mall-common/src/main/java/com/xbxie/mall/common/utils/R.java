package com.xbxie.mall.common.utils;

import java.util.HashMap;


public class R extends HashMap<String, Object> {
    public static R success() {
        R r = new R();
        r.put("code", 0);
        r.put("message", "success");
        return r;
    }

    public static R success(String message) {
        R r = new R();
        r.put("code", 0);
        r.put("message", message);
        return r;
    }

    public static R success(Object data) {
        R r = new R();
        r.put("code", 0);
        r.put("message", "success");
        r.put("data", data);
        return r;
    }

    public static R fail(int code, String message) {
        R r = new R();
        r.put("code", code);
        r.put("message", message);
        return r;
    }

    public static R fail(String message) {
        R r = new R();
        r.put("code", 500);
        r.put("message", message);
        return r;
    }
}