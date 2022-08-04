package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysRoleSysMenuEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@Data
@TableName("sys_role_sys_menu")
public class SysRoleSysMenu extends SysRoleSysMenuEntity {

    public SysRoleSysMenuEntity to(SysRoleSysMenuEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysRoleSysMenu from(SysRoleSysMenuEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
