/**
 * 
 */
package com.vdlm.spider;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.junit.Before;
import org.junit.Test;

import com.vdlm.spider.core.ConcurrentHashSet;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpClientProvider;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.utils.TelnetUtils;

/**
 * <pre>
 * 清洗ip
 * </pre>
 * 
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:34:19 PM Apr 20, 2015
 */
public class CleanIp {

	TelnetClient telnet = new TelnetClient();

	List<String> lines;
	ConcurrentHashSet<String> ips = new ConcurrentHashSet<String>();
	CountDownLatch cdl;
	HttpClientProvider provider;

	@Before
	public void before() throws Exception {
		final File file = new File("C:\\TempFiles\\productId.txt");

		// this.lines = new ArrayList<String>();
		// Collections.addAll(this.lines,
		// FileUtils.readFileToString(file).split(","));
		this.lines = FileUtils.readLines(file);
		this.cdl = new CountDownLatch(this.lines.size());
		this.provider = new HttpClientProvider();
		this.provider.init();
	}

	@Test
	public void clean() throws Exception {
		final int corePoolSize = 16;
		final ScheduledExecutorService exec = Executors.newScheduledThreadPool(corePoolSize);
		final int total = this.lines.size();
		final int avg = total / corePoolSize;
		for (int i = 0; i < corePoolSize; i++) {
			final int bgn = avg * i;
			final int end = avg * (i + 1);
			exec.submit(new Runnable() {
				@Override
				public void run() {
					for (int x = bgn; x < end; x++) {
						try {
							outEnabledIp(lines.get(x));
						} finally {
							cdl.countDown();
						}
					}
				}
			});
		}

		final int end = avg * corePoolSize;
		if (total > end) {
			for (int x = end; x < total; x++) {
				try {
					outEnabledIp(lines.get(x));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cdl.countDown();
				}
			}
		}

		cdl.await();

		for (String ip : this.ips) {
			System.out.println(ip);
		}
	}

	Pattern pattern = Pattern.compile("\\d+.\\d+.\\d+.\\d+:\\d+");

	void outEnabledIp(String line) {
		if (StringUtils.isBlank(line)) {
			return;
		}
		final String hostname;
		final int port;

		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			final String[] arr = matcher.group().split(":");
			hostname = arr[0];
			port = Integer.parseInt(arr[1]);
		} else {
			final String[] arr = line.split("\\s+");
			if (arr.length < 4) {
				return;
			}
			hostname = arr[1];
			port = Integer.parseInt(arr[2]);
		}
		final String address = hostname + ":" + port;
		if (this.ips.contains(address)) {
			return;
		}

		if (!TelnetUtils.telnetSuccessfully(hostname, port)) {
			return;
		}

		HttpClientInvoker invoker = provider.provide(ShopType.TAOBAO, "http://www.weixl.com", null, address);

		HttpInvokeResult result = invoker.invoke();

		if (result.isOK()) {
			this.ips.add(address);
			System.out.println(address);
		}
	}
}
