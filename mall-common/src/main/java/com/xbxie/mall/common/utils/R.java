package com.xbxie.mall.common.utils;

import lombok.Data;

import java.util.HashMap;

@Data
public class R<D> {
    private int code;
    
    private String message;
    
    private D data;

    public static R success() {
        R r = new R();
        r.setCode(0);
        r.setMessage("success");
        return r;
    }

    public static R success(String message) {
        R r = new R();
        r.setCode(0);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<T>();
        r.setCode(0);
        r.setMessage("success");
        r.setData(data);
        return r;
    }
    

    public static R fail(int code, String message) {
        R r = new R();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static R<?> fail(String message) {
        R<?> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static R<?> fail() {
        R<?> r = new R<>();
        r.setCode(500);
        r.setMessage("fail");
        return r;
    }
}