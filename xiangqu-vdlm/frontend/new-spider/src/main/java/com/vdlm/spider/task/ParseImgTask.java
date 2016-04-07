package com.vdlm.spider.task;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.alibaba.fastjson.JSON;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.utils.Logs;

/**
 * // 解析img

void parseImg() {

    // 通过ParseImgThreadPool中的线程从池队列中取出一个task

    // 无需手工编写代码

    ParseImgTask task = getTaskFromThreadPoolQueue();

    if (task != null) {

       // 从task中取出http result内容

       String content = getContent(task);

       // 解析出img对象

       Img img = parse(content);

       // 上传到七牛云

       uploadToQiniu(img);

       // 持久化img

       save(img);

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

public class ParseImgTask extends ParsableTask<Img> {

	public ParseImgTask(BusSignalManager bsm, 
						  HttpClientInvoker invoker,
						  HttpInvokeResult result,
			   			  CrawlableTask<ImgTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
} 

	@Override
	protected Img parse() {
		final ImgTaskBean bean = (ImgTaskBean) getBean();		
		// if the flow comes here, we think the image missing is acceptable and continue to do what we can 
		if (result.getContent() == null /*&& result.getStatusCode() != 404*/) {
			Logs.unpredictableLogger.error("Failed to download img:" + JSON.toJSONString(bean.getImg()) + ", HttpInvokeResult:"
					+ result + ", proxy ip:" + invoker.getIp(), result.getException());
//			return bean.getImg();
		}
		// we do not upload img to qiniu when the status code is not 200 
		if ( 200 != result.getStatusCode()) { // 下载图片不存在不认为这是个错误,继续走流程
			final ItemEvent event = new ItemEvent(ItemEventType.TOLERANCE_IMG);
			event.setImgInput(null);
			event.setImg(bean.getImg());
			bsm.signal(event);
		} else {
			final ItemEvent event = new ItemEvent(ItemEventType.UPLOAD_IMG);
			final InputStream input = new ByteArrayInputStream(result.getContent());
			event.setImgInput(input);
			event.setImg(bean.getImg());
			bsm.signal(event);
		}
		return bean.getImg();
	}
	
	@Override
	protected void persistent(Img entity) {
		final ItemProcessEvent event = new ItemProcessEvent(ItemProcessEventType.IMG);
		event.setImg(entity);
		bsm.signal(event);
	}

	@Override
	protected void furtherCrawl(Img entity) {
		// TODO Auto-generated method stub
		
	}

}
