package com.quickpaas.framework.configuration;

import com.quickpaas.framework.cache.ClassCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.quickpaas.framework.service.impl")
public class QuickFrameworkAutoConfigure {

    @Bean
    public ClassCache classCache() {
        return new ClassCache();
    }
}
