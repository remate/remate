/**
 * 
 */
package com.vdlm.spider.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.biz.qiniu.Qiniu;
import com.vdlm.biz.vo.UpLoadFileVO;
import com.vdlm.dal.type.FileBelong;
import com.vdlm.service.error.GlobalErrorCode;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.utils.CollectionTools;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:43:13 PM Jul 28, 2014
 */
@Controller
@RequestMapping("/qiniu")
public class QiniuController {

	@Autowired
	Qiniu qiniu;

	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/defaultImg")
	public ResponseObject<String> defaultImg() throws Exception {

		final URL url = QiniuController.class.getResource("/defaultImg.png");
		if (url == null) {
			return new ResponseObject<String>(GlobalErrorCode.NOT_FOUND);
		}

		final byte[] bytes = IOUtils.toByteArray(url);

		InputStream input = null;

		try {
			input = new ByteArrayInputStream(bytes);

			final List<UpLoadFileVO> results = qiniu.uploadImgStream(CollectionTools.asArrayList(input), FileBelong.PRODUCT);

			if (CollectionUtils.isNotEmpty(results)) {
				return new ResponseObject<String>(results.get(0).getId());
			}

		}
		finally {
			IOUtils.closeQuietly(input);
		}

		return new ResponseObject<String>();
	}

}
