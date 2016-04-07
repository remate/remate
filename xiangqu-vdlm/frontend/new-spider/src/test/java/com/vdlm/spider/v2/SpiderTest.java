package com.vdlm.spider.v2;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.http.HttpRequest;

public class SpiderTest {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Test
	public void test() {
	    long id=Config.instance().getTmallItemListPageId();	    
	    String url=  Config.instance().getTaobaoItemListUrl();
		
		String submitUrl = "http://10.8.100.139:30080/task/submit";
		String param = "url=http://item.taobao.com/item.htm?id=44411296005&pageId=6&notifyType=NONE&notifyUrl=http://localhost:12080/spider2/notify";
		 		
		//发送 GET 请求
        String s=HttpRequest.sendGet(submitUrl,param);          
        System.out.println(s);
        
        //发送 POST 请求
        String sr=HttpRequest.sendPost(submitUrl,param);
        System.out.println(sr);
        
       
                
	}

}
