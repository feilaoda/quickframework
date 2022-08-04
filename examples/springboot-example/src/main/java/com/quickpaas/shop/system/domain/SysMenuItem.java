package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysMenuItemEntity;
import lombok.Data;

/**
 * Description:
 *
 * @author feilaoda
 * @email 
 *
 */
@Data
@TableName("sys_menu_item")
public class SysMenuItem extends SysMenuItemEntity {

    public SysMenuItemEntity to(SysMenuItemEntity entity) {
        if(entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysMenuItem from(SysMenuItemEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
