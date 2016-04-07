package com.vdlm.restapi.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.type.LogisticsCompany;
import com.vdlm.dal.vo.LogisticsVO;
import com.vdlm.restapi.ResponseObject;

@Controller
public class LogisticsController {

	@ResponseBody
	@RequestMapping("/logistics/list")
	public ResponseObject<List<LogisticsVO>> list() {
		List<LogisticsVO> list = new ArrayList<LogisticsVO>();

		LogisticsVO vo = null;
		LogisticsCompany[] logistics = LogisticsCompany.values();
		for (LogisticsCompany obj : logistics) {
			vo = new LogisticsVO();
			vo.setKey(obj.name());
			vo.setName(obj.toString());
			list.add(vo);
		}

		return new ResponseObject<List<LogisticsVO>>(list);
	}
}
