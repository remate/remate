package com.vdlm.spider.task;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.http.HttpInvokeResult;

public class ParseSkuTest {

	@Test
	public void test() {
		try {	
			final String taobaoFile = this.getClass().getResource("/data/taobao.ajax.txt").getFile();
			final String tmallFile = this.getClass().getResource("/data/tmall.ajax.txt").getFile();
						
			final String content = FileUtils.readFileToString(new File(tmallFile));
			
			final HttpInvokeResult result = new HttpInvokeResult();
			result.setContent(content.getBytes());
			result.setCharset(Charset.forName(Statics.UTF8));			
			
			final SkuTaskBean bean=new SkuTaskBean();
			bean.setShopType(ShopType.TMALL);				 
			final CrawlSkuTask crawlSkuTask=new CrawlSkuTask(null,bean,null,null ,1);
			
			final ParseSkuTask parseSkuTask= new ParseSkuTask(null, null, result, crawlSkuTask);
			parseSkuTask.parse();	
			
		} catch (final Exception e) {
			// TODO: handle exception
		}	
	}
}
