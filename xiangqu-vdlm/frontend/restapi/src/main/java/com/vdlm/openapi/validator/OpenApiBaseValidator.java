package com.vdlm.openapi.validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.vdlm.dal.type.UserPartnerType;
import com.vdlm.openapi.baseForm.OpenApiBaseForm;

public class OpenApiBaseValidator implements Validator {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean supports(Class<?> clazz) {
		return OpenApiBaseForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		OpenApiBaseForm form = (OpenApiBaseForm) target;
		
		if(StringUtils.isBlank(form.getExtUid())){
			log.warn("partner access open api without ext_uid");
		}else{
			log.debug("partner access open api, ext_uid="+form.getExtUid());
		}
		
		if(StringUtils.isBlank(form.getDomain())){
			errors.rejectValue("domain", "partner channel is blank");
		}else{
			try {
				UserPartnerType channel = UserPartnerType.valueOf(form.getDomain());
				log.debug("partner access open api, channel:"+channel);
			} catch (Exception e) {
				errors.rejectValue("domain", "partner channel is not support! domain="+form.getDomain());
			}
		}
	}
}
