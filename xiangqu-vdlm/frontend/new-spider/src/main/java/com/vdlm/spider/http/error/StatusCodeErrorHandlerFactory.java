package com.vdlm.spider.http.error;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

/**
 *
 * @author: chenxi
 */

@Component
public class StatusCodeErrorHandlerFactory {

	private final Map<Integer, StatusCodeErrorHandler> handlers = Maps.newHashMap();
	
	private final StatusCodeErrorHandler denyError = new CBegin400ErrorHandler();
	private final StatusCodeErrorHandler redirectError = new CBegin300ErrorHandler();
	private final StatusCodeErrorHandler unknownError = new UnknownErrorHandler();
	
	@PostConstruct
	public void init() {
		handlers.put(301, redirectError);
		handlers.put(302, redirectError);
		handlers.put(303, redirectError);
		handlers.put(307, redirectError);
		handlers.put(400, denyError);
		handlers.put(401, denyError);
		handlers.put(402, denyError);
		handlers.put(403, denyError);
		handlers.put(404, new C404ErrorHandler());
		handlers.put(405, denyError);
	}
	
	public StatusCodeErrorHandler getErrorHandler(int statusCode) {
		final StatusCodeErrorHandler handler = handlers.get(statusCode);
		if (handler == null) {
			return unknownError;
		}
		return handler;
	}
}
