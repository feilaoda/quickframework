package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.annotation.ManyToMany;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysRoleEntity;
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
@TableName("sys_role")
public class SysRole extends SysRoleEntity {

    //@Join
    @ManyToMany(target = SysPermission.class, joinEntity = SysRoleSysPermission.class, leftMappedBy = "roleId", rightMappedBy = "permissionId")
    @TableField(exist = false)
    private List<SysPermission> permissions;

    //@Join
    @ManyToMany(target = SysMenu.class, joinEntity = SysRoleSysMenu.class, leftMappedBy = "roleId", rightMappedBy = "menuId")
    @TableField(exist = false)
    private List<SysMenu> menus;


    public SysRoleEntity to(SysRoleEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysRole from(SysRoleEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
