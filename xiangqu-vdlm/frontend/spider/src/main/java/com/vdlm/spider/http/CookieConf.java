/**
 * 
 */
package com.vdlm.spider.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.vdlm.spider.InitializedException;
import com.vdlm.spider.core.ThreadLocalRandom;

public class CookieConf {
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
	
	//Map<String, List<Cookies>> defaultConfs = new HashMap<String, List<Cookies>>();
	List<Cookie> defConf = new  ArrayList<Cookie>();
	Map<String, Map<Integer, List<Cookie>>> confs = new HashMap<String, Map<Integer, List<Cookie>>>();
	private int expTimes = 1;
	private List<String> ckeys = new ArrayList<String>();
	private Map<String, String> ipBind = new HashMap<String, String>();
	private static String filePath = "/ouer/conf/cookie.ini";
	private long lastModified = -1;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		CookieConf.filePath = filePath;
	}
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	private Boolean updateDefCookie(Ini conf, String cookie) {
		for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
			if (entry.getKey().equals("def")) {
				entry.getValue().put("ck1", cookie);
				return true;
			}
		}
		return false;
	}
	
	private Boolean updateDefCookie(Ini conf, List<Cookie> cks) {
		Boolean ret = false;
		for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
			if ( ! entry.getKey().equals("def"))  continue;
			String ck1 = entry.getValue().get("ck1");
			String[] arr = ck1.split(";");
			if (arr == null) continue;
			for (String str : arr) {
				String[] pair = str.split("=");
				if (pair.length == 2) {
					for (Cookie e : cks) {
						if (pair[0].trim().equalsIgnoreCase(e.getK()) && ! pair[1].trim().equalsIgnoreCase(e.getV())) {
							pair[1] = e.getV();
							ret = true;
						}
					}
				}
			}
			
			if (ret) {
				StringBuilder sb = new StringBuilder();
				for (String s : arr) {
					sb.append(s).append(';');
				}
				
				if (sb != null) {
					entry.getValue().put("ck1", sb.toString());
					return ret;
				}
			}
		}
		return ret;
	}
	
	private Boolean updateSpecCookie(Ini conf, String ip, String cookie) {
		String set  = getIpBind().get(StringUtils.isEmpty(ip) ? "127.0.0.1" : ip);
		if (StringUtils.isNotBlank(set)) {
			for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
				if (entry.getKey().equals(set)) { // 找到已绑定ip的set
					Profile.Section sec = entry.getValue();
					for (Map.Entry<String, String> aentry : sec.entrySet()) {
						if (aentry.getValue().equals(cookie)) 
							return false; // cookie已存在就不用再更新了
					}

					int size = confs.get(set).size();
					if (size == 0) {
						sec.add("ck1", cookie);
					} else if (size == 1) {
						sec.add("ck2", cookie);
					} else if (size == 2) {
						sec.add("ck3", cookie);
					} else {
						sec.put("ck1", cookie); // 替换
					}
				}
			}
		} else { // save def
			updateDefCookie(conf, cookie);
		} // end for
		return true;
	}
	
	private Boolean updateSpecCookie(Ini conf, String ip, List<Cookie> cks) {
		String set  = getIpBind().get(StringUtils.isEmpty(ip) ? "127.0.0.1" : ip);
		if (StringUtils.isNotBlank(set)) {
			for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
				if ( ! entry.getKey().equals(set))  continue; // 找到已绑定ip的set
				
				Profile.Section sec = entry.getValue();
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> aentry : sec.entrySet()) {
					String ck = aentry.getValue();
					String[] arr = ck.split(";");
					if (arr == null) continue;
					for (String str : arr) {
						String[] pair = str.split("=");
						if (pair.length == 2) {
							for (Cookie e : cks) {
								if (pair[0].trim().equalsIgnoreCase(e.getK())) {
									pair[1] = e.getV();
								}
							}
						}
					}
		
					for (String s : arr) {
						sb.append(s).append(';');
					}
				}

				int size = confs.get(set).size();
				if (size == 0) {
					sec.add("ck1", sb.toString());
				} else if (size == 1) {
					sec.add("ck2", sb.toString());
				} else if (size == 2) {
					sec.add("ck3", sb.toString());
				} else {
					sec.put("ck1", sb.toString()); // 替换
				}
			}
		} else { // save def
			//updateDefCookie(conf, cookie);
			return false;
		} // end for
		return true;
	}
	
	public Boolean store(List<Cookie> cks, String ip) throws IOException {
		if (cks == null) return false;
		int cnt = 0;

		List<Cookie> effCks = new ArrayList<Cookie>();
		for (Cookie e : cks) {
			if (ckeys.contains(e.getK())) { // do it
				effCks.add(e);
				cnt++;
			}
		}
		if (cnt > 0) {
			if ( !this.available() ) {
				this.init();
			}
			File file = new File(this.getFilePath());
			Ini conf;
			try {
				Boolean needUpdate = false;
				conf = new Ini(file);
				if (StringUtils.isEmpty(ip)) {
					//needUpdate = updateDefCookie(conf, sb.toString());
					needUpdate = updateDefCookie(conf, effCks);
				} else {
					//needUpdate = updateSpecCookie(conf, ip, sb.toString());
					needUpdate = updateSpecCookie(conf, ip, effCks);
				}
				if (needUpdate)
					conf.store(file);
			}
			catch (Exception e) {
				throw new InitializedException("can not create Ini4j by File:" + file.getAbsolutePath());
			}
			
			return true;
		}
		return false;
	}
	
	public Boolean reload() throws IOException {
		
		final File file = new File(this.getFilePath());
		if (file.lastModified() <= lastModified) {
			return null;
		}
		log.debug("reload cookies from : " + this.getFilePath());

		Ini conf;
		try {
			conf = new Ini(file);
		}
		catch (Exception e) {
			throw new InitializedException("can not create Ini4j by File:" + file.getAbsolutePath());
		}
		
		// 提前把tag取出来
		final Profile.Section sect = conf.get("tag");
		for (Map.Entry<String, String> aentry : sect.entrySet()) {
			ckeys.add(aentry.getKey());
		}
		conf.remove("tag");
		
		for (Map.Entry<String, Profile.Section> entry : conf.entrySet()) {
			String key = entry.getKey();
			if (key.equals("common")) {
				if (StringUtils.isNotEmpty(entry.getValue().get("exp_times"))) {
					setExpTimes(Integer.valueOf(entry.getValue().get("exp_times")));
				}
			} else if (key.equals("tag")) {
				//
			} else {
				Map<Integer, List<Cookie>> ret = create(entry);
				if (key.equals("def")) {
					defConf = ret.get(0);
				} else {
					confs.put(key, ret);
				}
			}
		}
		log.info("ckeys:" + ckeys.toString());
		log.info("defConf:" + JSON.toJSONString(defConf));
		log.info("confs:" + JSON.toJSONString(confs));
		this.lastModified = file.lastModified();
		return true;
	}
	
	Map<Integer, List<Cookie>> create(Map.Entry<String, Profile.Section> entry) {
		final Map<Integer, List<Cookie>> result = new HashMap<Integer, List<Cookie>>();
		List<String> alist = new ArrayList<String>(3);
		String ck1 = entry.getValue().get("ck1");
		String ck2 = entry.getValue().get("ck2");
		String ck3 = entry.getValue().get("ck3");
		alist.add(ck1);
		alist.add(ck2);
		alist.add(ck3);
		
		Integer idx = 0;
		for (String ck : alist) {
			List<Cookie> list = new ArrayList<Cookie>();
			if (StringUtils.isBlank(ck)) {
				continue;
			}
			String[] arr = ck.split(";");
			for (String str : arr) {
				String[] pair = str.split("=");
				if (pair.length == 2)
					list.add(new Cookie(pair[0].trim(), pair[1].trim()));
			}
			result.put(idx, list);
			idx++;
		}
		return result;
	}
	
	
	public void destroy() {
		if (log.isInfoEnabled()) {
			log.info("start to destroy " + getClass().getSimpleName());
		}
		
		this.exec.shutdown();
		
		if (log.isInfoEnabled()) {
			log.info("success to destroy " + getClass().getSimpleName());
		}
	}
	
	public void init() throws IOException {
		if (log.isInfoEnabled()) {
			log.info("start to init " + getClass().getSimpleName());
		}

		if (StringUtils.isNotEmpty(this.getFilePath())) {
			File file = new File(filePath);
			if (!file.exists()) {
				filePath=null;
			}
		}
		
		if (StringUtils.isEmpty(this.getFilePath())) {
			final URL url = this.getClass().getResource("/http/cookie.ini");
			if (url == null) {
				throw new FileNotFoundException("cookie.ini");
			}
			this.setFilePath(url.getFile());
		}
		this.reload();

		exec.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					CookieConf.this.reload();
				}
				catch (Exception e) {
					log.error("Error to reload cookie.ini", e);
				}
			}
		}, 1, 10, TimeUnit.MINUTES);

		if (log.isInfoEnabled()) {
			log.info("success to init " + getClass().getSimpleName());
		}
	}
	
	private String getSetValue() {
		for (String str : confs.keySet()) {
			if (getIpBind().containsValue(str)) continue;
			return str;
		}
		return null;
	}
	
	public List<Cookie> getCookie(String ip) {
		if (StringUtils.isEmpty(ip)) {
			return this.defConf;
		} else {
			if ( !getIpBind().containsKey(ip)) {
				String val = getSetValue();
				if (StringUtils.isNotEmpty(val))
					getIpBind().put(ip, val);
				else 
					return this.defConf;
			}
			return confs.get(getIpBind().get(ip))
					.get(ThreadLocalRandom.current().nextInt( confs.get(getIpBind().get(ip)).size() ));
		}
	}
	
	public Boolean available() {
		Boolean ret = false;
		if  ((confs != null && ! confs.isEmpty()) || ( defConf != null  && ! defConf.isEmpty())) {
			ret = true;
		} else {
			try {
				init();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	public int getExpTimes() {
		return expTimes;
	}
	public void setExpTimes(int expTimes) {
		this.expTimes = expTimes;
	}
	public List<String> getCkeys() {
		return ckeys;
	}
	public void setCkeys(List<String> ckeys) {
		this.ckeys = ckeys;
	}
	public Map<String, String> getIpBind() {
		return ipBind;
	}
	public void setIpBind(Map<String, String> ipBind) {
		this.ipBind = ipBind;
	}
	
}
