

package com.vdlm.web.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.mybatis.IdTypeHandler;

/**
 * @author ahlon
 */
@Controller
public class ToolController {
	
	@ResponseBody
	@RequestMapping("/tool/encode/{id}")
	public String encode(@PathVariable String id) {
		return IdTypeHandler.encode(Long.parseLong(id));
	}
	
	@ResponseBody
	@RequestMapping("/tool/decode/{code}")
	public String decode(@PathVariable String code) {
		return String.valueOf(IdTypeHandler.decode(code));
	}
}

