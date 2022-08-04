package com.quickpaas.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2SpringBoot 配置文件
 * 详情参考，http://springfox.github.io/springfox/docs/current/#springfox-samples
 */
//@Configuration
//@EnableSwagger2
public class SwaggerConfig {


    @Bean
    Docket uiApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.quickpaas.shop")).build();

    }

    private ApiInfo uiApiInfo() {
        return new ApiInfo("Quick Framework", "", "1.0", null, "", null, null);
    }

    /**
     * 全局请求头参数
     */
    private List<Parameter> globalOperationParameters() {
        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder().name("X-AUTH-TOKEN").description("请求令牌").parameterType("header").modelRef(new ModelRef("string")).required(false).build());
        parameters.add(new ParameterBuilder().name("X-AUTH-KEY").description("请求令牌").parameterType("header").modelRef(new ModelRef("string")).required(false).build());
        parameters.add(new ParameterBuilder().name("X-PROJECT-ID").description("请求令牌").parameterType("header").modelRef(new ModelRef("string")).required(false).build());
        parameters.add(new ParameterBuilder().name("X-AUTH-SOURCE").description("请求令牌").parameterType("header").modelRef(new ModelRef("string")).required(false).build());
        return parameters;
    }

    /**
     * 请求返回的HTTP全局状态码说明(非业务状态码)
     */
    private List<ResponseMessage> globalResponseMessage() {
        List<ResponseMessage> messageList = new ArrayList<>();
        return messageList;
    }


    @Bean
    UiConfiguration uiConfig() {
        //validatorUrl, docExpansion, apiSorter, defaultModelRendering, String[] supportedSubmitMethods, enableJsonEditor, showRequestHeaders
        return new UiConfiguration(null, "none", "alpha", "model", UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, false);
    }

}
