package com.vdlm.web.error;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.service.error.BizException;
import com.vdlm.utils.CommonUtils;

@Controller
class CustomErrorController {
	
	@Autowired
	private MessageSource messageSource;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Display an error page, as defined in web.xml <code>custom-error</code> element.
	 */
	@RequestMapping("generalError")
	public String generalError(HttpServletRequest request, HttpServletResponse response, Model model) {
		// retrieve some useful information from the request
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		final Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		// String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		
		statusCode = getExceptionStatus(throwable, statusCode);
		final String exceptionMessage = getExceptionMessage(throwable, statusCode);
		
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		if (requestUri == null) {
			requestUri = "Unknown";
		}
		
		final String message = MessageFormat.format("{0} returned for {1} with message {2}",
			statusCode, requestUri, exceptionMessage
		);
		
		log.error(request.getRequestURI()+ " general error : " + message);
		
		// FIXME 错误消息国际化
		response.setStatus(statusCode);
		model.addAttribute("statusCode", statusCode);
		
		final RequestContext requestContext = new RequestContext(request);
		model.addAttribute("title", requestContext.getMessage("page.error." + statusCode + ".title"));
		model.addAttribute("message", requestContext.getMessage("page.error." + statusCode + ".message", exceptionMessage));
		
        return "error/general";
	}

	private String getExceptionMessage(Throwable throwable, Integer statusCode) {
		if (throwable != null) {
			return CommonUtils.getRootCause(throwable).getMessage();
		}
		final HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
		return httpStatus.getReasonPhrase();
	}
	
	private Integer getExceptionStatus(Throwable throwable, Integer statusCode) {
		if (throwable != null) {
			if (CommonUtils.getRootCause(throwable) instanceof BizException) {
				final BizException bizExp = (BizException)CommonUtils.getRootCause(throwable);
				return bizExp.getErrorCode().getErrorCode();
			}
		}
		return statusCode;
	}
}
