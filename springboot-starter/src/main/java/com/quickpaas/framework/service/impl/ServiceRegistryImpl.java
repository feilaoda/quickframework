package com.quickpaas.framework.mybatisplus.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.entity.BaseEntity;
import com.quickpaas.framework.service.QuickService;
import com.quickpaas.framework.service.ServiceRegistry;
import com.quickpaas.framework.utils.NameUtils;
import com.quickpaas.framework.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class ServiceRegistryImpl implements ServiceRegistry, BeanPostProcessor , ApplicationListener<ApplicationReadyEvent> {
    private ClassCache classCache;
    private Map<String, QuickService<? extends BaseEntity, ? extends BaseDomain>> services = new HashMap<>();

    public ServiceRegistryImpl(ClassCache classCache) {
        this.classCache = classCache;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext context = applicationReadyEvent.getApplicationContext();

        Map<String, Object> beans = context.getBeansWithAnnotation(Service.class);
        beans.forEach((name, bean) -> {
            if(bean instanceof QuickService) {
                Class<?> clz =  ReflectionUtils.getSuperClassGenericType(bean.getClass().getSuperclass(), 1);
                Class<?> domainClass =  ReflectionUtils.getSuperClassGenericType(bean.getClass().getSuperclass(), 2);
                services.put(clz.getSimpleName(), (QuickService)bean);
                services.put(domainClass.getSimpleName(), (QuickService)bean);
                classCache.add(domainClass.getSimpleName(), domainClass);

                Map<String, String> tableColumns = new LinkedHashMap<>();
                Arrays.stream(domainClass.getDeclaredFields()).forEach(field-> {
                    TableField tableField = field.getAnnotation(TableField.class);

                    if(tableField == null || StringUtils.isEmpty(tableField.value())) {
                        tableColumns.put(field.getName(), NameUtils.toColumnName(field.getName()));
                    }else {
                        tableColumns.put(field.getName(), tableField.value());
                    }
                });
                classCache.addColumns(domainClass, tableColumns);
            }
        });


    }

    public void addService(String name, QuickService<?,?> QuickService) {
        services.put(name, QuickService);
    }

    public <E extends BaseEntity, V extends BaseDomain<E>> QuickService<E,V> findService(String name) {
        return (QuickService<E, V>) services.get(name);
    }
}
