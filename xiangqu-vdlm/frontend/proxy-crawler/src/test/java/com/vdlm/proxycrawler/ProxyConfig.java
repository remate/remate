package com.vdlm.proxycrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.common.bus.DefaultBusRegistry;
import com.vdlm.proxycrawler.third.XiciHomeProxyCrawler;

/**
 *
 * @author: chenxi
 */

@Configuration
@ImportResource("classpath:META-INF/applicationContext-proxy.xml")
public class ProxyConfig {

	@Bean
	BusSignalManager busSignalManager() {
		return new BusSignalManager(new DefaultBusRegistry());
	}
	
	@Bean
	XiciHomeProxyCrawler xiciHomeProxy() throws Exception {
		return new XiciHomeProxyCrawler();
	}
	
//	@Bean
//	XiciNnProxyCrawler proxyCrawler() throws Exception {
//		return new XiciNnProxyCrawler();
//	}
//	
//	@Bean
//	Daili5566ProxyCrawler daili5566ProxyCrawler() throws Exception {
//		return new Daili5566ProxyCrawler();
//	}
	
	@Autowired
	@Bean
	ProxyValidator proxyValidator(BusSignalManager bsm) {
		return new ProxyValidator(bsm);
	}
}
