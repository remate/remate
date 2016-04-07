package com.vdlm.proxycrawler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vdlm.proxycrawler.third.ThirdProxyConstants;

/**
 *
 * @author: chenxi
 */

public class TotalPagesTest {

	@Test
	public void testXiciNnPages() throws Exception {
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(ThirdProxyConstants.XICI_NN_URL);
		final HttpResponse response = client.execute(method);
		if (response.getStatusLine().getStatusCode() != 200) {
			return;
		}
		final HttpEntity respEntity = response.getEntity();
		final String content = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		
		final Document doc = Jsoup.parse(content);
		final Element pagination = doc.getElementsByAttributeValue("class", "pagination").first();
		final int aSize = pagination.select("a").size();
		final int totalPages = Integer.valueOf(pagination.select("a").get(aSize - 2).text());
		System.err.println(totalPages);
	}
	
	@Test
	public void testDaili5566Pages() throws Exception {
		final HttpClient client = new DefaultHttpClient();  
		final HttpGet method = new HttpGet(ThirdProxyConstants.DAILI5566_URL);
		final HttpResponse response = client.execute(method);
		if (response.getStatusLine().getStatusCode() != 200) {
			return;
		}
		final HttpEntity respEntity = response.getEntity();
		final String content = EntityUtils.toString(respEntity);
		EntityUtils.consume(respEntity);
		
		final Document doc = Jsoup.parse(content);
		final Element listnav = doc.getElementById("listnav");
		final Element a = listnav.select("a").last();
		final String lastPageStr = a.attr("href");
		final int totalPages = Integer.valueOf(lastPageStr.substring(6));
		System.err.println(totalPages);
	}
}
