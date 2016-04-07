package com.vdlm.config;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.vdlm.biz.config.BizConfig;
import com.vdlm.biz.filter.UserServletFilter;
import com.vdlm.filter.DomainFilter;
import com.vdlm.filter.RunAsFilter;
import com.vdlm.filter.TraceFilter;

public class WebAppInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {
    
    public static final String API_VERSION = "v2";

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { ApplicationConfig.class, DalConfig.class,
				ServiceConfig.class, BizConfig.class, SecurityConfig.class };
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

		DelegatingFilterProxy securityFilterChain = new DelegatingFilterProxy(
				"springSecurityFilterChain");
		
		UserServletFilter userServletFilter = new UserServletFilter();
		
		ShallowEtagHeaderFilter etagFilter = new ShallowEtagHeaderFilter();

		TraceFilter traceFilter = new TraceFilter();

		RunAsFilter runAsFilter = new RunAsFilter();
		
		DomainFilter domainFilter = new DomainFilter();
		
		return new Filter[] {characterEncodingFilter, securityFilterChain, userServletFilter, etagFilter, traceFilter, runAsFilter, domainFilter};
	}

	@Override
	protected void customizeRegistration(
			ServletRegistration.Dynamic registration) {
		registration.setInitParameter("defaultHtmlEscape", "true");
	}

	static class ConfigApplicationContextInitializer implements
			ApplicationContextInitializer<ConfigurableApplicationContext> {

		private Logger LOG = LoggerFactory
				.getLogger(ConfigApplicationContextInitializer.class);

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