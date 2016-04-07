package com.vdlm.sms.mandao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//该Demo主要解决Java在Linux等环境乱码的问题
public class MandaoSmsHelper {
	private static Logger log = LoggerFactory.getLogger(MandaoSmsHelper.class);

	/**
	 * 漫道发送短信：传多个手机号就是群发，一个手机号就是单条提交
	 * 
	 * @param serviceURL
	 *            短信服务器地址
	 * @param sn
	 *            序列号
	 * @param pwd
	 *            密码
	 * @param mobiles
	 *            手机号
	 * @param content
	 *            URL_UT8编码内容
	 * @param ext
	 *            扩展码
	 * @param stime
	 *            定时时间
	 * @param rrid
	 *            唯一标识
	 * @return true or false
	 * @throws UnsupportedEncodingException
	 */
	public static String sendSms(String serviceURL,String sn, String pwd, String mobiles,
			String content, String ext, String stime, String rrid)
			throws UnsupportedEncodingException {
		MandaoSmsClient client = new MandaoSmsClient(serviceURL,sn, pwd);
		String content_utf8 = URLEncoder.encode(content, "utf8");
		String result_mt = client.mdSmsSend(mobiles, content_utf8, ext, stime,
				rrid);
		
		if (result_mt.startsWith("-") || result_mt.equals(""))// 发送短信，如果是以负号开头就是发送失败。
		{
			log.error("send sms to MD fail, return value：" + result_mt + " please check webservice return value table!" +
					" mobiles:" + mobiles + " content:" + content + " ext:" + ext + " stime:" + stime + " rrid:" + rrid);
			return "";
		}
		// 输出返回标识，为小于19位的正数，String类型的。记录您发送的批次。
		log.info("send sms to MD success, return value：" + result_mt + ", mobiles: " + mobiles + ", content: " + content);
		return result_mt;
	}

	/**
	 * 漫道发送短信：传多个手机号就是群发，一个手机号就是单条提交
	 * 
	 * @param sn
	 *            序列号
	 * @param pwd
	 *            密码
	 * @param mobiles
	 *            手机号
	 * @param content
	 *            URL_UT8编码内容
	 * @return true or false
	 * @throws UnsupportedEncodingException
	 */
	public static String sendSms(String serviceURL,String sn, String pwd, String mobiles,
			String content,String ext) throws UnsupportedEncodingException {
		return sendSms(serviceURL,sn, pwd, mobiles, content, ext, "", "");
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// 输入软件序列号和密码
		String serviceURL = "http://sdk2.entinfo.cn:8060/webservice.asmx";
		String sn = "SDK-BBX-010-20987";
		String pwd = "C-d243-4";
		String mobiles = "18667181802";
		String content = URLDecoder.decode(URLEncoder.encode("图,片.测试内容【快快开店】", "utf8"), "utf8");
		String ext = "1";
		sendSms(serviceURL, sn, pwd, mobiles, content,ext);
	}
}
