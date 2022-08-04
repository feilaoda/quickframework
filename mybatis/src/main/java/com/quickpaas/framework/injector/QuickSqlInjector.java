package com.quickpaas.framework.injector;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.apache.ibatis.builder.MapperBuilderAssistant;

import java.util.List;
import java.util.Set;

public class QuickSqlInjector extends DefaultSqlInjector {

    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        Class<?> modelClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
        if (modelClass != null) {
            String className = mapperClass.toString();
            Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
            if (!mapperRegistryCache.contains(className)) {

                TableName tableName = modelClass.getAnnotation(TableName.class);
                Class<?> domainClass = modelClass;
                if(tableName == null) {
                    domainClass = modelClass.getSuperclass();
                }

                TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant, domainClass);
                List<AbstractMethod> methodList = this.getMethodList(mapperClass, tableInfo);
                if (CollectionUtils.isNotEmpty(methodList)) {
                    methodList.forEach((m) -> {
                        m.inject(builderAssistant, mapperClass, modelClass, tableInfo);
                    });
                } else {
                    this.logger.debug(mapperClass.toString() + ", No effective injection method was found.");
                }

                mapperRegistryCache.add(className);
            }
        }
    }
}
