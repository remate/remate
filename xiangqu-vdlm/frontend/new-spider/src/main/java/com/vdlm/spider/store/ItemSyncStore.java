package com.vdlm.spider.store;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ItemStatus;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.dao.DescDao;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.SkuDao;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.event.ItemCompletedEvent;

/**
 *
 * @author: chenxi
 */

public class ItemSyncStore implements BusSignalListener<ItemCompletedEvent> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ItemDao  itemDao;
	
	@Autowired
	private SkuDao skuDao;
	
	@Autowired
	private ImgDao imgDao;
	
	@Autowired
	private DescDao descDao;
	
	private SyncResultService syncResultService;
	
	@Autowired
	@Qualifier("syncResultService")
	public void setSyncResultService(SyncResultService syncResultService) {
		this.syncResultService = syncResultService;
	}

	public ItemSyncStore(BusSignalManager bsm) {
		bsm.bind(ItemCompletedEvent.class, this);
	}

	@Override
	public void signalFired(ItemCompletedEvent signal) {
		syncItem(signal.getItemId(), signal.getType() == null ? SpideItemType.ITEM : signal.getType(),
												   signal.getOption() == null ? 1 : signal.getOption());
	}
	
	private void syncItem(Long itemId, SpideItemType type, Integer option) {
		final Item item =  itemDao.queryOneItem(itemId);
		if (item == null)   {
			log.error("syncItem type:{} error. could not find item by itemId:[{}] from item_process", type.toString(), itemId);  
			return ;
		}
		itemDao.updateCompleted(1, item.getId());
		try {
			final List<Sku> skus = skuDao.querySkus(item.getId());
			final List<Img> imgs = imgDao.queryImgs(item.getId());
			final Desc desc = descDao.queryOneDesc(item.getId());
			if (desc != null) {
				syncResultService.syncItem(fillItemDesc(item, desc), skus, imgs, type, option);
			} else {
				syncResultService.syncItem(item, skus, imgs, type, option);
			}
			log.debug("syncItem success... itemId:[{}], type:[{}], option:[{}]", itemId, type.toString(), option);
		} catch (final Exception e) {
			log.error("syncItem error. exception happends. itemId:[{}] ", item.getId(), e);
			String desc = e.toString();
			if (e.getCause() != null) {
				desc += "Caused By:" + e.getCause().toString();
			}
			itemDao.updateCompleted(ItemStatus.FAILED.ordinal(), item.getId(), desc);
		}
	}
	
	private Item fillItemDesc(Item item, Desc desc) {
		final DescTaskBean bean = new DescTaskBean();
		bean.setOuerShopId(item.getOuerShopId());
		item.setFragments(desc.getFragments().getFragments());
		//item.setDetails(desc.getDescUrl());
		return item;
	}
	
	
}
