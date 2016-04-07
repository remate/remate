package com.vdlm.spider.task;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.bean.WDescTaskBean;
import com.vdlm.spider.entity.Desc;
import com.vdlm.spider.entity.Desc.DescFragments;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.task.helper.DescParseHelper;
import com.vdlm.spider.utils.ObjectConvertUtils;
import com.vdlm.spider.wdetail.DescResponse;

/**
 *
 * @author: chenxi
 */

public class ParseWDescTask extends ParsableTask<Desc> {

	private final static String FULL_DESC_API_MARKUP = "mtop.wdetail.getItemFullDesc";
	private final static String BRIEF_DESC_API_MARKUP = "mtop.wdetail.getItemDescx";
	
	private boolean useBriefApi = false;
	
	public ParseWDescTask(BusSignalManager bsm, HttpClientInvoker invoker,
			HttpInvokeResult result, CrawlableTask<WDescTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
		if (!StringUtils.contains(getBean().getRequestUrl(), FULL_DESC_API_MARKUP)) {
			useBriefApi = true;
		}
	}
	
	@Override
	protected Desc parse() {
		final WDescTaskBean bean = (WDescTaskBean) getBean();		
		DescResponse resp;
		try {
			resp = ObjectConvertUtils.fromString(result.getContentStringAndReset(), DescResponse.class);
		} catch (final Exception e) {
			LOG.error("parse witem failed", e);
			return null;
		}
		
		final Desc desc = new Desc();
		
		String details = null;
		List<FragmentVO> fragments = null; 
		if (useBriefApi) {
			fragments = DescParseHelper.parseFragments(bean, resp.getData().getPages());
			desc.setImgs(resp.getData().getImages());
		}
		else {
			details = DescParseHelper.formatDesc(resp.getData().getDesc());
			fragments = DescParseHelper.getDescFragments(bean, details);
			if (fragments.isEmpty() || fragments.get(0) == null) {
				final String requestUrl = StringUtils.replace(bean.getRequestUrl(), 
						FULL_DESC_API_MARKUP, BRIEF_DESC_API_MARKUP);
				bean.setRequestUrl(requestUrl);
				bsm.signal(bean);
				return null;
			}
		}
		desc.setItemId(bean.getItemTaskId());
		desc.setDescUrl(bean.getDescUrl());
		desc.setDetails(details);
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
		final WDescTaskBean bean = (WDescTaskBean) getBean();		
		// 详情图片
		final Set<String> detailImgs = new LinkedHashSet<String>();
		List<String> descImgs = null;
		if (useBriefApi) {
			descImgs = entity.getImgs();
		} else {
			descImgs = DescParseHelper.getImgsFromString(entity.getDetails());
		}
		detailImgs.addAll(descImgs);
		final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.UPDATE_DETAIL_IMG);
		event.setItemId(entity.getItemId());
		event.setDetailImgCount(detailImgs.size());
		bsm.signal(event);
		
		final List<Img> imgs = ImgFactory.createWDetailImgs(bean, detailImgs, bean.getImgIndex());
		ImgTaskBean imgTask;
		for (final Img img : imgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, entity.getItemId(), img);
			bsm.signal(imgTask);
		}
	}

}
