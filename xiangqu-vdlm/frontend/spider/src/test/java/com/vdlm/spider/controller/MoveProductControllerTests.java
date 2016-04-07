/**
 * 
 */
package com.vdlm.spider.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.vdlm.utils.http.HttpInvokeResult;
import com.vdlm.utils.http.PoolingHttpClients;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 3:55:46 PM Jul 27, 2014
 */
public class MoveProductControllerTests {

	MoveProductController testObject;

	@Before
	public void before() {
		this.testObject = new MoveProductController();
	}

	@Test
	public void testRnd() {
		final Map<String, String> params = new HashMap<String, String>();
		params.put("rnd", "123456");
		params.put("ouerUserId", "8rrn7z99");
		params.put("ouerShopId", "1r5bid0x");
		params.put("shopType", "3");
		params.put("itemId", "123456");

		// 搬家
		final HttpInvokeResult rslt = PoolingHttpClients.post("http://localhost:12080/moveProduct/rnd", params);
		System.out.println(rslt);

	}

	@Test
	public void testMoveProduct() throws Exception {
		final File file = new File("C:\\Users\\Think\\Desktop\\shopUrl.txt");
		for (String line : FileUtils.readLines(file)) {

			final String url = "http://122.225.68.112:12080/moveProduct/lgnu?reqFrom=2&url=" + line;

			// 搬家
			final HttpInvokeResult rslt = PoolingHttpClients.get(url, 60000);
			if (rslt.isOK()) {
				System.out.println("success to moveProduct: " + line);
			}
			else {
				System.out.println("Failed to moveProduct: " + line);

				System.err.print(rslt);
				if (rslt.getException() != null) {
					rslt.getException().printStackTrace();
				}
				break;
			}
		}
	}
}
