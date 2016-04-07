package com.vdlm.bos.file;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;

@Controller
public class ImageController {
	
	@Autowired
	private ResourceFacade resourceFacade;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ImageUploadValidator());
	}
	
	@RequestMapping("imgUpload")
	public String imgUpload(Model model){
		model.addAttribute("belong", FileBelong.OTHER);
		return "fileupload/imgUpload";
	}
	
	@ResponseBody //前台：th:action="'_f/u?'+${_csrf.parameterName}+'='+${_csrf.token}"
	@RequestMapping(value = ResourceFacade.RES_PREFIX_UP_FILE + "/u", method = RequestMethod.POST)
	public List<UpLoadFileVO> upload(@Valid @ModelAttribute ImageUploadForm form, Errors errors, HttpServletRequest request)
			throws Exception {
		if(form.getBelong() == null){
			RequestContext requestContext = new RequestContext(request);
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.fileBelong.message"));
		}
		ControllerHelper.checkException(errors);
		return resourceFacade.uploadFile(form.getFile(), form.getBelong());
	}
}
