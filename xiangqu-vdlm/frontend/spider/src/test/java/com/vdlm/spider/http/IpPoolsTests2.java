/**
 * 
 */
package com.vdlm.spider.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import com.vdlm.spider.ShopType;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 2:45:21 PM Jul 19, 2014
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext({ "classpath:spring-http.xml" })
public class IpPoolsTests2 {

	@SpringBeanByType
	IpPools ipPools;

	@Test
	public void testDisableIp() {
		ipPools.disableIp(ShopType.TAOBAO, "127.0.0.1");
	}
}
