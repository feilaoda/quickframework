package com.quickpaas.framework.domain;

import com.quickpaas.framework.entity.BaseEntity;

public interface BaseDomain<E extends BaseEntity> {
    Long getId();
    void setId(Long id);
    default E to()  { return to(null);}
    E to(E E);
    BaseDomain<E> from(E entity);

    static <D extends BaseDomain, E extends BaseEntity> D convertFrom(E entity) {
        return null;
    }
}
