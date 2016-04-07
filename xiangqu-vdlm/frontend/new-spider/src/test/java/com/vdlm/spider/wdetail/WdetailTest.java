package com.vdlm.spider.wdetail;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.vdlm.common.protocol.GsonObjectConverter;
import com.vdlm.common.protocol.ObjectConverter;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class WdetailTest {

	@Test 
	public void testTaobao2Tmall() throws ClientProtocolException, IOException {
		final String url = "http://item.taobao.com/item.htm?id=38614922233";
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(url);
		final HttpResponse response = client.execute(method);
		final HttpEntity respEntity = response.getEntity();
		final String result = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		System.out.println(result);
	}
	
	@Test
	public void testWdetail() throws Exception {
		final String url = "http://hws.m.taobao.com/cache/wdetail/5.0?id=44215763168";
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(url);
		final HttpResponse response = client.execute(method);
		final HttpEntity respEntity = response.getEntity();
		final String result = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		final ObjectConverter converter = new GsonObjectConverter();
		final ItemResponse resp = converter.fromString(result, ItemResponse.class);
		System.out.println(resp);
	}
	
	@Test
	public void testWdetailParser() throws Exception {
		final String url = "http://hws.m.taobao.com/cache/wdetail/5.0?id=40373787907";
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(url);
		final HttpResponse response = client.execute(method);
		final HttpEntity respEntity = response.getEntity();
		final String result = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		final ObjectConverter converter = new GsonObjectConverter();
		final ItemResponse resp = converter.fromString(result, ItemResponse.class);
		final WItem item = ApiStackValueParser.parseItem(resp);
		System.out.println(item);
	}
	
	@Test
	public void testWdetailDesc() throws Exception {
		final String url = "http://hws.m.taobao.com/cache/mtop.wdetail.getItemDescx/4.1/?data=%7B%22item_num_id%22%3A%223342512099%22%7D";
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(url);
		final HttpResponse response = client.execute(method);
		final HttpEntity respEntity = response.getEntity();
		final String result = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		final ObjectConverter converter = new GsonObjectConverter();
		final DescResponse resp = converter.fromString(result, DescResponse.class);
		final List<FragmentVO> fragments = resp.parseFragments();
		System.out.println(fragments);
	}
}
