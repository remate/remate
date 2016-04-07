package com.vdlm.web.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vdlm.service.outpay.impl.tenpay.MD5Util;
import com.vdlm.utils.http.PoolingHttpClients;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2016年2月24日上午11:42:32
 */
public class XqWapUserControllerTest {
	private static final String httpUrl = "http://dev.xiangqutest.com:9080";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
    @Test
	public void sign()
	{
		String phone="18858187175";
		String source="BIND_PHONE";
		System.out.println(MD5Util.MD5(phone+source+"xiangqu"));
	}

}
