/**
 * 
 */
package com.vdlm.spider;

import org.junit.Test;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 11:20:12 AM Jul 19, 2014
 */
public class ShopTypeTests {

	@Test
	public void testValueOf() {
		System.out.println(ShopType.valueOf(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValueOf2() {
		ShopType.valueOf(5);
	}

}
