/*
 * Copyright(c) 2017 kashuo.net All rights reserved.
 */
package com.quickpaas.framework.domain;


import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

/**
 * @author fld
 *  2017-11-02 18:58:43
 */
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data; //单个数据

    private Page page;


    @SuppressWarnings(value = "unchecked")
    public static <T> Result<T> ok() {
        return new Result(0, null);
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> Result<T> ok(int code, String message) {
        return new Result(code, message);
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> Result<T> error() {
        return new Result(StatusCode.ERROR, null);
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> Result<T> error(int code, String message) {
        return new Result(code, message);
    }

    public static <T  extends List> Result<T> list(IPage ipage) {
        Result<T> res = Result.ok();
        res.data = (T)ipage.getRecords();
        Page page = new Page();
        page.setPages(ipage.getPages());
        page.setTotal(ipage.getTotal());
        page.setCurrent(ipage.getCurrent());
        page.setSize(ipage.getSize());
        res.page = page;
        return res;
    }

    public static <T  extends List> Result<T> list(T data) {
        Result<T> res = new Result(0, null);
        Page page = new Page();
        page.setPages(1);
        page.setTotal(data.size());
        page.setCurrent(1);
        page.setSize(data.size());
        res.page = page;
        res.data = (T)data;
        return res;
    }



    public Result() {
        this(0, null);
    }


    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return code == 0;
    }


    public static <T> Result<T> ok(T obj) {
        return one(obj, null);
    }

    public static <T> Result<T> one(T obj) {
        return one(obj, null);
    }

    public static <T> Result<T> one(T obj, String message) {
        return one(0, obj, null);
    }

    public static <T> Result<T> one(int code, T obj, String message) {
        Result<T> res = new Result(code, message);
        res.data = obj;
        return res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
