package com.quickpaas.framework.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {
    Class targetDomain() ;
    Class middleDomain() ;
    String leftMappedField();
    String rightMappedField();
}
