package com.vdlm.spider.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.ItemTaskBeans;
import com.vdlm.spider.dao.ItemTaskDao;

/**
 *
 * @author: chenxi
 */

public class ItemListStore implements BusSignalListener<ItemTaskBeans> {
	
	@Autowired
	private ItemTaskDao itemTaskDao;
	
	public ItemListStore(BusSignalManager bsm) {
		bsm.bind(ItemTaskBeans.class, this);
	}
	
	private void saveItemTasks(List<ItemTaskBean> entities) {
		itemTaskDao.insert(entities);
	}
	
	@Override
	public void signalFired(ItemTaskBeans signal) {
		saveItemTasks(signal.getItems());
	}

}
