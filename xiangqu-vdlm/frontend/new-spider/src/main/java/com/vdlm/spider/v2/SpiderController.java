package com.vdlm.spider.v2;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.ReqFrom;
import com.vdlm.spider.ResponseObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.TaskLogDao;
import com.vdlm.spider.entity.TaskLog;
import com.vdlm.spider.store.ItemStore;
import com.vdlm.spider.store.ShopStore;

@Controller
@RequestMapping("/spider2")
public class SpiderController {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ItemStore itemStore;
	@Autowired
	private ShopStore shopStore;	
	@Autowired
	private TaskLogDao taskLogDao; 
	
	
	//提交shop任务
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/submitShop")
	public ResponseObject<Map<String, Object>> submitShopTask(
			@RequestParam(value = "ouer_user_id", required = true) String ouer_user_id,
			@RequestParam(value = "ouer_shop_id", required = true) String ouer_shop_id,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue,
			@RequestParam(value = "url", required = true) String url) throws Exception {
			
		 final Map<String, Object> result = new HashMap<String, Object>(5);
		 final ShopType shopType = Utils.getShopTypeByUrl(url);
		 final ReqFrom reqFrom = ReqFrom.valueOf(reqFromValue); 
		 
		 long pageId=0;		
		 if(pageId<=0){
		    if(shopType == ShopType.TAOBAO)
			    pageId=Config.instance().getTaobaoShopPageId();	    
		    else if(shopType == ShopType.TMALL)
		    	pageId=Config.instance().getTmallShopPageId();	    
		    else{
		    	 log.error("Unknown shopType."); 	
		    	 result.put("statusCode", 401);	
		    } 		 
		 } 		
		 //发起任务
		 long taskId= sendRequest(url,pageId,ouer_shop_id,ouer_user_id,reqFrom,shopType);
		 if(taskId > 0){
			 result.put("statusCode", 200);
			 result.put("taskId", taskId);
		 }
		 else if(taskId == -1){
			 result.put("statusCode", 501);		
		 }
		 else if(taskId == -2){
			 result.put("statusCode", 601);	
		 }		
		 return new ResponseObject<Map<String, Object>>(result);
	}	

	//提交item任务
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/submitItem")
	public ResponseObject<Map<String, Object>> submitItemTask(
			@RequestParam(value = "ouer_user_id", required = true) String ouer_user_id,
			@RequestParam(value = "ouer_shop_id", required = true) String ouer_shop_id,
			@RequestParam(value = "reqFrom", required = false, defaultValue = "1") Integer reqFromValue,
			@RequestParam(value = "url", required = true) String url) throws Exception {
					
		
		 final Map<String, Object> result = new HashMap<String, Object>(5);
		 final ShopType shopType = Utils.getShopTypeByUrl(url);
		 final ReqFrom reqFrom = ReqFrom.valueOf(reqFromValue); 
		 
		 long pageId=0;		 
	     if(shopType == ShopType.TAOBAO)
		    pageId=Config.instance().getTaobaoItemPageId();	    
	     else if(shopType == ShopType.TMALL)
	    	pageId=Config.instance().getTmallItemPageId();	    
	     else{
	    	log.error("Unknown shopType."); 	
	    	result.put("statusCode", 401);	
	     } 		 
		  		 
		 //发起任务
		 long taskId= sendRequest(url,pageId,ouer_shop_id,ouer_user_id,reqFrom,shopType);
		 if(taskId > 0){
			 result.put("statusCode", 200);
			 result.put("taskId", taskId);
		 }
		 else if(taskId == -1){
			 result.put("statusCode", 501);		
		 }
		 else if(taskId == -2){
			 result.put("statusCode", 601);	
		 }	
		 return new ResponseObject<Map<String, Object>>(result);
	}
	
	long sendRequest(String url,long pageId,String ouer_shop_id,String ouer_user_id,ReqFrom reqFrom,ShopType shopType)throws Exception{
		 //发起任务
		 long taskId = Utils.SendRequest(url,pageId);	
		 if(taskId > 0){						
			 TaskLog taskLog=new TaskLog();
			 taskLog.setOuer_shop_id(ouer_shop_id);
			 taskLog.setOuer_user_id(ouer_user_id);
			 taskLog.setReq_from(reqFrom);
			 taskLog.setShopType(shopType);			 
			 taskLog.setTaskId(taskId);
			 taskLog.setStatus(Statics.TASKLOG_UNFINISHED);
			 long logId= taskLogDao.insert(taskLog);	
			 if(logId > 0)
				 return taskId;
			 else{
				 log.error("taskLog insert error:"+taskLog.toString());
				 return -2;
			 }
		 }
		 else{			 
			 log.error("subMitTask error. url:"+url+";pageId:"+pageId);
			 return -1;		 
		 } 
	}	
	
	//回调通知
	@ResponseBody
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = "/notify")
	public ResponseObject<Map<String, Object>> notifyTask(@RequestBody String json) throws Exception {		
		//if (log.isDebugEnabled()) {
			//log.debug("result_json >> {}", json);			 
		//}
		final Map<String, Object> result = new HashMap<String, Object>(5);
		try{
			JSONObject resultJson = JSON.parseObject(json);
			//页面标识	
			long pageId	=Long.parseLong(resultJson.getString("pageId"));	
			//任务ID
			long taskId	= Long.parseLong(resultJson.getString("taskId"));
			//是否任务结束
			Boolean isEnd = Boolean.parseBoolean(resultJson.getString("isEnd"));
		    if(isEnd){		    	 
		    	 TaskLog taskLog=new TaskLog();		
				 taskLog.setStatus(Statics.TASKLOG_FULFIL);
				 taskLog.setTaskId(taskId); 
				 if(taskLogDao.update(taskLog) > 0){		
					 result.put("statusCode", 200);
					 result.put("taskId", taskId);						
				 }
				 else{
					 log.error("taskLog update error:"+taskLog.toString());
					 result.put("statusCode", 501);	 
				 }
		    }
		    else{			        
		    	Thread thread = new ParseThread(resultJson,pageId,itemStore,shopStore,taskLogDao);
				thread.start();	
		    }   
		}
		catch (Exception e) {
			log.error(e.getMessage());
			result.put("statusCode", 301);	 
		}	
		return new ResponseObject<Map<String, Object>>(result);
	}	
}
