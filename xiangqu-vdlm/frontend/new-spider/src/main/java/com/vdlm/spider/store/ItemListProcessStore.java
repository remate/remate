package com.vdlm.spider.store;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.ShopTaskBean;
import com.vdlm.spider.dao.ItemListProcessDao;
import com.vdlm.spider.dao.ItemListTaskDao;
import com.vdlm.spider.entity.ItemListProcess;
import com.vdlm.spider.event.ItemListProcessEvent;
import com.vdlm.spider.event.ItemListProcessEventType;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.task.resubmit.QueueEventType;
import com.vdlm.spider.task.resubmit.TaskTrackEvent;
import com.vdlm.spider.utils.Logs;

/**
 *
 * @author: chenxi
 */

public class ItemListProcessStore implements BusSignalListener<ItemListProcessEvent> {
	
    @Autowired
    private BusSignalManager bsm;
	@Autowired
	private ItemListProcessDao itemListProcessDao;
	@Autowired
	private ItemListTaskDao itemListTaskDao;
	
	private final Object pseudoLock = new Object();
	private final ConcurrentMap<Long, Object> shopLocks = Maps.newConcurrentMap();
	
	public ItemListProcessStore(BusSignalManager bsm) {
		bsm.bind(ItemListProcessEvent.class, this);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void initShopProcess(ItemListProcess data) {
		final Long id = itemListProcessDao.exist(data.getShopId());
		if (id == null) {
			itemListProcessDao.insert(data);
			if (Logs.ITEMLIST_PROCESS.isDebugEnabled()) {
				Logs.ITEMLIST_PROCESS.debug("initShopProcess: insert:" + data.toString());
			}
		} else {
			final ItemListProcess itemListProcess = itemListProcessDao.queryOneItemProcess(data.getShopId());
			if (itemListProcess.isPartially()) {
				itemListProcessDao.addItemCount(data.getShopId(), data.getItemCount());
			} else {
				itemListProcessDao.update(data);
			}
			if (Logs.ITEMLIST_PROCESS.isDebugEnabled()) {
				Logs.ITEMLIST_PROCESS.debug("initShopProcess: update:" + data.toString());
			}
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void incrementItemCount(long shopId) {
		itemListProcessDao.incItemCount(shopId);
		final ItemListProcess itemListProcess = itemListProcessDao.queryOneItemProcess(shopId);
		if (itemListProcess == null) {
			Logs.ITEMLIST_PROCESS.warn("no itemlist process exists for shop {}", shopId);
			return;
		}
		if (Logs.ITEMLIST_PROCESS.isDebugEnabled()) {
			Logs.ITEMLIST_PROCESS.debug(itemListProcess.toString());
		}
		if (itemListProcess.completed()) {
			final Object lock = shopLocks.putIfAbsent(shopId, pseudoLock);
			if (lock == null) {
				final TaskTrackEvent<ItemListTaskBean> itemlistEvent = 
						new TaskTrackEvent<ItemListTaskBean>(QueueEventType.HANDLED, ItemListTaskBean.class);
				itemlistEvent.setHandledId(shopId);
				bsm.signal(itemlistEvent);
				final TaskTrackEvent<ShopTaskBean> shopEvent = 
						new TaskTrackEvent<ShopTaskBean>(QueueEventType.HANDLED, ShopTaskBean.class);
				shopEvent.setHandledId(shopId);
				bsm.signal(shopEvent);
//				itemListTaskDao.deleteOne(shopId);
				final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.CLEAN_LOCKS);
				event.setCleanShopId(shopId);
				bsm.signal(event);
				shopLocks.remove(shopId);
				Logs.ITEMLIST_PROCESS.info("shop task has completed for shop {}", shopId);
			}
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void setPartially(long shopId, String ouerShopId, boolean partially) {
		itemListProcessDao.updatePartially(shopId, partially);
		itemListTaskDao.updatePartially(ouerShopId, partially);
	}
	
	@Override
	public void signalFired(ItemListProcessEvent signal) {
		final ItemListProcessEventType type = signal.getType();
		if (ItemListProcessEventType.INIT.equals(type)) {
			initShopProcess(signal.getInitData());
			return;
		}
		if (ItemListProcessEventType.ITEM_COMPLETED.equals(type)) {
			incrementItemCount(signal.getShopId());
			return;
		}
		if (ItemListProcessEventType.PARTIALLY.equals(type)) {
			setPartially(signal.getShopId(), signal.getOuerShopId(), signal.isPartially());
		}
	}

}
