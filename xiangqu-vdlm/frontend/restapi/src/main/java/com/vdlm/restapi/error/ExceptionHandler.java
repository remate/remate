package com.vdlm.restapi.error;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.alibaba.fastjson.JSON;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;

/**
 * General error handler for the application.
 */
@ControllerAdvice
class ExceptionHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Handle exceptions thrown by handlers.
	 */
	@ResponseBody
	@org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
	public ResponseObject<?> exception(HttpServletRequest req, Exception e) {
		BizException be = getBizException(e);
		if (be != null){
			log.info(req.getRequestURI() + " business error [" + e.getMessage()
					+ "] Params:" + JSON.toJSONString(req.getParameterMap()), e);	
		} else {
			log.error(req.getRequestURI() + " system error [" + e.getMessage()
					+ "] Params:" + JSON.toJSONString(req.getParameterMap()), e);	
		}

		GlobalErrorCode ec;
		String moreInfo;
		if (e instanceof MissingServletRequestParameterException) {
			ec = GlobalErrorCode.INVALID_ARGUMENT;
			RequestContext requestContext = new RequestContext(req);
			Object[] params = new Object[1];
			params[0] = ((MissingServletRequestParameterException) e).getParameterName();
			moreInfo = requestContext.getMessage("valid.notBlank.param", params);
		} else if (e instanceof SQLException) {
			ec = GlobalErrorCode.INTERNAL_ERROR;
			moreInfo = e.getClass().getName();
		} else if (be == null) {
			ec = GlobalErrorCode.UNKNOWN;
			moreInfo = e.getClass().getName();
		} else {
			ec = be.getErrorCode();
			moreInfo = be.getMessage();
		}
		/*else if (e instanceof UsernameNotFoundException) {
			ec = GlobalErrorCode.NOT_FOUND;
			RequestContext requestContext = new RequestContext(req);
			moreInfo = requestContext.getMessage("valid.user.notExist.message");
		}*/
		return new ResponseObject<Object>(moreInfo, ec);
	}

	private BizException getBizException(Throwable e1) {
		Throwable e2 = e1;
		do {
			if (e2 instanceof BizException)
				return (BizException) e2;
			e1 = e2;
			e2 = e1.getCause();
		} while (e2 != null && e2 != e1);

		return null;
	}
}