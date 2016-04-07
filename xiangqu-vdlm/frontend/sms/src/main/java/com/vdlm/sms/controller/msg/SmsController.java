package com.vdlm.sms.controller.msg;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.dal.model.SmsSendRecord;
import com.vdlm.dal.status.SmsSendStatus;
import com.vdlm.dal.util.SpringContextUtil;
import com.vdlm.dal.vo.OrderVO;
import com.vdlm.dal.vo.UserInfo;
import com.vdlm.listener.SmsServerSwitchListener;
import com.vdlm.service.msg.SmsSendRecordService;
import com.vdlm.service.push.PushService;
import com.vdlm.sms.ResponseObject;
import com.vdlm.sms.mandao.MandaoSmsHelper;
import com.vdlm.sms.mandao.WhiteListHelper;

@Controller
public class SmsController {

    private Logger log = LoggerFactory.getLogger(getClass());

    //	@Value("${mandao.sms.notify.on-off}")
    //	private boolean smsNotifyOn;

    @Value("${mandao.sms.sn}")
    private String mandaoSmsSn;
    
    @Value("${mandao.sms.pwd}")
    private String mandaoSmsPwd;

    @Value("${mandao.sms.mainserviceurl}")
    private String serviceURL;

    @Value("${mandao.sms.whitelist}")
    private String whitelist;

    @Autowired
    private PushService pushService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private SmsSendRecordService smsSendRecordService;

    @Autowired
    private SmsSendHandler       smsSendHandler;

    @ResponseBody
    @RequestMapping(value = "monitor")
    public ResponseObject<Boolean> smsServiceMonitor() {
        return new ResponseObject<Boolean>(true);
    }

    //	@ResponseBody
    //	@RequestMapping(value = "/smsTest")
    //	public ResponseObject<Boolean> smsTest(HttpServletRequest request,
    //			HttpServletResponse response) {
    //		String mobile = "18667181802";
    //		String content = "17767070359";
    //		// String mobile = (String) request.getParameter("mobile");
    //		// String content = (String) request.getParameter("content");
    //
    //		boolean result = pushService.sendSms(mobile, content);
    //		return new ResponseObject<Boolean>(result);
    //	}

    @RequestMapping("/mandaoSms/notify")
    public void mandaoSmsNotify(String args, HttpServletResponse response) {
        try {
            //			if(!smsNotifyOn){
            //				response.getWriter().print("0");
            //				return;
            //			}
            if (StringUtils.isNotBlank(args)) {
                String[] batchStrs = args.split(";");
                for (int i = 0; i < batchStrs.length; i++) {
                    String batchStr = batchStrs[i];
                    if (StringUtils.isNotBlank(batchStr)) {
                        String[] msg = batchStr.split(",");
                        String mobile = msg[2];
                        String thirdBatchId = msg[3];
                        SmsSendStatus status;
                        if (msg[4].equals("0") || msg[4].equals("DELIVRD")) {
                            log.info("sms notify mobile:{} thirdBatchId:{}  success result:{}", mobile, thirdBatchId, batchStr);
                            status = SmsSendStatus.SUCCESS;
                        } else {
                            log.info("sms notify mobile:{} thirdBatchId:{} failed result:{}", mobile, thirdBatchId, batchStr);
                            status = SmsSendStatus.FAIL;
                        }
                        String thirdResult = batchStr;
                        smsSendRecordService.updateSendResult(mobile, thirdBatchId,
                            status.toString(), thirdResult);
                    }
                }
            } else {
                log.error("接受通知参数异常");
            }
            response.getWriter().print("0");
        } catch (Exception e) {
            log.error("漫道通知返回失败");
        }
    }

    /**
     * 以Json格式传入数据
     * 
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/engine/send")
    public ResponseObject<Boolean> smsEngineService(HttpServletRequest request,
                                                    HttpServletResponse response) {
        String appId = request.getParameter("appid");
        String mobile = request.getParameter("mobile");
        String template = request.getParameter("content");
        String data = request.getParameter("data");
        String ipAddress = WhiteListHelper.getIpAddr(request);
        String clientIP = request.getParameter("clientIP");

        String content = "";

        IWebContext model1 = new WebContext(request, response, request.getServletContext());
        JSONObject json = JSONObject.parseObject(data);
        if (StringUtils.isNotBlank(data)) {
            Iterator<String> it = json.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (key.equals("order")) {
                    JSONObject orderJson = json.getJSONObject(key);
                    OrderVO order = JSON.toJavaObject(orderJson, OrderVO.class);
                    model1.getRequestAttributes().put(key, order);
                } else if (key.equals("buyer")) {
                    JSONObject buyerJson = json.getJSONObject(key);
                    UserInfo buyer = JSON.toJavaObject(buyerJson, UserInfo.class);
                    model1.getRequestAttributes().put(key, buyer);
                } else if (key.equals("seller")) {
                    JSONObject sellerJson = json.getJSONObject(key);
                    UserInfo seller = JSON.toJavaObject(sellerJson, UserInfo.class);
                    model1.getRequestAttributes().put(key, seller);
                } else {
                    String str = json.getString(key);
                    model1.getRequestAttributes().put(key, str);
                }
            }
            content = templateEngine.process(template, model1).trim();
        } else {
            content = template;
        }
        log.info("/engine/send appid:{} , mobile:{}, content:{}, ipAddress:{}, clientIP:{}",
        		appId, mobile, content, ipAddress, clientIP);
        return new ResponseObject<Boolean>(smsSendHandler.doSend(appId, mobile, content, ipAddress,
            clientIP));
    }

    // "http://10.8.100.109:8080/sms/send?mobile=18667181802&content=test1&appid=1";
    @ResponseBody
    @RequestMapping(value = "/send")
    public ResponseObject<Boolean> smsService(HttpServletRequest request,
                                              HttpServletResponse response) {
        String mobile = (String) request.getParameter("mobile");
        String content = (String) request.getParameter("content");
        String appId = (String) request.getParameter("appid");
        String ipAddress = WhiteListHelper.getIpAddr(request);
        String clientIP = (String) request.getParameter("clientIP");
        log.info("/send appid:{} , mobile:{}, content:{}, ipAddress:{}, clientIP:{}",
        		appId, mobile, content, ipAddress, clientIP);
        return new ResponseObject<Boolean>(smsSendHandler.doSend(appId, mobile, content, ipAddress,
            clientIP));
    }

}
