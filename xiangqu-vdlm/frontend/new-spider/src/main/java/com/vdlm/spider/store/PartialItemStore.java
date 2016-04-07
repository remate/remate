package com.vdlm.spider.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemDao;
import com.vdlm.spider.dao.ItemListProcessDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.event.PItemEvent;
import com.vdlm.spider.event.PItemEvent.ItemInfo;
import com.vdlm.spider.event.PItemEventType;

/**
 *
 * @author: chenxi
 */

public class PartialItemStore implements BusSignalListener<PItemEvent> {

	private final static Logger LOG = LoggerFactory.getLogger(PartialItemStore.class);
	
	public PartialItemStore(BusSignalManager bsm) {
		bsm.bind(PItemEvent.class, this);
	}
	
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private ImgDao imgDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	@Autowired
	private ItemListProcessDao itemListProcessDao;
	
	@Transactional(rollbackFor = Exception.class)
	private void setItemInfo4Desc(String itemId, ItemInfo itemInfo) {
		final Long id = itemDao.exist(itemId);
		if (id == null) {
			LOG.warn("cannot find item with third id: {}", itemId);
			return;
		}
		itemInfo.setId(id);
		final int groupCnt = imgDao.queryImgCount(id, 1);
		final int skuCnt = imgDao.queryImgCount(id, 2);
		itemInfo.setGroupImgCnt(groupCnt);
		itemInfo.setSkuImgCnt(skuCnt);
		itemProcessDao.updateSpideType(id, SpideItemType.DESC);
		itemListProcessDao.decItemCount(id, SpideItemType.DESC);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void setItemInfo4Skus(ItemInfo itemInfo) {
		final Long id = itemInfo.getId();
		final int groupCnt = imgDao.queryImgCount(id, 1);
		itemInfo.setGroupImgCnt(groupCnt);
		itemProcessDao.updateSkuImgCount(id, itemInfo.getSkuImgCnt());
		itemListProcessDao.decItemCount(id, SpideItemType.SKUS);
	}
	
//	@Transactional(rollbackFor = Exception.class)
//	private void setItemInfo4Imgs(String itemId, ItemInfo itemInfo) {
//		final Long id = itemDao.exist(itemId);
//		itemInfo.setId(id);
//		imgDao.deleteList(id);
//		itemProcessDao.updateGroupSkuImgCount(id, itemInfo.getGroupImgCnt(), itemInfo.getSkuImgCnt());
//	}
	
	@Transactional(rollbackFor = Exception.class)
	private void setItemInfo4GroupImgs(String itemId, ItemInfo itemInfo) {
		final Long id = itemDao.exist(itemId);
		if (id == null) {
			LOG.warn("cannot find item with third id: {}", itemId);
			return;
		}
		itemInfo.setId(id);
		imgDao.deleteList(id, 1);
		itemProcessDao.updateGroupImgCount(id, itemInfo.getGroupImgCnt());
		itemListProcessDao.decItemCount(id, SpideItemType.GROUP_IMG);
	}
	
	@Override
	public void signalFired(PItemEvent signal) {
		if (PItemEventType.UPDATE_DESC.equals(signal.getType())) {
			setItemInfo4Desc(signal.getItemId(), signal.getItemInfo());
			return;
		}
//		if (PItemEventType.UPDATE_ALL_IMG.equals(signal.getType())) {
//			setItemInfo4Imgs(signal.getItemId(), signal.getItemInfo());
//			return;
//		}
		if (PItemEventType.UPDATE_GROUP_IMG.equals(signal.getType())) {
			setItemInfo4GroupImgs(signal.getItemId(), signal.getItemInfo());
			return;
		}
		if (PItemEventType.UPDATE_SKUS.equals(signal.getType())) {
			setItemInfo4Skus(signal.getItemInfo());
			return;
		}
	}

}
