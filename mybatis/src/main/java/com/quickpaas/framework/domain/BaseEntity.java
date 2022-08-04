package com.quickpaas.framework.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public class BaseEntity<T extends Serializable> extends BaseDomain<T>{
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId
    protected T id;

    /**
     * 创建时间/注册时间
     */
    protected Date createdAt;


    /**
     * 最后更新时间
     */
    protected Date updatedAt;

    @Override
    public T tid() {
        return id;
    }


    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
