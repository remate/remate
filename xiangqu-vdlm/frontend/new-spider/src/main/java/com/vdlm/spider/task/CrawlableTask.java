package com.vdlm.spider.task;

import java.util.Map;

import org.htmlparser.NodeFilter;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.entity.HttpRequestError;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.http.error.StatusCodeErrorHandler;
import com.vdlm.spider.http.error.StatusCodeErrorHandlerFactory;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public abstract class CrawlableTask<T extends ItemListTaskBean> implements Retriable {
	
	protected final Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected final BusSignalManager bsm;
	protected final T bean;
	protected final HttpClientProvider provider;
	protected final StatusCodeErrorHandlerFactory scehf;
	
	protected int maxRetryTimes = 20;
	protected boolean retry = false;
	
	public CrawlableTask(BusSignalManager bsm, 
						   T bean, 
						   HttpClientProvider provider, 
						   StatusCodeErrorHandlerFactory scehf,
						   int maxRetryTimes) {
		this.bsm = bsm;
		this.bean = bean;
		this.provider = provider;
		this.scehf = scehf;
		this.maxRetryTimes = maxRetryTimes;
	}
	
	@Override
	public void retry() {
		final int retryTimes = bean.retryTimes();
		if (retryTimes >= maxRetryTimes) {
			Logs.RETRY.warn("crawl {} task has already retried {} times", bean, maxRetryTimes);
			maxRetries();
			return;
		}
		
		if (!needRetry()) {
			Logs.RETRY.warn("{} task does not need retry any more due to failure.", bean);
			return;
		}
		
		bsm.signal(bean);
		Logs.RETRY.info("retry to crawl {} task {} time later", bean, retryTimes);
	}

	public byte[] data() {
		return bean.toJSONBytes();
	}
	
	public void crawl() {
		final long start = System.currentTimeMillis();
		Logs.TASK.info("start to crawl task {} ...", bean);
		final HttpClientInvoker invoker = getHttpClient();
		if (!invoker.isUsable()) {
			handleUnusableInvoker();
			retry();
			return;
		}
		
		final HttpInvokeResult result = invoker.invoke();
		parseHttpResult(invoker, result);
		Logs.TASK.info("end crawl task {} , cost {} ms", bean, System.currentTimeMillis() - start);
	}
	
	protected void parseHttpResult(HttpClientInvoker invoker, HttpInvokeResult result) {
		if (result.isOK()) {
			if (isAuthHtml(result.getContentString())) {
				Logs.RETRY.info("200 return but is an auth html");
				retry();
				return;
			}
			final ParsableTask<?> task = createParsableTask(invoker, result);
			bsm.signal(task);
			Logs.TASK.info("created a parsable task {}", bean);
		} else {
			// save error status code
			final HttpRequestError error = new HttpRequestError();
			error.setUrl(bean.getRequestUrl());
			error.setStatusCode(result.getStatusCode());
			LOG.info("got an error http request {}", error);
			bsm.signal(error);
			// error handling
			final StatusCodeErrorHandler errorHandler = scehf.getErrorHandler(result.getStatusCode());
			errorHandler.errorHandling(provider, invoker, result, bean);
			if (errorHandler.toContinue()) {
				final ParsableTask<?> task = createParsableTask(invoker, result);
				bsm.signal(task);
				Logs.TASK.info("created a parsable task {}", bean);
			}
			else if (errorHandler.needRetry()) {
				retry();
				return;
			}
		}
	}
	
	protected boolean isAuthHtml(String htmlContent) {
		final org.htmlparser.Parser parser = HtmlParserProvider.createParser(htmlContent);
		try {
			final Map<String, ParserConfig> configs = ParserConfigProviders.getAuthConfigs(bean);
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);

			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			return (nodeList != null && nodeList.size() > 0);
		}
		catch (final Exception ignore) {
		}
		return false;
	}
	
	public T getBean() {
		return bean;
	}

	protected HttpClientInvoker getHttpClient() {
		return this.provider.provide(this.bean.getShopType(),
				this.bean.getRequestUrl(), this.bean.getRefererUrl());
	}
	
	protected abstract void handleUnusableInvoker();
	protected abstract ParsableTask<?> createParsableTask(HttpClientInvoker invoker, HttpInvokeResult result);
	protected void maxRetries() {
		// to be override
	}

	protected boolean needRetry() {
		return true;
	}
}
