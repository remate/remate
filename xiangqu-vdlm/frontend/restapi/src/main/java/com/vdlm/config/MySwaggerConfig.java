//package com.vdlm.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.validation.Errors;
//
//import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
//import com.mangofactory.swagger.plugin.EnableSwagger;
//import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
//import com.wordnik.swagger.model.ApiInfo;
//
//@Configuration
//@EnableSwagger
//@Profile({ "dev", "test" })
//class MySwaggerConfig {
//    private SpringSwaggerConfig springSwaggerConfig;
//    
//    /**
//     * Required to autowire SpringSwaggerConfig
//     */
//    @Autowired
//    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
//        this.springSwaggerConfig = springSwaggerConfig;
//    }
//
//    /**
//     * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc
//     * framework - allowing for multiple swagger groups i.e. same code base
//     * multiple swagger resource listings.
//     */
//    @Bean
//    public SwaggerSpringMvcPlugin customImplementation() {
//        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
//                .ignoredParameterTypes(Errors.class)
//                .apiVersion("v2")
//                .apiInfo(
//                        new ApiInfo("vdlm 第二版", "vdlm rest api 第二版", "tos",
//                                "backend@ixiaopu.com",
//                                "donnot use w/o written permission", "private"));
//    }
//    
//}