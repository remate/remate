/**
 * 
 */
package com.vdlm.spider.queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vdlm.spider.core.BlockingFQueue;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 4:01:25 PM Jul 20, 2014
 */
public class BlockingFQueueTests {
	
	BlockingFQueue queue;
	
	@Before
	public void before() {
		queue = new BlockingFQueue("/ouer/data/fqueue/test");
	}

	@Test
	public void testTake() throws Exception {
		
		queue.add("test".getBytes());
		
		
		byte[] bytes = queue.take();
		
		Assert.assertEquals("test", new String(bytes));
	}
}
