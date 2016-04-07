package com.vdlm.restapi.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

public class ImageUploadValidator implements Validator {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Override
	public boolean supports(Class<?> clazz) {
		return ImageUploadForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		long maxSize = 1024 * 1024 * 4;

		ImageUploadForm bean = (ImageUploadForm) target;
		boolean isValid = true;
		if (bean.getFile() == null || bean.getFile().size() == 0) {
			isValid = false;
			log.warn( "upload warning 文件不能为空  belong=[" + bean.getBelong() + "]");
			errors.rejectValue("file", "valid.file.notBlank.message", "文件不能为空");
		}
		if (isValid) {
			for (MultipartFile file : bean.getFile()) {
				if (file.getSize() > maxSize) {
					log.warn( "upload warning 文件大小超过限制，file size=[" + file.getSize() + "]");
					errors.rejectValue("file", "valid.file.size.limit.message", "文件大小超过限制，最大4M");
					break;
				}
				if ( (file.getOriginalFilename()+"").length() > 100 )  {
					log.warn( "upload warning 文件名字不能大于100个字，length=[" + (file.getOriginalFilename()+"").length() + "]");
					errors.rejectValue("file", "valid.file.name.limit.message", "文件名字不能大于100个字");
					break;
				}
				/*
				if (bean.getBelong() != FileBelong.LOG && !file.getContentType().startsWith("image/")) {
					log.warn( "upload warning 文件类型错误 [" + file.getContentType() + "] file length=["+bean.getFile().size()+"] belong=[" + bean.getBelong() + "]");
					errors.rejectValue("file", "valid.file.type.message", "文件类型错误");
					break;
				}
				*/
				
			}
		}
	}
}
