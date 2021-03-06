package com.vdlm.web.error;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;

import com.alibaba.fastjson.JSON;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.error.XqProductBuyException;

/**
 * General error handler for the application.
 */
@ControllerAdvice
class ExceptionHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Handle exceptions thrown by handlers.
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
	public ModelAndView exception(Exception e, HttpServletRequest req) {
		BizException be = getBizException(e);
		if (be != null){
			log.info(req.getRequestURI() + " business error [" + e.getMessage()
					+ "] Params:" + JSON.toJSONString(req.getParameterMap()), e);	
		} else {
			log.error(req.getRequestURI() + " system error [" + e.getMessage()
					+ "] Params:" + JSON.toJSONString(req.getParameterMap()), e);	
		}

		ModelAndView mav = new ModelAndView("error/general");
		
		// 想去商品购买出错提示页面
		if (e instanceof XqProductBuyException) {
		    mav = new ModelAndView("catalog/rusherror");
		}
		
		// 非业务异常，保存到数据库
		if (!(e instanceof BizException)) {
		    // TODO 写入到数据库
		}
		
		
		GlobalErrorCode ec;
		String moreInfo;
		List<Object> args = new ArrayList<Object>();
		
		RequestContext reqCtx = new RequestContext(req);
		if (e instanceof MissingServletRequestParameterException) {
			ec = GlobalErrorCode.INVALID_ARGUMENT;
			Object[] params = new Object[1];
			params[0] = ((MissingServletRequestParameterException) e).getParameterName();
			moreInfo = reqCtx.getMessage("valid.notBlank.param", params);
		} else if (be == null) {
			ec = GlobalErrorCode.UNKNOWN;
			moreInfo = e.getClass().getSimpleName() + ": " + e.getMessage();
		} else {
			ec = be.getErrorCode();
			moreInfo = be.getMessage();
		}

		mav.addObject("title", reqCtx.getMessage("page.error." + ec.getErrorCode() + ".title"));
		args.add(moreInfo);

		if (e instanceof XqProductBuyException) {
		    mav.addObject("message", e.getMessage());
        } else {
            mav.addObject("message", reqCtx.getMessage("page.error." + ec.getErrorCode() + ".message", args, ec.toString()));
        }
        
		return mav;
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