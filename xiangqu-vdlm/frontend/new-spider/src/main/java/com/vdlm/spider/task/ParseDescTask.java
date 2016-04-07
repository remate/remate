package com.vdlm.spider.task;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.entity.Desc.DescFragments;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.helper.DescParseHelper;

/**
 * // 解析desc

void parseDesc() {

    // 通过ParseDescThreadPool中的线程从池队列中取出一个task

    // 无需手工编写代码

    ParseDescTask task = getTaskFromThreadPoolQueue();

    if (task != null) {

       // 从task中取出http result内容

       String content = getContent(task);

       // 持久化desc

       save(desc);

       // 更新item_process表

       ItemProcess itemProcess = updateItemProcessAndGet(task);

       // 如果该item已全部爬完毕，则同步数据到vdlm?

       if (itemProcess.isCompleted()) {

           syncItemToVdlm();

           // 再做一些其他工作？

           cleanUp();

       }

    }

}
 * @author: chenxi
 */

public class ParseDescTask extends ParsableTask<Desc> {

	public ParseDescTask(BusSignalManager bsm, 
						   HttpClientInvoker invoker,
						   HttpInvokeResult result,
						   CrawlableTask<DescTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected Desc parse() {
		final DescTaskBean bean = (DescTaskBean) getBean();		
		final String details = DescParseHelper.formatDesc(result.getContentStringAndReset());
		final Desc desc = new Desc();
		desc.setItemId(bean.getItemTaskId());
		desc.setDescUrl(bean.getDescUrl());
		desc.setDetails(details);
		final List<FragmentVO> fragments = DescParseHelper.getDescFragments(bean, details);
		final DescFragments dfs = new DescFragments();
		dfs.setFragments(fragments);
		desc.setFragments(dfs);
		return desc;
	}

	@Override
	protected void persistent(Desc entity) {
		bsm.signal(entity);
		final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.DESC);
		event.setItemId(entity.getItemId());
		bsm.signal(event);
	}

	@Override
	protected void furtherCrawl(Desc entity) {
		final DescTaskBean bean = (DescTaskBean) getBean();		
		// 详情图片
		final Set<String> detailImgs = new LinkedHashSet<String>();
		final List<String> descImgs = DescParseHelper.getImgsFromString(entity.getDetails());
		detailImgs.addAll(descImgs);
		final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.UPDATE_DETAIL_IMG);
		event.setItemId(entity.getItemId());
		event.setDetailImgCount(detailImgs.size());
		bsm.signal(event);
		
		final List<Img> imgs = ImgFactory.createDetailImgs(bean, detailImgs, bean.getImgIndex());
		ImgTaskBean imgTask;
		for (final Img img : imgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, entity.getItemId(), img);
			bsm.signal(imgTask);
		}
	}

}
