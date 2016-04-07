package com.vdlm.sms.aop;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SmsSendRateAop {

	Logger logger = LoggerFactory.getLogger(SmsSendRateAop.class);

	private static String SMSLIMIT_PHONE_PRE = "smsLimitPhone_%s";
	private static String SMSLIMIT_IP_PRE = "smsLimitIp_%s";
	private static String SMSLIMIT_PROXY_IP_PRE = "smsLimitProxyIp_%s";

	@Value("${sms.limit.phone.num}")
	private long phoneLimitNum = 10;

	@Value("${sms.limit.ip.num}")
	private long ipLimitNum = 6;
	
	@Value("${sms.limit.proxy.ip.num}")
	private long ipLimitProxyNum = 60;

	@Value("${sms.limit.timeout}")
	private int limitTimeout = 300;
	
	@Value("${sms.limit.holdlist}")
	private String holdList;

	@Autowired
	private MemcachedClient memcachedClient;

	@Pointcut(value = "execution (* com.vdlm.sms.controller.msg.SmsSendHandler.doSend(..)) && args(appId,mobile,content,ipAddress,clientIP)", argNames = "appId,mobile,content,ipAddress,clientIP")
	public void pointcut_doSend(String appId, String mobile, String content, String ipAddress,
			String clientIP) {
	}

	// 短信流量控制
	@Around(value = "pointcut_doSend(appId,mobile,content,ipAddress,clientIP)", argNames = "appId,mobile,content,ipAddress,clientIP")
	public Object sendSmsLimitAround(ProceedingJoinPoint point, String appId, String mobile,
			String content, String ipAddress, String clientIP) throws Throwable {
		if (StringUtils.isEmpty(mobile)) return false;
		
		if (!holdList.isEmpty() && holdList.contains(mobile)) {
				logger.error("SmsSend simu success {}", mobile);
				return false;
		}

		String key = String.format(SMSLIMIT_PHONE_PRE, mobile);

		try {
			long result = memcachedClient.incr(key, 1, 1, memcachedClient.getOpTimeout(), limitTimeout);
			if (result > phoneLimitNum) {
				logger.error("SmsSendLimitPhone {}, {},{},{},{},{}", result, appId, mobile, content, ipAddress,
						clientIP);
				return false;
			}
			if (StringUtils.isNotEmpty(clientIP)) {
				String proxyIp=null;
				if(clientIP.contains(","))//如果clientIp包含多个，获取真实ip和最后的代理ip做校验
				{
					proxyIp=StringUtils.trim(StringUtils.substringAfterLast(clientIP, ","));
					clientIP=StringUtils.trim(StringUtils.substringBefore(clientIP, ","));
				}
				key = String.format(SMSLIMIT_IP_PRE, clientIP);
				result = memcachedClient.incr(key, 1, 1, memcachedClient.getOpTimeout(), limitTimeout);
				logger.info("ipListmit print:" + clientIP + " result:" + result + " ipLimitNum:" + ipLimitNum);
				if (result > ipLimitNum) {
					logger.error("SmsSendLimitIp {}, {},{},{},{},{}", result, appId, mobile, content,
							ipAddress, clientIP);
					return false;
				}
				if(StringUtils.isNotEmpty(proxyIp))
				{
					key = String.format(SMSLIMIT_PROXY_IP_PRE, proxyIp);
					result = memcachedClient.incr(key, 1, 1, memcachedClient.getOpTimeout(), limitTimeout);
					logger.info("proxy ipListmit print:" + proxyIp + " result:" + result + " ipLimitProxyNum:" + ipLimitProxyNum);
					if (result > ipLimitProxyNum) {
						logger.error("SmsSendLimitIp {}, {},{},{},{},{}", result, appId, mobile, content,
								ipAddress, clientIP);
						return false;
					}
				}
				
			}
		}
		catch (Exception ex) {
			logger.error("Error to add to Memcached", ex);
		}

		return point.proceed();
	}

}
