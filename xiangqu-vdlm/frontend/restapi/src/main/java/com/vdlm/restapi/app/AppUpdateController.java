package com.vdlm.restapi.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vdlm.dal.model.AppVersion;
import com.vdlm.dal.type.OSType;
import com.vdlm.restapi.BaseController;
import com.vdlm.restapi.ResponseObject;
import com.vdlm.service.app.AppVersionService;

@Controller
public class AppUpdateController extends BaseController {

	@Autowired
	private AppVersionService appVersionService;

	/**
	 * @return 若需要升级，则返回最新版本信息
	 */
	@ResponseBody
	@RequestMapping("/update/check")
	public ResponseObject<AppVersion> checkVersion(@Param("clientVersion") int clientVersion, OSType osType) {
		AppVersion curVer = appVersionService.findCurrentVersion(clientVersion, osType);
		return new ResponseObject<AppVersion>(curVer != null && curVer.getClientVersion() > clientVersion ? curVer : null);
	}
}
