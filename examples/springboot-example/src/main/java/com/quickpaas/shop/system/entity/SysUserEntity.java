package com.quickpaas.shop.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.quickpaas.framework.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@Data

public class SysUserEntity extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     *
     */
    private String account;


    /**
     *
     */
    @JsonIgnore
    private String password;


    /**
     *
     */
    private String avatar;


    /**
     *
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long departmentId;


    /**
     *
     */
    private String email;


    /**
     *
     */
    private String name;


    /**
     *
     */
    private String phone;


    /**
     *
     */
    private String salt;


    /**
     *
     */
    private Integer gender;


    /**
     *
     */
    private Integer status;


    /**
     *
     */
    private Integer version;


}
