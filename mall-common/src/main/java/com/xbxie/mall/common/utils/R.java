package com.xbxie.mall.common.utils;

import lombok.Data;
import java.io.Serializable;


@Data
public class R<D> implements Serializable {
    private int code;
    
    private String msg;
    
    private D data;

    public static R<Void> success() {
        R<Void> r = new R<>();
        r.setCode(0);
        r.setMsg("success");
        return r;
    }

    public static R<Void> success(String msg) {
        R<Void> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> successData(T data) {
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg("success");
        r.setData(data);
        return r;
    }
    

    public static R<Void> fail(int code, String msg) {
        R<Void> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static R<Void> fail(String msg) {
        R<Void> r = new R<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static R<Void> fail() {
        R<Void> r = new R<>();
        r.setCode(500);
        r.setMsg("fail");
        return r;
    }
}