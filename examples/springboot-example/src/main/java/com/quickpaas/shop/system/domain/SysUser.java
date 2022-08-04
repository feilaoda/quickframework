package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.annotation.ManyToMany;
import com.quickpaas.framework.annotation.ManyToOne;
import com.quickpaas.shop.system.entity.SysUserEntity;
import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 * 
 */
@Data
@TableName("sys_user")
public class SysUser extends SysUserEntity {
    //@Join
    @ManyToOne(target = SysDepartment.class, mappedBy = "departmentId")
    @TableField(exist = false)
    private SysDepartment department;


    //@Join
    @ManyToMany(target = SysRole.class, joinEntity = SysUserSysRole.class, leftMappedBy = "userId", rightMappedBy = "roleId")
    @javax.persistence.ManyToMany
    @TableField(exist = false)
    private List<SysRole> roles;


}
