package com.vdlm.spider.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vdlm.common.protocol.GsonObjectConverter;
import com.vdlm.common.protocol.ObjectConverter;
import com.vdlm.spider.task.ItemTaskStrategy;

/**
 *
 * @author: chenxi
 */

@Configuration
@ImportResource("classpath:META-INF/applicationContext-dao.xml")
public class SpiderDaoConfig {

	@Bean
	ItemTaskStrategy itemTaskStrategy() {
		return new ItemTaskStrategy();
	}
	
	@Bean
	ObjectConverter objectConverter() {
		return new GsonObjectConverter();
	}
}
