package com.xbxie.mall.common.utils;

import lombok.Data;


@Data
public class R<D> {
    private int code;
    
    private String message;
    
    private D data;

    public static R<Void> success() {
        R<Void> r = new R<>();
        r.setCode(0);
        r.setMessage("success");
        return r;
    }

    public static R<Void> success(String message) {
        R<Void> r = new R<>();
        r.setCode(0);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMessage("success");
        r.setData(data);
        return r;
    }
    

    public static R<Void> fail(int code, String message) {
        R<Void> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static R<Void> fail(String message) {
        R<Void> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static R<Void> fail() {
        R<Void> r = new R<>();
        r.setCode(500);
        r.setMessage("fail");
        return r;
    }
}