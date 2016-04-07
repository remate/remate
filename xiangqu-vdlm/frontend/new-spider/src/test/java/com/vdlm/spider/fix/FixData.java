package com.vdlm.spider.fix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author: chenxi
 */

public class FixData {

	public static void main(String[] args) throws IOException, InterruptedException {
//		File file = new File(args[0]);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(args[0]));
			String url = null;
			while ((url = br.readLine()) != null) {
				final HttpClient client = new DefaultHttpClient();  
				final HttpGet method = new HttpGet(url);
				final HttpResponse response = client.execute(method);
				final HttpEntity respEntity = response.getEntity();
				final String result = EntityUtils.toString(respEntity);
				EntityUtils.consume(respEntity);
				System.out.println(result);
				//Thread.currentThread().sleep(1000);
			}
		} catch (final IOException e) {
			throw e;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

}
