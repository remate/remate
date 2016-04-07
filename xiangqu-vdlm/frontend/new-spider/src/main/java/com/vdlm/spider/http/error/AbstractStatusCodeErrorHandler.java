package com.vdlm.spider.http.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author: chenxi
 */

public abstract class AbstractStatusCodeErrorHandler implements StatusCodeErrorHandler {

	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private final boolean needRetry;
	
	private boolean toContinue = false;
	
	public AbstractStatusCodeErrorHandler(boolean needRetry) {
		this.needRetry = needRetry;
	}

	@Override
	public boolean needRetry() {
		return needRetry;
	}

	@Override
	public boolean toContinue() {
		return toContinue;
	}

	public void setToContinue(boolean toContinue) {
		this.toContinue = toContinue;
	}

}
