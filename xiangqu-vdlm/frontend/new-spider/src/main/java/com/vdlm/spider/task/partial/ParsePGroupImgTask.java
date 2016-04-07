package com.vdlm.spider.task.partial;

import java.util.List;
import java.util.Set;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemListTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.parial.PItemGroupImgTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.event.PItemEvent;
import com.vdlm.spider.event.PItemEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.CrawlableTask;
import com.vdlm.spider.task.ParsableTask;
import com.vdlm.spider.task.partial.ParsePGroupImgTask.GroupImgInfo;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.parser.ApiStackValueParser;
import com.vdlm.spider.wdetail.parser.WItem;

/**
 *
 * @author: chenxi
 */

public class ParsePGroupImgTask extends ParsableTask<GroupImgInfo> {

	static class GroupImgInfo {
		private final PItemEvent event;
		private final Set<String> groupImgs;

		public GroupImgInfo(PItemEvent event, Set<String> groupImgs) {
			this.event = event;
			this.groupImgs = groupImgs;
		}

		public PItemEvent getEvent() {
			return event;
		}

		public Set<String> getGroupImgs() {
			return groupImgs;
		}
		
	}
	
	public ParsePGroupImgTask(BusSignalManager bsm, HttpClientInvoker invoker,
			HttpInvokeResult result, CrawlableTask<? extends ItemListTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected GroupImgInfo parse() {
		final PItemGroupImgTaskBean bean = (PItemGroupImgTaskBean) getBean();
		
		ItemResponse resp;
		try {
			resp = ObjectConvertUtils.fromString(result.getContentStringAndReset(), ItemResponse.class);
		} catch (final Exception e) {
			LOG.error("parse witem failed", e);
			return null;
		}
		final WItem wItem = ApiStackValueParser.parseItem(resp);
		if (wItem == null) {
			return null;
		}
		final PItemEvent event = new PItemEvent(PItemEventType.UPDATE_GROUP_IMG, bean.getItemId());
		event.getItemInfo().setGroupImgCnt(wItem.getGroupImgs().size());
		return new GroupImgInfo(event, wItem.getGroupImgs());
	}

	@Override
	protected void persistent(GroupImgInfo entity) {
		bsm.signal(entity.getEvent());
	}

	@Override
	protected void furtherCrawl(GroupImgInfo entity) {
		final Set<String> groupImgs = entity.getGroupImgs();
		final List<Img> imgs = ImgFactory.createWGroupImgs(groupImgs);
		final PItemGroupImgTaskBean bean = (PItemGroupImgTaskBean) getBean();		
		ImgTaskBean imgTask;
		for (final Img img : imgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, entity.event.getItemInfo().getId(), img);
			bsm.signal(imgTask);
		}
	}

}
