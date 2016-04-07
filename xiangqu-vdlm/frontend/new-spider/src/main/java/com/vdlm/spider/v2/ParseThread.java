package com.vdlm.spider.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.dao.TaskLogDao;
import com.vdlm.spider.store.ItemStore;
import com.vdlm.spider.store.ShopStore;

public class ParseThread extends Thread{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private long pageId;
	private JSONObject resultJson; 
	private ItemStore itemStore;
	private ShopStore shopStore;
	private TaskLogDao taskLogDao;
	
	public ParseThread(JSONObject resultJson,long pageId,ItemStore itemStore,ShopStore shopStore,TaskLogDao taskLogDao) 
	{ 
	  this.resultJson = resultJson;
	  this.pageId=pageId;
	  this.itemStore=itemStore;
	  this.shopStore=shopStore;
	  this.taskLogDao=taskLogDao;
	} 
	
	@Override
	public void run() 
	{ 		
			Boolean result=false;
			if(pageId == Config.instance().getTaobaoShopPageId() || pageId == Config.instance().getTmallShopPageId()){
				//解析shop
				ParseShopTask parseShop=new ParseShopTask(); 				 
				result = parseShop.Parse(resultJson,shopStore,taskLogDao);
				if(!result)
					log.error("parseShop error: resultJson->"+resultJson);	
			}
			else if(pageId == Config.instance().getTaobaoItemPageId() || pageId == Config.instance().getTmallItemPageId()){
				//解析item
				ParseItemTask parseItem=new ParseItemTask(); 				
				result = parseItem.Parse(resultJson,itemStore,taskLogDao);
				if(!result)
				    log.error("parseItem error: resultJson->"+resultJson);		
			} 
			else if(pageId == Config.instance().getTaobaoItemListPageId() || pageId == Config.instance().getTmallItemListPageId()){
				//解析itemList
				ParseItemListTask parseItemList=new ParseItemListTask(); 			
				result = parseItemList.Parse(resultJson,taskLogDao);
				if(!result)
				   log.error("parseItemList error: resultJson->"+resultJson);	
			}		
	} 
}
