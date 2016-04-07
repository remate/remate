package com.vdlm.spider.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.dao.HttpRequestErrorDao;
import com.vdlm.spider.entity.HttpRequestError;

/**
 *
 * @author: chenxi
 */

public class HttpRequestErrorStore implements BusSignalListener<HttpRequestError> {

	private final static Logger LOG = LoggerFactory.getLogger(HttpRequestErrorStore.class);
	
	@Autowired
	private HttpRequestErrorDao httpRequestErrorDao;
	
	public HttpRequestErrorStore(BusSignalManager bsm) {
		bsm.bind(HttpRequestError.class, this);
	}
	
	@Override
	public void signalFired(HttpRequestError signal) {
		try {
			httpRequestErrorDao.insert(signal);
		} catch (final Throwable t) {
			LOG.error("failed to insert http request error to db", t);
		}
	}

}
