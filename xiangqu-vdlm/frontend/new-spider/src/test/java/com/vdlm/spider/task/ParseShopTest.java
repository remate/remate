package com.vdlm.spider.task;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.http.HttpInvokeResult;

public class ParseShopTest {
	
	@Test
	public void test() {
		try {	
			final String taobaoFile = this.getClass().getResource("/data/taobao.shop.html").getFile();
			final String tmallFile = this.getClass().getResource("/data/tmall.shop.html").getFile();
			
			
			final String htmlContent = FileUtils.readFileToString(new File(taobaoFile));
			
			final HttpInvokeResult result=new HttpInvokeResult();
			result.setContent(htmlContent.getBytes());
			result.setCharset(Charset.forName(Statics.UTF8));			
			
			final ShopTaskBean bean=new ShopTaskBean();
			bean.setShopType(ShopType.TAOBAO);
			bean.setRnd(StringUtils.EMPTY);						
			final CrawlShopTask crawlShopTask=new CrawlShopTask(null,bean,null,null, 1);
			
			final ParseShopTask parseShopTask= new ParseShopTask(null, null, result, crawlShopTask);
			parseShopTask.parse();	
			
		} catch (final Exception e) {
			// TODO: handle exception
		}	
	}
}
