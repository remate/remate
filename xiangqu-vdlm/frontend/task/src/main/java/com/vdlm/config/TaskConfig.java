package com.vdlm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.vdlm.task.Scanned;


@Configuration
@Import(ApplicationConfig.class)
@ImportResource("classpath:META-INF/applicationContext-task.xml")
@ComponentScan(basePackageClasses = Scanned.class)
class TaskConfig extends WebMvcConfigurationSupport {
}