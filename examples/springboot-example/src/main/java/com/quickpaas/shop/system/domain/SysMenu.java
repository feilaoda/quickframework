package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysMenuEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author feilaoda
 * @email
 *
 */
@Data
@TableName("sys_menu")
public class SysMenu extends SysMenuEntity {

    public SysMenuEntity to(SysMenuEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysMenu from(SysMenuEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
