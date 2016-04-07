package com.vdlm.spider.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.TaskLogDao; 
import com.vdlm.spider.entity.TaskLog; 

public class ParseItemListTask { 
	 
	protected final Logger log = LoggerFactory.getLogger(getClass());
	 
    public Boolean Parse(JSONObject result,TaskLogDao taskLogDao) {
       try{
	    	//ItemList任务ID
		    final long ItemListTaskId = Long.parseLong(result.getString("taskId")); 	
			 
		    TaskLog ItemListTaskLog = taskLogDao.queryOneTaskLog(ItemListTaskId);
			if(ItemListTaskLog == null)
			{				   
				log.error("error: ItemListTaskLog is null; ItemListTaskId:"+ItemListTaskId);
				return false;
			}	
			
			long itemPageId;  
		    if(ItemListTaskLog.getShopType()==ShopType.TAOBAO)
		    	itemPageId=Config.instance().getTaobaoItemPageId();	    
		    else if(ItemListTaskLog.getShopType()==ShopType.TMALL)
		    	itemPageId=Config.instance().getTmallItemPageId();  
		    else{
		    	log.error("Unknown shopType."); 	
		    	return false;
		    } 		
			 
		    long itemTaskId,itemListTaskLogId;
		    TaskLog itemTaskLog;
		    final JSONArray itemUrls = result.getJSONArray("objs"); 
		    if(log.isErrorEnabled()){
		    	log.debug("parseItemList fulfil.");
				log.debug("itemListSize:"+itemUrls.size());
		    }
			for(int i = 0; i < itemUrls.size(); i++){  	
			    //发起爬item任务  
				//if(log.isErrorEnabled()){				 
		        	//log.debug("itemUrl >> {}", itemUrls.getJSONObject(i).getJSONArray("link").getString(0));
		        	//log.debug("itemListUrl >> {}", itemUrls.getJSONObject(i).getJSONArray("url").getString(0));
		    	//}	
			    itemTaskId = Utils.SendRequest(itemUrls.getJSONObject(i).getJSONArray("link").getString(0), itemPageId);		
				if(itemTaskId > 0){
				    itemTaskLog = new TaskLog();
				    itemTaskLog.setOuer_shop_id(ItemListTaskLog.getOuer_shop_id());
				    itemTaskLog.setOuer_user_id(ItemListTaskLog.getOuer_user_id());
				    itemTaskLog.setReq_from(ItemListTaskLog.getReq_from());
				    itemTaskLog.setShopType(ItemListTaskLog.getShopType());			 
				    itemTaskLog.setTaskId(itemTaskId);
				    itemTaskLog.setStatus(Statics.TASKLOG_UNFINISHED);
			        itemListTaskLogId = taskLogDao.insert(itemTaskLog);	
					if(itemListTaskLogId <= 0){			 
						 log.error("itemTaskLog insert error. entity:"+itemTaskLog.toString()); 
					} 
			    }
				else{
					 log.error("submit itemTask error. itemUrl:"+itemUrls.getString(i)+";itemPageId:"+itemPageId); 
				}
			} 
			return true;
       }catch(Exception e){    		
    	   log.error(e.getMessage());	
    	   return false;
    	}  
    }
}
