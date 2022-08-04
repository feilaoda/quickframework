package com.quickpaas.shop.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.quickpaas.framework.annotation.OneToMany;
import com.quickpaas.framework.annotation.OneToOne;
import com.quickpaas.framework.utils.ObjectUtils;
import com.quickpaas.shop.system.entity.SysDepartmentEntity;
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
@TableName("sys_department")
public class SysDepartment extends SysDepartmentEntity {
    //@Join
    @OneToOne(target = SysDepartment.class, mappedBy = "parentId")
    @TableField(exist = false)
    private SysDepartment parent;

    //@Join
    @OneToMany(target = SysUser.class, mappedBy = "departmentId")
    @TableField(exist = false)
    private List<SysUser> users;

    public SysDepartmentEntity to(SysDepartmentEntity entity) {
        if (entity == null) {
            return this;
        }
        return ObjectUtils.map(entity, this);
    }

    public SysDepartment from(SysDepartmentEntity entity) {
        return ObjectUtils.map(this, entity);
    }

}
