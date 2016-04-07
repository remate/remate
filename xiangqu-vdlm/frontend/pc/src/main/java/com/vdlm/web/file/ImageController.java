package com.vdlm.web.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.vdlm.biz.res.ResourceFacade;
import com.vdlm.service.file.ImageService;

@Controller
public class ImageController {

	@Autowired
	private ImageService imageService;

	@Autowired
	private ResourceFacade resourceFacade;
	
	/*
	@RequestMapping(ResourceFacade.RES_PREFIX_UP_FILE + "/{id}")
	public void view(@PathVariable String id, HttpServletResponse response) throws IOException {
		byte[] bFile = null;//resourceFacade.findImage(id);
		OutputStream stream = response.getOutputStream();
		stream.write(bFile);
		stream.flush();
		stream.close();
	}
	*/
}
