package com.vdlm.restapi.user;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author kerns<mijiale@ixiaopu.com>
 * @since 2015年10月19日下午4:44:22
 */
public class UserAuthControllerTest {
	private static final String  httpUrl = "http://dev.xiangqutest.com:8888";
	@Test
	public void test() {
		String url = httpUrl+"/v2/login_check?u=13067871131&p=1234";
        com.vdlm.utils.http.HttpInvokeResult invokeResult =  com.vdlm.utils.http.PoolingHttpClients.get(url);
        Assert.assertNotNull(invokeResult);
        Assert.assertEquals(200,invokeResult.getStatusCode());
        System.out.println(invokeResult.getContent());
	}

}
