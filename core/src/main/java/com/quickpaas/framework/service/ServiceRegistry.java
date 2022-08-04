package com.quickpaas.framework.service;

import com.quickpaas.framework.domain.BaseDomain;

public interface ServiceRegistry  {
    <T extends BaseDomain> BaseQuickService<T> findService(String name);
}
