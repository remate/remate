package com.vdlm.spider.task;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.http.HttpInvokeResult;

public class ParseItemTest {

	@Test
	public void test() {
		try {	
			final String taobaoFile = this.getClass().getResource("/data/taobao.item.txt").getFile();
			final String tmallFile = this.getClass().getResource("/data/tmall.item.txt").getFile();
						
			final String content = FileUtils.readFileToString(new File(taobaoFile));
			
			final HttpInvokeResult result = new HttpInvokeResult();
			result.setContent(content.getBytes());
			result.setCharset(Charset.forName(Statics.UTF8));			
			
			final ItemTaskBean bean=new ItemTaskBean();
			bean.setShopType(ShopType.TAOBAO);		 			
			final CrawlItemTask crawlItemTask=new CrawlItemTask(null,bean,null,null, 1);
			
			
			final ParseItemTask parseItemTask= new ParseItemTask(null, null, result, crawlItemTask);
			parseItemTask.parse();	
			
		} catch (final Exception e) {
			// TODO: handle exception
		}	
	}
}
