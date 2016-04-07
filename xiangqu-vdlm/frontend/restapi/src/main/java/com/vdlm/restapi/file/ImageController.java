package com.vdlm.restapi.file;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.RequestContext;

import com.vdlm.biz.controller.ControllerHelper;
import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.error.BizException;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.service.file.ImageService;

@Controller
public class ImageController {
    
	@Autowired
	private ImageService imageService;
	
	@Autowired
	private ResourceFacade resourceFacade;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new ImageUploadValidator());
	}
	
	/**
	 * 上传富文本图片，返回的结果是error message url 
	 * <br>/_f/u-desc
	 * @throws IOException 
	 * @throws Exception 
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = ResourceFacade.RES_PREFIX_UP_FILE + "/u_desc", method = RequestMethod.POST)
	public JSONObject uploadDesc(ImageUploadForm form,MultipartHttpServletRequest request) throws Exception {
		JSONObject obj = new JSONObject();
		
		List<MultipartFile> fileList = request.getFiles("imgFile");
		List<UpLoadFileVO> result = resourceFacade.uploadFile(fileList, FileBelong.PRODUCT);
		
		if(result.isEmpty()){
			obj.put("error",1);
			obj.put("message", "上传图片失败");
		}else{
			obj.put("error", 0);
			obj.put("message", "上传图片成功");
			obj.put("url", result.get(0).getUrl());
		}
		/*
		// 定义图片的格式
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		String dirName = request.getParameter("dir"); // 文件的后缀
		if(!extMap.containsKey(dirName)){
			 throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "图片格式不正确");
		}
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		List<FileItem> items =null;
		try{
			 items = upload.parseRequest(request);
		}catch(Exception e){
			log.debug("获取表单字段异常");
		}
		Iterator itr = items.iterator();
		while (itr.hasNext()) {
			FileItem item = (FileItem) itr.next();
			
			if (item.isFormField()) {
				continue;
			}
			
			String fileName = item.getName();
			//检查扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
				throw new BizException(GlobalErrorCode.INVALID_ARGUMENT, "上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。");
			}
			
			UpLoadFileVO  vo=resourceFacade.uploadFileStream(item.getInputStream(),FileBelong.PRODUCT);
			if(vo!=null){
				obj.put("error", "0");
				obj.put("url", vo.getUrl() );
			}else{
				obj.put("error", "1");
				obj.put("message", "上传图片失败");
			}
			return obj;
		}*/
		
		return obj;
		}
	/**
	 * pc 上传图片/_f/u
	 * @param form
	 * @param errors
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = ResourceFacade.RES_PREFIX_UP_FILE + "/u", method = RequestMethod.POST)
	public ResponseObject<List<UpLoadFileVO>> upload(@Valid @ModelAttribute ImageUploadForm form, Errors errors, HttpServletRequest request)
			throws Exception {
		if(form.getBelong() == null){
			log.warn( "upload warning 文件belong为空  file length=[" + form.getFile().size() + "]");
			RequestContext requestContext = new RequestContext(request);
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.fileBelong.message"));
		}
		ControllerHelper.checkException(errors);
		return new ResponseObject<List<UpLoadFileVO>>(resourceFacade.uploadFile(form.getFile(), form.getBelong()));
	}
	
	@ResponseBody
	@RequestMapping(value = ResourceFacade.RES_PREFIX_UP_FILE + "/getUpToken")
	public ResponseObject<String> uploadToken(FileBelong fileBelong, HttpServletRequest req) throws Exception {
		if (fileBelong == null) {
			RequestContext requestContext = new RequestContext(req);
			throw new BizException(GlobalErrorCode.INVALID_ARGUMENT,
					requestContext.getMessage("valid.fileBelong.message"));
		}
		return new ResponseObject<String>(resourceFacade.genUpTokenForClient(fileBelong));
	}
	
	/**
	 * 上传日志不用检查登录
	 * @param form
	 * @param errors
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = ResourceFacade.RES_PREFIX_UP_FILE + "/u/log", method = RequestMethod.POST)
	public ResponseObject<Boolean> uploadLog(@Valid @ModelAttribute ImageUploadForm form, Errors errors, HttpServletRequest request)
			throws Exception {
		if(form.getBelong() == null){
			RequestContext requestContext = new RequestContext(request);
			throw new BizException(GlobalErrorCode.UNKNOWN, requestContext.getMessage("valid.fileBelong.message"));
		}
		ControllerHelper.checkException(errors);
		return new ResponseObject<Boolean>(resourceFacade.uploadFile(form.getFile(), form.getBelong()).size() > 0);
	}
}
