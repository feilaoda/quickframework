package com.quickpaas.framework.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.quickpaas.framework.cache.ClassCache;
import com.quickpaas.framework.domain.BaseDomain;
import com.quickpaas.framework.service.BaseQuickService;
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
    private Map<String, BaseQuickService< ? extends BaseDomain>> services = new HashMap<>();

    public ServiceRegistryImpl(ClassCache classCache) {
        this.classCache = classCache;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext context = applicationReadyEvent.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(Service.class);
        beans.forEach((name, bean) -> {
            if(bean instanceof BaseQuickService) {
                Class<?> clz =  ReflectionUtils.getSuperClassGenericType(bean.getClass().getSuperclass(), 1);
                services.put(clz.getSimpleName(), (BaseQuickService)bean);
                classCache.add(clz.getSimpleName(), clz);
                Map<String, String> tableColumns = new LinkedHashMap<>();
                Arrays.stream(clz.getDeclaredFields()).forEach(field-> {
                    TableField tableField = field.getAnnotation(TableField.class);
                    if(tableField == null || StringUtils.isEmpty(tableField.value())) {
                        tableColumns.put(field.getName(), NameUtils.toColumnName(field.getName()));
                    }else {
                        tableColumns.put(field.getName(), tableField.value());
                    }
                });
                classCache.addColumns(clz, tableColumns);
            }
        });
    }

    public void addService(String name, BaseQuickService<?> QuickService) {
        services.put(name, QuickService);
    }

    public <V extends BaseDomain> BaseQuickService<V> findService(String name) {
        return (BaseQuickService<V>) services.get(name);
    }
}
