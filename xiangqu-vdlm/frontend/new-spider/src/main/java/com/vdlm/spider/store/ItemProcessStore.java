package com.vdlm.spider.store;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListProcessDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.dao.ItemTaskDao;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.event.ItemCompletedEvent;
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

public class ItemProcessStore implements BusSignalListener<ItemProcessEvent> {

	private final static Logger LOG = LoggerFactory.getLogger(ItemProcessStore.class);

	@Autowired
	private ItemProcessDao itemProcessDao;
	@Autowired
	private ItemListProcessDao itemListProcessDao;
    @Autowired
    private BusSignalManager bsm;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ItemTaskDao itemTaskDao;
	
	private final Object pseudoLock = new Object();
	private final ConcurrentMap<Long, Object> itemLocks = Maps.newConcurrentMap();
	
	public ItemProcessStore(BusSignalManager bsm) {
		bsm.bind(ItemProcessEvent.class, this);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void initItemProcess(ItemProcess data) {
		final Long id = itemProcessDao.exist(data.getItemId());
		if (id == null) {
			itemProcessDao.insert(data);
			if (Logs.ITEM_PROCESS.isDebugEnabled()) {
				Logs.ITEM_PROCESS.debug("initItemProcess: insert:" + data.toString());
			}
		} else {
			itemProcessDao.update(data);
			if (Logs.ITEM_PROCESS.isDebugEnabled()) {
				Logs.ITEM_PROCESS.debug("initItemProcess: update:" + data.toString());
			}
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void reInitItemProcess(ItemProcess data) {
		itemProcessDao.update(data);
		itemListProcessDao.decItemCount(data.getItemId(), SpideItemType.MITEM);
		if (Logs.ITEM_PROCESS.isDebugEnabled()) {
			Logs.ITEM_PROCESS.debug("initItemProcess: update:" + data.toString());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void incrementImg(Img img) {
		if (img.getType() == 1) {
			itemProcessDao.incGroupImgCount(img.getItemId());
		}
		else if (img.getType() == 2) {
			itemProcessDao.incSkuImgCount(img.getItemId());
		}
		else if (img.getType() == 3) {
			itemProcessDao.incDetailImgCount(img.getItemId());
		}
		// FIXME avoid check everytime
		checkItemProcess(img.getItemId());
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void updateDetailImgCount(long itemId, int count) {
		itemProcessDao.updateDetailImgCount(itemId, count);
		if (count == 0) {
			checkItemProcess(itemId);
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void markDescParsed(long itemId, ItemProcess process) {
		itemProcessDao.setDescParsed(itemId);
		checkItemProcess(itemId);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void markSkuParsed(long itemId) {
		itemProcessDao.setSkuParsed(itemId);
		checkItemProcess(itemId);
	}
	
	private void checkItemProcess(final long itemId) {
		final ItemProcess itemProcess = itemProcessDao.queryOneItemProcess(itemId);
		if (itemProcess == null) {
			Logs.ITEM_PROCESS.error("no item process exists for item {}", itemId);
			return;
		}
		if (Logs.ITEM_PROCESS.isDebugEnabled()) {
			Logs.ITEM_PROCESS.debug("check item process {}", itemProcess.toString());
		}
		if (itemProcess.completed()) {
			final Object lock = itemLocks.putIfAbsent(itemId, pseudoLock);
			if (lock == null) {
				final TaskTrackEvent<ItemTaskBean> tte = 
						new TaskTrackEvent<ItemTaskBean>(QueueEventType.HANDLED, ItemTaskBean.class);
				tte.setHandledId(itemId);
				bsm.signal(tte);
				final ItemListProcessEvent event = new ItemListProcessEvent(ItemListProcessEventType.ITEM_COMPLETED);
				event.setShopId(itemProcess.getShopId());
				bsm.signal(event);
				bsm.signal(new ItemCompletedEvent( itemProcess.getItemId(), itemProcess.getType(), itemProcess.getOption()) );
				Logs.ITEM_PROCESS.info("executed item completed events for shopId: {}, itemId: {}", 
						itemProcess.getShopId(), itemId);
			}
		}
	}
	
	private void cleanLock(long itemId) {
		final Object lock = itemLocks.remove(itemId);
		if (lock != null) {
			Logs.ITEM_PROCESS.info("clean lock for item {}", itemId);
		} else {
			Logs.ITEM_PROCESS.warn("failed to clean lock for item {}", itemId);
		}
	}
	
	private void cleanLocks(long shopId) {
		final List<Long> itemIds = itemProcessDao.queryItemIds(shopId);
		Object lock;
		for (final Long itemId : itemIds) {
			lock = itemLocks.remove(itemId);
			if (lock != null) {
				Logs.ITEM_PROCESS.info("clean lock for shop {}, item {}", shopId, itemId);
			} else {
				Logs.ITEM_PROCESS.warn("failed to clean lock for shop {}, item {}", shopId, itemId);
			}
		}
	}
	
	@Override
	public void signalFired(ItemProcessEvent signal) {
		final ItemProcessEventType type = signal.getType();
		if (ItemProcessEventType.INIT.equals(type)) {
			initItemProcess(signal.getInitData());
			return;
		}
		if (ItemProcessEventType.REINIT.equals(type)) {
			reInitItemProcess(signal.getInitData());
			return;
		}
		if (ItemProcessEventType.DESC.equals(type)) {
			markDescParsed(signal.getItemId(), signal.getInitData());
			return;
		}
		if (ItemProcessEventType.IMG.equals(type)) {
			incrementImg(signal.getImg());
			return;
		}
		if (ItemProcessEventType.SKU.equals(type)) {
			markSkuParsed(signal.getItemId());
			return;
		}
		if (ItemProcessEventType.UPDATE_DETAIL_IMG.equals(type)) {
			updateDetailImgCount(signal.getItemId(), signal.getDetailImgCount());
			return;
		}
		if (ItemProcessEventType.CLEAN_LOCKS.equals(type)) {
			cleanLocks(signal.getCleanShopId());
			return;
		}
		if (ItemProcessEventType.CLEAN_LOCK.equals(type)) {
			cleanLock(signal.getItemId());
			return;
		}
	}

}
