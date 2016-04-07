package com.vdlm.spider.task.resubmit;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.bean.WItemTaskBean;

/**
 *
 * @author: chenxi
 */

public class TaskStatusHandlerFactory {

	private final Map<Class<? extends ItemListTaskBean>, TaskStatusHandler> handlers = Maps.newHashMap();
	
	public TaskStatusHandlerFactory(TaskStatusHandler shop,
										TaskStatusHandler itemlist, 
										TaskStatusHandler item) {
		handlers.put(ShopTaskBean.class, shop);
		handlers.put(ItemListTaskBean.class, itemlist);
		handlers.put(ItemTaskBean.class, item);
		handlers.put(WItemTaskBean.class, item);
	}
	
	public TaskStatusHandler getHandler(Class<? extends ItemListTaskBean> clazz) {
		return handlers.get(clazz);
	}
}
