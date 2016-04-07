package com.vdlm.spider.task;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.http.HttpInvokeResult;

public class ParseItemListTest {

	@Test
	public void test() {
		try {	
			final String taobaoFile = this.getClass().getResource("/data/taobao.itemlist.txt").getFile();
			final String tmallFile = this.getClass().getResource("/data/tmall.itemlist.txt").getFile();
						
			final String content = FileUtils.readFileToString(new File(taobaoFile));
			
			final HttpInvokeResult result=new HttpInvokeResult();
			result.setContent(content.getBytes());
			result.setCharset(Charset.forName(Statics.UTF8));			
			
			final ItemListTaskBean bean=new ItemListTaskBean();
			bean.setShopType(ShopType.TAOBAO);		 			
			final CrawlItemListTask crawlItemListTask=new CrawlItemListTask(null,bean,null,null, 1, null);
			
			final ParseItemListTask parseItemListTask= new ParseItemListTask(null, null, result, crawlItemListTask);
			parseItemListTask.parse();	
			
		} catch (final Exception e) {
			// TODO: handle exception
		}	
	}

}
