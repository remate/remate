

package com.vdlm.config;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.vdlm.biz.config.BizConfig;
import com.vdlm.biz.filter.UserServletFilter;
import com.vdlm.filter.UnionFilter;
import com.vdlm.filter.UniqueNoFilter;
import com.vdlm.filter.XiangquFilter;
import com.vdlm.thirds.config.ThirdConfig;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { ApplicationConfig.class, DalConfig.class, ServiceConfig.class, BizConfig.class,
				SecurityConfig.class, ThirdConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebMvcConfig.class };
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		
		DelegatingFilterProxy securityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");

		UserServletFilter userServletFilter = new UserServletFilter();

		XiangquFilter xiangquFilter = new XiangquFilter();
		
		UniqueNoFilter uidFilter = new UniqueNoFilter();

		UnionFilter unionFilter = new UnionFilter();
		
		ShallowEtagHeaderFilter etagFilter = new ShallowEtagHeaderFilter();


		return new Filter[] {characterEncodingFilter, securityFilterChain, userServletFilter, xiangquFilter, uidFilter, unionFilter, etagFilter};
	}

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setInitParameter("defaultHtmlEscape", "true");
	}

	static class ConfigApplicationContextInitializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		private Logger LOG = LoggerFactory.getLogger(ConfigApplicationContextInitializer.class);

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
//			ConfigurableEnvironment env = applicationContext.getEnvironment();
//			try {
//				MutablePropertySources propertySources = env.getPropertySources();
//				propertySources.addFirst(new ResourcePropertySource("classpath:filtered.properties"));
//				LOG.info("filtered.properties loaded: {}", Arrays.toString(env.getActiveProfiles()));
//			} catch (IOException e) {
//				// it's ok if the file is not there. we will just log that info.
//				LOG.info("didn't find filtered.properties in classpath so not loading it in the AppContextInitialized");
//			}
		}
	}
}

