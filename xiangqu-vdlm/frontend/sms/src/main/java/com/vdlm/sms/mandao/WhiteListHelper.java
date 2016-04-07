package com.vdlm.sms.mandao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class WhiteListHelper {

	public static boolean checkIPInWhileList(String testIpAddress,
			String lowIpAddress, String highIpAddress)
			throws UnknownHostException {
		long ipLo = ipToLong(InetAddress.getByName(lowIpAddress));
		long ipHi = ipToLong(InetAddress.getByName(highIpAddress));
		long ipToTest = ipToLong(InetAddress.getByName(testIpAddress));

		return ipToTest >= ipLo && ipToTest <= ipHi;

	}

	public static long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}
	
	public static String getIpAddr(HttpServletRequest request) { 
	    String ip = request.getHeader("x-forwarded-for");    
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("WL-Proxy-Client-IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_CLIENT_IP");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");    
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {    
            ip = request.getRemoteAddr();    
        }
        return ip; 
	}

	/*public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
//		if (ipAddress == null || ipAddress.length() == 0
//				|| "unknown".equalsIgnoreCase(ipAddress)) {
//
//		}
		ipAddress = request.getRemoteAddr();
//		if (ipAddress.equals("127.0.0.1")) {
//			// 根据网卡取本机配置的IP
//			InetAddress inet = null;
//			try {
//				inet = InetAddress.getLocalHost();
//			} catch (UnknownHostException e) {
//			}
//			ipAddress = inet.getHostAddress();
//		}

		return ipAddress;

	}*/

	public static boolean isLegalRequest(String ipAddress, String whitelist) {
		boolean isLegalRequest = false;
		Map<String, String> whiteListRange = getWhiteListRange(whitelist);
		Iterator<String> iterator = whiteListRange.keySet().iterator();
		while (iterator.hasNext()) {
			String lowIpAddress = iterator.next();
			String highIpAddress = whiteListRange.get(lowIpAddress);
			try {
				if (WhiteListHelper.checkIPInWhileList(ipAddress, lowIpAddress,
						highIpAddress)) {
					isLegalRequest = true;
					break;
				}
			} catch (UnknownHostException e) {
			}
		}
		return isLegalRequest;
	}

	public static Map<String, String> getWhiteListRange(String whitelist) {
		Map<String, String> whiteListMap = new HashMap<String, String>();
		if (StringUtils.isEmpty(whitelist)) {
			return whiteListMap;
		}
		String[] whitelistArray = whitelist.split(";");
		for (int i = 0; i < whitelistArray.length; i++) {
			String temp = StringUtils.trimWhitespace(whitelistArray[i]);
			if (temp.contains("-")) {
				String[] ipPair = temp.split("-");
				if (ipPair.length == 1) {
					whiteListMap.put(ipPair[0], ipPair[0]);
				} else {
					whiteListMap.put(ipPair[0], ipPair[1]);
				}
			} else {
				whiteListMap.put(temp, temp);
			}
		}
		return whiteListMap;

	}

	public static void main(String[] args) throws UnknownHostException {
		long ipLo = ipToLong(InetAddress.getByName("192.2.3.1"));
		long ipHi = ipToLong(InetAddress.getByName("192.2.4.0"));
		long ipToTest = ipToLong(InetAddress.getByName("192.2.3.0"));

		System.out.println(ipToTest >= ipLo && ipToTest <= ipHi);

		String whitelist = "10.8.1.36-10.8.1.36; ";
		String[] whitelistArray = whitelist.split(";");
		System.out.println(whitelistArray.length);
	}

}
