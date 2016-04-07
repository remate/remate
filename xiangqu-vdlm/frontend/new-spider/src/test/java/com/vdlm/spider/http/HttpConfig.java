package com.vdlm.spider.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vdlm.spider.dao.ProxyDao;

/**
 *
 * @author: chenxi
 */

@Configuration
@ImportResource("classpath:META-INF/applicationContext-http.xml")
public class HttpConfig {
	
	@Bean
	ProxyDao proxyDao() {
		return new ProxyDao();	
	}

}
