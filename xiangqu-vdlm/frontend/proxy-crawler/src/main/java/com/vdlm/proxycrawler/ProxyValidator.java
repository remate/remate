package com.vdlm.proxycrawler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.proxycrawler.dao.ProxyDao;
import com.vdlm.proxycrawler.http.HttpClientInvoker;
import com.vdlm.proxycrawler.http.HttpClientProvider;
import com.vdlm.proxycrawler.http.HttpInvokeResult;
import com.vdlm.proxycrawler.utils.ShopType;

/**
 *
 * @author: chenxi
 */

public class ProxyValidator implements BusSignalListener<Proxy> {

	private final static String TAOBAO_SHOP_TEST_URL = "http://shop110591102.taobao.com";
	private final static String TMALL_SHOP_TEST_URL = "http://yuezhitiansenz.tmall.com/";
	
	private final static Logger LOG = LoggerFactory.getLogger(ProxyValidator.class);
	
	@Autowired
	private HttpClientProvider httpClientProvider;
	@Autowired
	private ProxyDao proxyDao;
	
	public ProxyValidator(BusSignalManager bsm) {
		bsm.bind(Proxy.class, this);
	}
	
	@Override
	public void signalFired(Proxy signal) {
		ProxyStatus status = ProxyStatus.DISABLED;
		if (validateTaobao(signal)) {
			status = ProxyStatus.TAOBAO_ACCEPT;
			if (validateTmall(signal)) {
				status = ProxyStatus.TMALL_ACCEPT;
			}
		}
		if (ProxyStatus.DISABLED.equals(status)) {
			LOG.info("ignore proxy {}", signal);
		} else {
			signal.setStatus(status);
			try {
				if (proxyDao.exist(signal) == null) {
					proxyDao.insert(signal);
					LOG.info("saved proxy {}", signal);
				}
				else {
					proxyDao.update(signal);
				}
			} catch (final DataIntegrityViolationException dive) {
				signal.setCheckTime2Current(true);
				signal.setCheckTime(new Date());
				if (proxyDao.exist(signal) == null) {
					proxyDao.insert(signal);
					LOG.info("saved proxy {}", signal);
				}
				else {
					try {
						proxyDao.update(signal);
					} catch (final Exception e) {
						LOG.warn("failed to update proxy " + signal, e);
					}
				}
			}
			catch (final Throwable t) {
				LOG.warn("failed to save proxy " + signal, t);
			}
		}
	}

	private boolean validateTaobao(Proxy proxy) {
		final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TAOBAO, TAOBAO_SHOP_TEST_URL, 
				null, proxy.getIp() + ":" + proxy.getPort());
		final HttpInvokeResult result = invoker.invoke();
		if (result.isOK()) {
			return true;
		}
		return false;
	}
	
	private boolean validateTmall(Proxy proxy) {
		final HttpClientInvoker invoker = httpClientProvider.provide(ShopType.TMALL, TMALL_SHOP_TEST_URL, 
				null, proxy.getIp() + ":" + proxy.getPort());
		final HttpInvokeResult result = invoker.invoke();
		if (result.isOK()) {
			return true;
		}
		return false;
	}
}
