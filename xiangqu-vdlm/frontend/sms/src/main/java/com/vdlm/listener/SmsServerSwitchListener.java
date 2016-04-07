package com.vdlm.listener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component("smsServerSwitchListener")
public class SmsServerSwitchListener implements
		ApplicationListener<ContextRefreshedEvent> {

	private Logger log = LoggerFactory.getLogger(SmsServerSwitchListener.class);

	@Value("${mandao.sms.mainserviceurl}")
	private String mainServiceURL; // 主地址
	@Value("${mandao.sms.standbyserviceurl}")
	private String standbyServiceURL;// 备用地址
	private int connectCountLimit = 3;// 地址连接限制次数

	private volatile String serviceURL = mainServiceURL;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		boolean firstContext = event.getApplicationContext().getDisplayName()
				.equals("Root WebApplicationContext");
		if (firstContext) {
			log.info("SmsServerSwitchListener start!");
			ChangeUrlThread changeUrlThread = new ChangeUrlThread();
			changeUrlThread.setDaemon(true);
			changeUrlThread.start();
		}

	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	private class ChangeUrlThread extends Thread {
		private int count = 0;
		private URLConnection connection;
		private HttpURLConnection httpconn;

		public void run() {
			log.info("detect mandao sms server switch thread start...");
			while (true) {
				try {
					URL url = new URL(mainServiceURL);
					connection = url.openConnection();
					httpconn = (HttpURLConnection) connection;
					// 设置连接超时时间10秒
					httpconn.setConnectTimeout(10000);
					httpconn.connect();
					// 连接成功，切换为主地址
					setServiceURL(mainServiceURL);
					count = 0;
					// 线程沉睡
					sleepSomeSecond(180);
				} catch (IOException e) {
					count++;
					if (count == 1)
						log.error("mandao sms main server connect failed "
								+ count + " times...", e);
					// 线程主地址三次连接失败连接 切换为备用地址
					if (count > connectCountLimit) {
						setServiceURL(standbyServiceURL);
						log.info("switch to mandao sms standby server, current time:"
								+ new Date());
						count = 0;
						sleepSomeSecond(60);
					} else {
						sleepSomeSecond(10);
					}
				}
			}
		}

		private void sleepSomeSecond(int num) {
			try {
				Thread.sleep(1000 * num);
			} catch (InterruptedException e) {
				log.error("thread sleep interrupt!", e);
			}
		}

	}

}
