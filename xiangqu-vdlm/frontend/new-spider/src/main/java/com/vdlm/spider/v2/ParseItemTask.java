package com.vdlm.spider.v2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.TaskLogDao;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.entity.TaskLog;
import com.vdlm.spider.store.ItemStore;
import com.vdlm.spider.task.helper.ItemParseHelper;

public class ParseItemTask {
		 
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
    public Boolean Parse(JSONObject result,ItemStore itemStore,TaskLogDao taskLogDao){	
    	try{
	    	//任务ID
			long itemTaskId	= Long.parseLong(result.getString("taskId"));
			TaskLog itemTaskLog = taskLogDao.queryOneTaskLog(itemTaskId);	 
			if(itemTaskLog == null)
			{				   
				log.error("error: itemTaskLog is null; itemTaskId:"+itemTaskId);
				return false;
			}	
				
			List<Sku> skus;
			Item entity = new Item();	
			JSONArray itemNames,itemIds,itemUrls,shopIds,groupImgs,skuImgs,
			userIds,remoteSkuUrls,prices,descUrls,skuTypes,skuValues,skuTexts,skuMap;
			
			//item信息
			JSONObject item = result.getJSONArray("objs").getJSONObject(0);
		   
			//根据配置规则生成的结构化数据		 
		    itemNames =item.getJSONArray("itemName");	 
		    itemIds =item.getJSONArray("item_id");
		    itemUrls =item.getJSONArray("item_url");		    	
		    prices =item.getJSONArray("price");	 
		    userIds =item.getJSONArray("UserId");
		    shopIds =item.getJSONArray("ShopId");		    
		    groupImgs =item.getJSONArray("groupImgs");	
		    skuImgs =item.getJSONArray("skuImgs");			    
		    skuTypes =item.getJSONArray("skuTypes");
		    skuValues =item.getJSONArray("skuValues");
		    skuTexts =item.getJSONArray("skuTexts"); 
		    skuMap =item.getJSONArray("skuMap");  
		    
		    descUrls=item.getJSONArray("descUrl");
		    // TODO  解析远程DESC地址,返回html格式    		    
		    
		    //没有SKU 取远程默认的sku对象
	        if(skuMap==null){
	        	// TODO persist(item, null, imgs);
	        	//return true;
	        }  
	        
	        //商品是否下架 ，先根据skuValues为NULL 来判断下架
	        entity.setStatus(Statics.NORMAL);
	        if(skuMap!=null && (skuValues!=null && skuValues.size()>0)){
	        	if(StringUtils.isBlank(skuValues.getJSONObject(0).getString("skuValuesList")))
	        		entity.setStatus(Statics.SOLD_OUT);
	        }
	        else{
	        	//格式化skus
	        	if(skuMap!=null && skuMap.size()>0){
	  	           skus= createSkuList(skuTypes,skuValues,skuTexts);
	  	        }	        	
	        }   
	        
	        remoteSkuUrls=item.getJSONArray("remoteSkuUrl");
		    // TODO  解析远程sku地址	  
		  
		    
			//保存item		    	
		    entity.setOuerShopId(itemTaskLog.getOuer_user_id());
		    entity.setOuerUserId(itemTaskLog.getOuer_shop_id());
		    entity.setShopType(itemTaskLog.getShopType());
		    entity.setReqFrom(itemTaskLog.getReq_from());		    
		    if(itemNames!=null && itemNames.size()>0){
		       entity.setName(itemNames.getString(0));
		    }
		    if(itemIds!=null && itemIds.size()>0){
			   entity.setItemId(itemIds.getString(0));
			}
		    if(itemUrls!=null && itemUrls.size()>0){
			   entity.setItemUrl(itemUrls.getString(0));
			}
		    if(prices!=null && prices.size()>0){
		    	try {
			        entity.setPrice(Double.parseDouble(prices.getString(0)));
		    	}
		    	catch (final Exception ignore) {
		    		entity.setPrice(0.0);
		    	}
			}
		    else{
		    	entity.setPrice(0.0);	
		    }
			entity.setAmount(0);		
		    if(userIds!=null && userIds.size()>0){
			   entity.setUserId(userIds.getString(0));
			}
		    if(shopIds!=null && shopIds.size()>0){
			   entity.setShopId(shopIds.getString(0));
			}		 	
			itemStore.save(entity);		   
			if(log.isDebugEnabled()) {
				log.debug("ParseItem fulfil. itemName: {}", entity.getName());			 
		    } 	
			return true;	
    	} catch(Exception e){    		
      	    log.error(e.getMessage());	
      	    return false;
      	}  
   }

	protected void persist(Item item, List<Sku> skus, List<Img> imgs) {
		if (skus == null) {
			skus = new ArrayList<Sku>(1);
		}
		if (imgs == null) {
			imgs = new ArrayList<Img>(1);
		}

		// 下架商品
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			//this.service.save(item, skus, imgs);
			return;
		}

		// 校验价格、sku信息 并 入库
		Double maxPrice = null;
		Integer amount = 0;

		int nullIndex = 0;
		for (final Sku sku : skus) {
			if (sku.getAmount() == null) {
				sku.setAmount(99);
				++nullIndex;
			} else {
				amount += sku.getAmount();
			}

			if (sku.getPrice() == null) {
				if (item.getPrice() == null) {
					++nullIndex;
					sku.setPrice(0.0);
				} else {
					sku.setPrice(item.getPrice());
				}
			} else if (maxPrice == null || sku.getPrice() > maxPrice) {
				maxPrice = sku.getPrice();
			}
		}

		if (maxPrice != null) {
			item.setPrice(maxPrice);
		}
		if (item.getAmount() == null) {
			item.setAmount(amount);
		}

		// 判断 item 有效性
		if (nullIndex > 0) {
			item.setStatus(Statics.INCOMPLETED_INFO);

			if (item.getPrice() == null) {
				item.setPrice(0.0);
			}
		} else if (item.getStatus() == null) {
			item.setStatus(Statics.NORMAL);
		}
		if (CollectionUtils.isEmpty(skus)) {
			final Sku sku = new Sku();
			sku.setAmount(0);
			sku.setPrice(0.0);
			sku.setSpec("无");
			sku.setOrigSpec("");
			skus.add(sku);
		}

		if (item.getPrice() == null) {
			item.setPrice(0.0);
			item.setStatus(Statics.INCOMPLETED_INFO);
		}

		if (log.isDebugEnabled()) {
			log.debug("item >> {}", JSON.toJSONString(item));
			for (final Sku sku : skus) {
				log.debug("sku >> {}", JSON.toJSONString(sku));
			}
			for (final Img img : imgs) {
				log.debug("img >> {}", JSON.toJSONString(img));
			}
		}

		// 保存 item
		//this.service.save(item, skus, imgs);
	}

	//spider2.0格式化skulist
	List<Sku> createSkuList(JSONArray skuTypes,JSONArray skuValues, JSONArray skuTexts) {			
		int skuTextIndex=0;
		List<String> skuListTypes=new ArrayList<String>();
		List<List<String>> skuListValues = new ArrayList<List<String>>();
		List<List<String>> skuListTexts=new ArrayList<List<String>>();
		  	
	  	for (int k = 0; k < skuTypes.size(); k++) {
	  		skuListTypes.add(skuTypes.getJSONObject(k).getJSONArray("skuTypesList").getString(0));
	  	}	   
	  	
	  	Map<String, List<String>> skuMapValues=new LinkedHashMap<String, List<String>>();      
     	for (int j = 0; j < skuValues.size(); j++) {       		 
       		 String skuValueK=JSON.parseObject(skuValues.getString(j)).getJSONArray("skuValuesList").getString(0).split(":")[0];
       		 String skuValueV=JSON.parseObject(skuValues.getString(j)).getJSONArray("skuValuesList").getString(0).split(":")[1];       	
       		
       		 List<String> listSkuVal = skuMapValues.get(skuValueK);
       		 if (listSkuVal == null) {
       			listSkuVal = new ArrayList<String>();
                 skuMapValues.put(skuValueK, listSkuVal);
             }
       		 listSkuVal.add(skuValueV);       	
   		}   
     	for(Map.Entry<String, List<String>> entry: skuMapValues.entrySet()){            
            List<String> list=new ArrayList<String>();
            int skuValCount=0;
            for (int i = 0; i < entry.getValue().size(); i++) { 
            	list.add(entry.getKey()+":"+entry.getValue().get(i));
            	skuValCount+=1;
            }
            skuListValues.add(list);             
            list=new ArrayList<String>();
            for (int j = skuTextIndex; j < skuTextIndex+entry.getValue().size(); j++) {
            	list.add(skuTexts.getJSONObject(j).getJSONArray("skuTextsList").getString(0));
            }
            skuTextIndex=skuValCount;
            skuListTexts.add(list);
     	}        	
     	return ItemParseHelper.createSkuList(skuListTypes, skuListValues, skuListTexts); 		
	}
}
