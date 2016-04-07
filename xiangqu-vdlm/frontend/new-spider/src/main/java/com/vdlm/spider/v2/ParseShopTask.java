package com.vdlm.spider.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.TaskLogDao;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.TaskLog;
import com.vdlm.spider.store.ShopStore;

public class ParseShopTask { 
	 
	protected final Logger log = LoggerFactory.getLogger(getClass());
	 
    public Boolean Parse(JSONObject result,ShopStore shopStore,	TaskLogDao taskLogDao) {
    	try{
	    	//shop任务ID
		    final long shopTaskId = Long.parseLong(result.getString("taskId")); 		 	  
		   
		    TaskLog shopTaskLog = taskLogDao.queryOneTaskLog(shopTaskId);
			if(shopTaskLog == null)
			{				   
				log.error("error: shopTaskLog is null; shopTaskId:"+shopTaskId);
				return false;
			}	
			
			long itemListPageId; 
			String itemListUrl;
		    if(shopTaskLog.getShopType()==ShopType.TAOBAO){
		    	 itemListPageId=Config.instance().getTaobaoItemListPageId();
		    	 itemListUrl=Config.instance().getTaobaoItemListUrl();
		    }
		    else if(shopTaskLog.getShopType()==ShopType.TMALL){
		    	 itemListPageId=Config.instance().getTmallItemListPageId();	   
		    	 itemListUrl=Config.instance().getTmallItemListUrl();
		    }
		    else{
		    	log.error("Unknown shopType."); 	
		    	return false;
		    } 	
		   
			//店铺信息
	        JSONObject shop = result.getJSONArray("objs").getJSONObject(0);
		    //根据配置规则生成的结构化数据	
	        JSONArray userIds,shopIds,shopUrls,shopNames,nickNames,scoresMSs,scoresFWs,scoresWLs;	  
	        userIds = shop.getJSONArray("user_id");
		    shopIds = shop.getJSONArray("shop_id");
		    shopUrls = shop.getJSONArray("shop_url");		   
		    shopNames = shop.getJSONArray("name");
		    nickNames = shop.getJSONArray("nickname");
		    scoresMSs = shop.getJSONArray("score_miaoshu");	
		    scoresFWs = shop.getJSONArray("score_miaoshu");
		    scoresWLs = shop.getJSONArray("score_miaoshu");
		   				
		    //保存shop信息
		    Shop entity=new Shop();
		    entity.setOuerShopId(shopTaskLog.getOuer_shop_id());
		    entity.setOuerUserId(shopTaskLog.getOuer_user_id());
		    entity.setReqFrom(shopTaskLog.getReq_from());
		    entity.setShopType(shopTaskLog.getShopType());		   
		    if(userIds!=null && userIds.size()>0){
			       entity.setUserId(userIds.getString(0));
			}		   
		    if(shopIds!=null && shopIds.size()>0){
			       entity.setShopId(shopIds.getString(0));
			}
		    if(shopUrls!=null && shopUrls.size()>0){
			       entity.setShopUrl(shopUrls.getString(0));
			}
		    if(shopNames!=null && shopNames.size()>0){
			       entity.setName(shopNames.getString(0));
			}
		    if(nickNames!=null && nickNames.size()>0){
			       entity.setNickname(nickNames.getString(0));
			}
		    if(scoresMSs!=null && scoresFWs!=null&& scoresWLs!=null){
			       entity.setScore(scoresMSs.getString(0)+"-"+scoresFWs.getString(0)+"-"+scoresWLs.getString(0));
			} 
		    shopStore.save(entity);			    
		    if(log.isDebugEnabled()) {
				log.debug("ParseShop fulfil. shopName: {}", entity.getName());			 
		    }
		    
		    if(entity.getId()>0){	    
			    //发起爬itemlist任务 
			    itemListUrl = entity.getShopUrl()+itemListUrl;
			    if(log.isDebugEnabled()){
		        	//log.debug("itemListUrl >> {}", itemListUrl);
		        	//log.debug("itemListPageId >> {}", itemListPageId);
			    	log.debug("submit itemListTask. itemListUrl: {},itemListPageId:{}", itemListUrl,itemListPageId);	
		        } 
		        long itemListTaskId = Utils.SendRequest(itemListUrl, itemListPageId);	
			    if(itemListTaskId > 0){
			    	TaskLog itemListTaskLog = new TaskLog();
			        itemListTaskLog.setOuer_shop_id(shopTaskLog.getOuer_shop_id());
			        itemListTaskLog.setOuer_user_id(shopTaskLog.getOuer_user_id());
			        itemListTaskLog.setReq_from(shopTaskLog.getReq_from());
			        itemListTaskLog.setShopType(shopTaskLog.getShopType());			 
			        itemListTaskLog.setTaskId(itemListTaskId);
			        itemListTaskLog.setStatus(Statics.TASKLOG_UNFINISHED);
			        long itemListTaskLogId = taskLogDao.insert(itemListTaskLog);	
					if(itemListTaskLogId <= 0){			 
						 log.error("itemListTaskLog insert error. entity:"+itemListTaskLog.toString()); 
					}  
			    }
			    else{
			    	log.error("submit itemListTask error. itemListUrl:"+itemListUrl+"; itemListPageId:"+itemListPageId); 
			    	return false;	
			    }			
		    }
		    else{
		    	log.error("shopInfo save error. entity->"+entity.toString()); 
		    	return false;
		    }	
		    return true;
    	}catch(Exception e){    		
      	    log.error(e.getMessage());	
      	    return false;
      	}  
    }
}
