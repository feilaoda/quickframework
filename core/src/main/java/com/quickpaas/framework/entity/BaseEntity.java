package com.quickpaas.framework.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    protected Long id;

    /**
     * 创建时间/注册时间
     */
    protected Date createdAt;


    /**
     * 最后更新时间
     */
    protected Date updatedAt;


}
