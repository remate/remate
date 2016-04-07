package com.vdlm.proxycrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.common.bus.DefaultBusRegistry;
import com.vdlm.proxycrawler.dao.ProxyDao;
import com.vdlm.proxycrawler.third.Daili5566ProxyCrawler;
import com.vdlm.proxycrawler.third.XiciHomeProxyCrawler;
import com.vdlm.proxycrawler.third.XiciNnProxyCrawler;

/**
 *
 * @author: chenxi
 */

@Configuration
@ImportResource("classpath:META-INF/applicationContext-proxycrawler.xml")
public class ProxyCrawlerConfig {

	@Bean
	BusSignalManager busSignalManager() {
		return new BusSignalManager(new DefaultBusRegistry());
	}
	
	@Bean
	ProxyDao proxyDao() {
		return new ProxyDao();
	}
	
	@Bean
	XiciHomeProxyCrawler xiciHomeProxy() throws Exception {
		return new XiciHomeProxyCrawler();
	}
	
	@Bean
	XiciNnProxyCrawler xiciNnProxyCrawler() throws Exception {
		return new XiciNnProxyCrawler();
	}
	
	@Bean
	Daili5566ProxyCrawler daili5566ProxyCrawler() throws Exception {
		return new Daili5566ProxyCrawler();
	}
	
	@Autowired
	@Bean
	ProxyValidator proxyValidator(BusSignalManager bsm) {
		return new ProxyValidator(bsm);
	}
}
