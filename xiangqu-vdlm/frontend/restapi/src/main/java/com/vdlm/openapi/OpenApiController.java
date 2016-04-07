package com.vdlm.openapi;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vdlm.openapi.validator.OpenApiBaseValidator;
import com.vdlm.restapi.BaseController;

/**
 * all the logics are put here for the current, will be moved into individual package later
 * @author tonghu
 */
@RestController
@RequestMapping(value="/openapi", method = {RequestMethod.GET, RequestMethod.POST})
public class OpenApiController extends BaseController {
    // @RequestHeader("Domain") String domain, @RequestHeader("Token") String token
	
	@InitBinder("form")
	protected void initBinderExtuid(WebDataBinder binder) {
		binder.setValidator(new OpenApiBaseValidator());
	}
}
