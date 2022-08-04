package com.quickpaas.shop.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.quickpaas.framework.injector.QuickSqlInjector;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
@MapperScan("com.quickpaas.shop.*.mapper")
public class MybatisPlusConfig {

    @Bean
    public QuickSqlInjector quickSqlInjector() {
        return new QuickSqlInjector();
    }

//    @Bean
//    public GlobalConfig globalConfig() {
//        GlobalConfig config = new GlobalConfig();
//        config.setSqlInjector(quickSqlInjector());
//        config.setMetaObjectHandler(new MetaObjectHandler() {
//            @Override
//            public void insertFill(MetaObject metaObject) {
//
//            }
//
//            @Override
//            public void updateFill(MetaObject metaObject) {
//
//            }
//        });
//        return config;
//    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor();
        pagination.setDbType(DbType.MYSQL);
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }



}
