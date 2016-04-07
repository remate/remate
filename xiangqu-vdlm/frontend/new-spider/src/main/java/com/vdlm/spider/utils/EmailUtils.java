/**
 * 
 */
package com.vdlm.spider.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 9:25:13 PM Apr 19, 2015
 */
public class EmailUtils {

	private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	public static void sendQuietly(String subject, String msg) {
		try {
			send(subject, msg);
		}
		catch (Exception e) {
			logger.warn("Error to send email, subject:\n" + subject + "\nmsg:\n" + msg, e);
		}
	}

	public static void send(String subject, String msg) throws EmailException {
		// TODO 参数做成配置
		SimpleEmail email = new SimpleEmail();
		email.setCharset("UTF-8");
		// smtp host
		email.setHostName("smtp.163.com");
		// 登陆邮件服务器的用户名和密码
		email.setAuthentication("ouer_spider", "ouer@2014");
		// 接收人
		email.addTo("lucifer@ixiaopu.com", "lucifer");
		email.addTo("odin@ixiaopu.com", "odin");
		email.addTo("aton@ixiaopu.com", "aton");
		email.addTo("ladon@ixiaopu.com", "ladon");
		// 发送人
		email.setFrom("ouer_spider@163.com", "ouer_spider");
		// 标题
		email.setSubject(subject);
		// 邮件内容
		email.setMsg(msg);
		// 发送
		email.send();
	}
}
