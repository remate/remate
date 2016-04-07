/**
 * 
 */
package com.vdlm.spider.store;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.dao.xiangqu.XiangquItemDao;
import com.vdlm.spider.dao.xiangqu.XiangquShopDao;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.Sku;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:38:07 PM Aug 7, 2014
 */
@Component
public class SyncResultToXiangquService implements SyncResultService {

	private XiangquShopDao xiangquShopDao;
	private XiangquItemDao xiangquItemDao;

	@Autowired
	public void setXiangquShopDao(XiangquShopDao xiangquShopDao) {
		this.xiangquShopDao = xiangquShopDao;
	}

	@Autowired
	public void setXiangquItemDao(XiangquItemDao xiangquItemDao) {
		this.xiangquItemDao = xiangquItemDao;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncItem(Item item, List<Sku> skus, List<Img> imgs, SpideItemType type, Integer option) throws Exception {
		// 仅更新状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			this.xiangquItemDao.update(item.getShopId(), item.getItemId(), 1);
			return;
		}

		// 设置图片 ////////////////////////////////
		if (CollectionUtils.isEmpty(imgs)) {
			item.setImgs(StringUtils.EMPTY);
		}
		else {
			int size = 0;
			for (final Img img : imgs) {
				size += img.getImgUrl().length();
			}
			final StringBuilder sb = new StringBuilder(size);
			final int lastIndex = imgs.size() - 1;
			for (int i = 0; i < lastIndex; i++) {
				sb.append(imgs.get(i).getImgUrl()).append(';');
			}
			sb.append(imgs.get(lastIndex).getImgUrl());

			item.setImgs(sb.toString());
		}
		//////////////////////////////////////////////

		if (item.getId() == null) {
			// 新增
			xiangquItemDao.insert(item);
		}
		else {
			// 更新
			xiangquItemDao.update(item);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncShop(Shop shop) throws Exception {
		if (shop.getId() == null) {
			// 新增
			xiangquShopDao.insert(shop);
		}
		else {
			// 更新
			xiangquShopDao.update(shop);
		}
	}

}
