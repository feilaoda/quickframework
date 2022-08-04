package com.quickpaas.shop.controller;

import com.quickpaas.framework.controller.AbstractBaseController;
import com.quickpaas.framework.domain.BaseEntity;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.quickql.Query;
import com.quickpaas.framework.service.BaseService;

import java.io.Serializable;

public class BaseShopController<T extends BaseEntity<Long>> extends AbstractBaseController<T> {
    public BaseShopController(BaseService baseService) {
        super(baseService);
    }

    public void checkUnique(String name, String column, Object value) {
        this.checkUnique((Serializable)null, name, column, value);
    }

    public void checkUnique(Serializable id, String name, String column, Object value) {
        Long count = this.findCount(id, column, value);
        if (count > 0L) {
            throw new QuickException(name + "已经存在");
        }
    }

    public Long findCount(Serializable id, String column, Object value) {
        Query query = new Query();
        if (id != null) {
            query.ne("id", id);
        }

        query.eq(column, value);
        return this.baseService.count(query);
    }
}
