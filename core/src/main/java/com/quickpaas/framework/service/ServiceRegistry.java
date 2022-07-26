package com.quickpaas.framework.service;

import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.entity.BaseEntity;

public interface ServiceRegistry  {
    <E extends BaseEntity, V extends BaseDomain<E>> QuickService<E,V> findService(String name);
}
