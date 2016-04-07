package com.vdlm.spider.v2;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.http.HttpRequest;
import com.vdlm.spider.parser.config.ShopConfigs;

public class Utils {
		
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
    public static long SendRequest(String url,long pageId){    
    	try{
	    	/*
			  通知类型
			NONE  -不做通知
			URL   -HTTP URL方式通知第三方，默认该值
			FILE  -存到文件里，默认存放路径/ouer/data/results/{taskId}/{pageId}
			*/
			String notifyType = Config.instance().getNotifyType();		
			//提交URL
			String submitUrl =Config.instance().getSubmitUrl();
			//通知地址,回调该地址，传递爬取的数据，notifyType=FILE时必填		 
			String notifyUrl = Config.instance().getNotifyUrl();
			
			long taskId=0;	
			
			url = URLEncoder.encode(url,"UTF-8");
			
			String param =String.format("url=%s&pageId=%s&notifyType=%s&notifyUrl=%s",
				   url,
				   pageId,
				   notifyType,
				   notifyUrl);
	    	
	
	       //发送 POST 请求
		   String resultStr=HttpRequest.sendPost(submitUrl,param);
	       JSONObject resultJson=JSON.parseObject(resultStr);
	       String resultCode=resultJson.get("code").toString();	 	    
	       if(resultCode.equals("200")){
	    	   //正常	  
	    	   //任务ID
	    	   taskId = Long.parseLong(resultJson.get("result").toString());
	    	   // TODO 
	       }
	       else if(resultCode.equals("2003")){
	    	  //2003-重复提交	    	
	       }
	       else{
	    	   //未知错误    	   	    	
	       }  
	       return taskId;
	    }
    	catch(Exception e){    		
    	   LOG.error(e.getMessage());	
    	   return 0;
    	}    	
    }

    /**
	 * <pre>
	 * 根据店铺URL获取店铺类型
	 * </pre>
	 * @param url
	 * @return
	 */
	public static ShopType getShopTypeByUrl(String url) {
		// 以 http:// 开头
		final String shopUrl;
		if (url.startsWith(Statics.HTTP_URL_PREFIX)) {
			final int index = url.indexOf('/', Statics.HTTP_URL_PREFIX.length() + 1);
			if (index == -1) {
				shopUrl = url;
			}
			else {
				shopUrl = url.substring(0, index);
			}
		}
		else {
			final int index = url.indexOf('/');
			if (index == -1) {
				shopUrl = Statics.HTTP_URL_PREFIX + url;
			}
			else {
				shopUrl = Statics.HTTP_URL_PREFIX + url.substring(0, index);
			}
		}
		if (shopUrl.endsWith(ShopConfigs.getOrCreateTaobaoShopConfig().getUrlEndsWith())) {
			return ShopType.TAOBAO;
		}
		else if (shopUrl.endsWith(ShopConfigs.getOrCreateTmallShopConfig().getUrlEndsWith())) {
			return ShopType.TMALL;
		}
		else {
			return null;
		}
	}
}