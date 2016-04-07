package com.vdlm.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.vdlm.biz.config.BizConfig;

public class TaskInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { ApplicationConfig.class, DalConfig.class, ServiceConfig.class, BizConfig.class};
	}

	static class ConfigApplicationContextInitializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		private Logger LOG = LoggerFactory.getLogger(ConfigApplicationContextInitializer.class);

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			ConfigurableEnvironment environment = applicationContext.getEnvironment();
			try {
				environment.getPropertySources().addFirst(new ResourcePropertySource("classpath:filtered.properties"));
				LOG.info("filtered.properties loaded");
			} catch (IOException e) {
				// it's ok if the file is not there. we will just log that info.
				LOG.info("didn't find filtered.properties in classpath so not loading it in the AppContextInitialized");
			}
		}
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { TaskConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
}