package com.quickpaas.framework.quickql.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
public class QuickqlProperty {

    @Value("${quickql.config.autoQueryAllFields:false}")
    private boolean autoQueryAllFields;
}
