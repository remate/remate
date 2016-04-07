package com.vdlm.spider.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.vdlm.common.bus.BusSignalListener;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.dao.DescDao;
import com.vdlm.spider.dao.ImgDao;
import com.vdlm.spider.dao.ItemProcessDao;
import com.vdlm.spider.entity.Desc;

/**
 *
 * @author: chenxi
 */

public class DescStore implements BusSignalListener<Desc> {

	private final static Logger LOG = LoggerFactory.getLogger(DescStore.class);
	
	@Autowired
	private DescDao descDao;
	@Autowired
	private ImgDao imgDao;
	@Autowired
	private ItemProcessDao itemProcessDao;
	
	public DescStore(BusSignalManager bsm) {
		bsm.bind(Desc.class, this);
	}
	
	@Transactional(rollbackFor = Exception.class)
	private void saveDesc(Desc desc) throws Exception {
		final Long id = descDao.exist(desc.getItemId());
		if (id == null) {
			descDao.insert(desc);
		} else {
			descDao.update(desc);
			imgDao.deleteList(desc.getItemId(), 3);
			itemProcessDao.resetDescParsed(desc.getItemId());
		}
	}
	
	@Override
	public void signalFired(Desc signal) {
		try {
			saveDesc(signal);
		} catch (final Exception e) {
			LOG.error("failed to save desc", e);
		}
	}

}
