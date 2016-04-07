package com.vdlm.sms.controller.msg;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vdlm.dal.model.SmsSendRecord;
import com.vdlm.dal.status.SmsSendStatus;
import com.vdlm.dal.util.SpringContextUtil;
import com.vdlm.listener.SmsServerSwitchListener;
import com.vdlm.service.msg.SmsSendRecordService;
import com.vdlm.sms.mandao.MandaoSmsHelper;
import com.vdlm.sms.mandao.WhiteListHelper;

@Component
public class SmsSendHandler {

	Logger logger = LoggerFactory.getLogger(SmsSendHandler.class);

	@Value("${mandao.sms.sn}")
	private String mandaoSmsSn;
	@Value("${mandao.sms.pwd}")
	private String mandaoSmsPwd;
	@Value("${mandao.sms.whitelist}")
	private String whitelist;
	@Value("${mandao.sms.mainserviceurl}")
	private String serviceURL;

	@Autowired
	private SmsSendRecordService smsSendRecordService;

	/**
	 * 发送短信 aop流控,调整参数需要更改aop设置com.vdlm.sms.aop.SmsSendRateAop
	 * @param appId
	 * @param mobile
	 * @param content
	 * @param ipAddress
	 * @param clientIP
	 * @return
	 */
	public boolean doSend(String appId, String mobile, String content, String ipAddress,
			String clientIP) {
		String mandaoBatchId = "";
		if (StringUtils.isEmpty(ipAddress)) {
			logger.error("ipAddress is null, so send fail!");
			return false;
		}
		if (!WhiteListHelper.isLegalRequest(ipAddress, whitelist)) {
			logger.error("ipAddress is not in white list [" + whitelist + "], so send fail!");
			return false;
		}

		if (StringUtils.isEmpty(appId)) {
			logger.error("appid:" + appId + ", so send fail!");
			return false;
		}

		String smsSign = getSignByAppId(appId);
		if (StringUtils.isEmpty(smsSign)) {
			return false;
		}
		content += smsSign;
		try {
			Object bean = SpringContextUtil.getBean("smsServerSwitchListener");
			if (bean instanceof SmsServerSwitchListener) {
				SmsServerSwitchListener listener = (SmsServerSwitchListener) bean;
				serviceURL = listener.getServiceURL();
			}
			//String ext = "";
			String ext = "2";  // 默认用想去的ext
			if ("2".equals(appId)) {
				ext = "1";
			}
			if ("3".equals(appId)) {
				ext = "2";
			}
			if ("4".equals(appId)) {
				ext = "3";
			}
			if ("5".equals(appId)) {
				ext = "4";
			}
			if ("6".equals(appId)) {
				ext = "5";
			}
			mandaoBatchId = MandaoSmsHelper.sendSms(serviceURL, mandaoSmsSn, mandaoSmsPwd, mobile,
					content, ext);
		}
		catch (Exception e) {
			logger.error("fail send sms", e);
			mandaoBatchId = "";
		}
		
		insertSmsSendRecord(mobile, content, appId, mandaoBatchId);
		
		if (mandaoBatchId.equals("")) {
			return false;
		}


		return true;
	}

	private void insertSmsSendRecord(String mobile, String content, String appid,
			String mandaoBatchId) {
		SmsSendRecord record = new SmsSendRecord();
		record.setAppId(appid);
		record.setMobile(mobile);
		record.setContent(content);
		record.setThirdBatchId(mandaoBatchId);
		record.setStatus("".equals(mandaoBatchId) ? SmsSendStatus.FAIL : SmsSendStatus.PENDING);
		smsSendRecordService.insert(record);
	}

	private String getSignByAppId(String appid) {
		String sign = null;
		Integer appCode = new Integer(0);
		try {
			appCode = Integer.valueOf(appid);
			switch (appCode) {
			case 1:
//				sign = "【快快开店】";
//				break;
			case 3:
				sign = "【想去】";
				break;
			case 2:
				sign = "【酷传】";
				break;
			case 4:
				sign = "【次元国】";
				break;
			case 5:
				sign = "【快看段子】";
				break;
			case 6:
				sign = "【怪兽嘟嘟】";
				break;
			default:
				break;
			}
		}
		catch (Exception e) {
			logger.error("appid param error, maybe not in 1 2 3");
		}
		return sign;
	}
}
