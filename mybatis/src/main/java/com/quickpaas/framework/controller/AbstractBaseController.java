package com.quickpaas.framework.controller;

import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseEntity;
import com.quickpaas.framework.domain.Page;
import com.quickpaas.framework.exception.QuickException;
import com.quickpaas.framework.exception.WebException;
import com.quickpaas.framework.quickql.Query;
import com.quickpaas.framework.quickql.QueryParser;
import com.quickpaas.framework.quickql.QueryRequest;
import com.quickpaas.framework.service.BaseService;
import com.quickpaas.framework.service.ServiceRegistry;
import com.quickpaas.framework.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.quickpaas.framework.exception.WebErrorCode.ERROR_404;

@Slf4j
public abstract class AbstractBaseController<T extends BaseEntity<?>> {
    protected BaseService<T> baseService;
    @Autowired
    protected ServiceRegistry serviceRegistry;

    @Autowired
    protected ClassCache classCache;


    public AbstractBaseController() {
    }

    public AbstractBaseController(BaseService baseService) {
        this.baseService = baseService;
    }

    public BaseService<T> getBaseService() {
        return baseService;
    }


    private QueryRequest createRequest(String body, Class<T> clazz) {
        QueryParser parser = new QueryParser(classCache);
        QueryRequest queryRequest = parser.parseQueryRequest(clazz, body);
        return queryRequest;
    }

    public Page<T> findList(String body) {
        Query query = Query.create(body);
        return baseService.findPage(query);
    }

    public void beforeQuery(QueryRequest request) {
    }

    public T getEntity(Long id) {
        return getBaseService().findById(id);
    }

    public T getEntity(Long id, String body) {
        Query query = Query.create(body);
        query.eq("id", id);
        return getBaseService().findOne(query);
    }

    public T getEntity(Long id, T dto) {
        return getEntity(id);
    }

    public T findOne(Query query) {
        return baseService.findOne(query);
    }

    public List<T> findList(Query query) {
        return baseService.findList(query);
    }

    public T createEntity(T dto) {
        baseService.beforeSave(dto);
        getBaseService().save(dto);
        saveManyToMany(dto);
        return dto;
    }


    public T updateEntity(Serializable id, T dto) {
        baseService.beforeSave(dto);
        T domain = getBaseService().findById(dto.tid());
        if (domain == null) {
            throw new WebException(ERROR_404);
        }
        dto.setUpdatedAt(new Date());
        getBaseService().updateById(dto);
        saveManyToMany(dto);
        return dto;
    }

    public void saveManyToMany(T dto) {
        baseService.saveManyToMany(dto);
    }

    public T save(T dto) {
        baseService.beforeSave(dto);

        if(dto.tid() != null) {
            T domain = getBaseService().findById(dto.tid());
            if (domain == null) {
                throw new WebException(ERROR_404);
            }
            dto.setUpdatedAt(new Date());
            getBaseService().updateById(dto);
        }else {
            dto.setCreatedAt(new Date());
            dto.setUpdatedAt(new Date());
            getBaseService().save(dto);
        }
        saveManyToMany(dto);
        return dto;
    }



    public Boolean deleteEntity(Long id, T dto) {
        T domain = getBaseService().findById(id);
        if (domain == null) {
            return true;
        }
        boolean res = getBaseService().removeById(id);
        return res;
    }

}
