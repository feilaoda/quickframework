package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysRoleSysPermissionEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@Data
@TableName("sys_role_sys_permission")
public class SysRoleSysPermission extends SysRoleSysPermissionEntity {

    public SysRoleSysPermissionEntity to(SysRoleSysPermissionEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysRoleSysPermission from(SysRoleSysPermissionEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
