package com.vdlm.bos.file;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import com.vdlm.dal.type.FileBelong;

public class ImageUploadValidator implements Validator {

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
			errors.rejectValue("file", "valid.file.notBlank.message", "文件不能为空");
		}
		if (isValid) {
			for (MultipartFile file : bean.getFile()) {
				if (file.getSize() > maxSize) {
					errors.rejectValue("file", "valid.file.size.limit.message", "文件大小超过限制，最大4M");
					break;
				}
				if ( (file.getOriginalFilename()+"").length() > 100 )  {
					errors.rejectValue("file", "valid.file.name.limit.message", "文件名字不能大于100个字");
					break;
				}
				if (bean.getBelong() != FileBelong.LOG && !file.getContentType().startsWith("image/")) {
					errors.rejectValue("file", "valid.file.type.message", "文件类型错误，不是图片类型的文件");
					break;
				}
			}
		}
	}
}
