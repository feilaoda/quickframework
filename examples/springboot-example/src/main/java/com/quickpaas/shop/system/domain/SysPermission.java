package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysPermissionEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@Data
@TableName("sys_permission")
public class SysPermission extends SysPermissionEntity {

    public SysPermissionEntity to(SysPermissionEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysPermission from(SysPermissionEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
