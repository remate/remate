package com.vdlm.spider.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;

import com.alibaba.fastjson.JSON;
import com.vdlm.common.bus.BusSignalManager;
import com.vdlm.spider.ItemStatus;
import com.vdlm.spider.ShopType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.bean.DescTaskBean;
import com.vdlm.spider.bean.ImgTaskBean;
import com.vdlm.spider.bean.ItemTaskBean;
import com.vdlm.spider.bean.SkuTaskBean;
import com.vdlm.spider.bean.TaskBeanFactory;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.ImgFactory;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.ItemProcess;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.event.ItemEvent;
import com.vdlm.spider.event.ItemEventType;
import com.vdlm.spider.event.ItemProcessEvent;
import com.vdlm.spider.event.ItemProcessEventType;
import com.vdlm.spider.htmlparser.HtmlParserProvider;
import com.vdlm.spider.http.HttpClientInvoker;
import com.vdlm.spider.http.HttpInvokeResult;
import com.vdlm.spider.parser.NodeFilters;
import com.vdlm.spider.parser.ParserUtils;
import com.vdlm.spider.parser.Props;
import com.vdlm.spider.parser.config.ParserConfig;
import com.vdlm.spider.parser.config.ParserConfigProviders;
import com.vdlm.spider.task.ParseItemTask.ItemWrapper;
import com.vdlm.spider.task.helper.ItemParseHelper;
import com.vdlm.spider.utils.Logs;

/**
 * // 解析item

void parseItem() {

    // 通过ParseItemThreadPool中的线程从池队列取出一个task

    // 无需手工编写代码

    ParseItemTask task = getTaskFromThreadPoolQueue();

    if (task != null) {

       // 从task中取出http result内容

       String content = getContent(task);

       // 解析出item对象

       Item Item = parse(content);

       // 持久化item

       save(item);

       // 取出sku数量、地址，desc地址，img数量、地址

       List<String> skuList = extractSkuList (content);

       String descUrl = extractDescUrl(content);

       List<String> imgList = extractImgList(content);

       // 持久化数量信息

       /*

        * 可以新增一张item_process表，表中字段如下：

        * id,item_id,sku_size,desc_size,img_size,completed_sku,

        * completed_desc, completed_img，当sku_size == completed_sku

        * && desc_size == completed_desc && img_size == completed_img        * 代表该item全部爬成功

        */
/**
       save(item, skuList.size(), 1, imgList.size());

       // 为每一个sku、desc、img创建相应的item请求任务，并置入相应队       // 列

       for (String sku : skuList) {

           CrawlSkuTask task1 = createCrawlSkuTask(sku);

           CrawlSkuQueue.add(task1);

       }

       CrawlDescTask task2 = createCrawlDescTask(sku);

       CrawlDescQueue.add(task2);

       for (String img : imgList) {

           CrawlImgTask task3 = createCrawlImgTask(sku);

           CrawlImgQueue.add(task3);

       }

    }

}
 * @author: chenxi
 */

public class ParseItemTask extends ParsableTask<ItemWrapper> {

	static class ItemWrapper {
		
		private final Item item;
		private final String apiDescUrl;
		private final Set<String> groupImgs;
		private final Set<String> skuImgs;		
		private final List<Sku> skus;
		private final String skuUrl;
		private final Map<String, Sku> skuStocks;
		private final Map<String, Sku> skuPrices;
		
		private String skuReferUrl;
		private String skuReferIp;
		private String skuUserAgent;
		
		public ItemWrapper(Item item, String apiDescUrl, Set<String> groupImgs, Set<String> skuImgs,
				String skuUrl,List<Sku> skus,Map<String, Sku> skuStocks,Map<String, Sku> skuPrices) {
			this.item = item;
			this.apiDescUrl = apiDescUrl;
			this.groupImgs = groupImgs;
			this.skuImgs = skuImgs;	
			this.skuUrl=skuUrl;
			this.skus=skus;
			this.skuStocks=skuStocks;
			this.skuPrices=skuPrices;
	    }		

		public Map<String, Sku> getSkuStocks() {
			return skuStocks;
		}

		public Map<String, Sku> getSkuPrices() {
			return skuPrices;
		}

		public List<Sku> getSkus() {
			return skus;
		}

		public String getSkuUrl() {
			return skuUrl;
		}

		public Item getItem() {
			return item;
		}

		public String getApiDescUrl() {
			return apiDescUrl;
		}

		public Set<String> getGroupImgs() {
			return groupImgs;
		}

		public Set<String> getSkuImgs() {
			return skuImgs;
		}

		public String getSkuReferUrl() {
			return skuReferUrl;
		}

		public void setSkuReferUrl(String skuReferUrl) {
			this.skuReferUrl = skuReferUrl;
		}

		public String getSkuReferIp() {
			return skuReferIp;
		}

		public void setSkuReferIp(String skuReferIp) {
			this.skuReferIp = skuReferIp;
		}

		public String getSkuUserAgent() {
			return skuUserAgent;
		}

		public void setSkuUserAgent(String skuUserAgent) {
			this.skuUserAgent = skuUserAgent;
		}
	}
	
	public ParseItemTask(BusSignalManager bsm, 
						   HttpClientInvoker invoker,
						   HttpInvokeResult result,
						   CrawlableTask<ItemTaskBean> retriable) {
		super(bsm, invoker, result, retriable);
	}

	@Override
	protected ItemWrapper parse() {		
		final ItemTaskBean bean = (ItemTaskBean) getBean();		
		final Item item = new Item();
		item.setOuerUserId(bean.getOuerUserId());
		item.setOuerShopId(bean.getOuerShopId());
		item.setShopType(bean.getShopType());
		item.setItemUrl(bean.getItemUrl());
		item.setItemId(bean.getItemId());
		item.setReqFrom(bean.getReqFrom());		
		// 页面标题
		String title = null;		
		// sku信息
		final List<String> skuTypes = new ArrayList<String>(20);
		final List<List<String>> skuValues = new ArrayList<List<String>>();
		final List<List<String>> skuTexts = new ArrayList<List<String>>();
		final Set<String> skuImgs = new LinkedHashSet<String>();
		// 组图
		final Set<String> groupImgs = new LinkedHashSet<String>();
		final Set<String> names = new HashSet<String>(5);
		final Set<String> prices = new HashSet<String>(5);
		final Set<String> userIds = new HashSet<String>(5);
		final Set<String> shopIds = new HashSet<String>(5);
		//状态  -正常-item信息需要补全-下架
		final Set<String> soldOuts = new HashSet<String>(5);
		final Parser parser = HtmlParserProvider.createParser(result.getContentString());
		try {
			final Map<String, ParserConfig> configs = ParserConfigProviders.getParserItemConfigs(bean);
			// 获取配置
			final NodeFilter filter = NodeFilters.getOrCreateNodeFilter(configs);
			final NodeList nodeList = parser.extractAllNodesThatMatch(filter);

			// 啥都没有
			if (nodeList == null || nodeList.size() == 0) {
				// 如果是认证页面，则重试
				/*if (this.isAuthHtml(htmlContent)) {
					this.provider.disable(this.invoker);
					this.retry(true);
					return RETRY;
				} else {
					Logs.maybeChangedRuleLogger.warn("Nothing to parse item.htm, maybe invalid ParseRule" + bean);
					logStatistics(Logs.StatsResult.FAIL, bean);
					//this.retry(true);
					//return RETRY;
				}*/
				logStatistics(Logs.StatsResult.FAIL, bean);
				return null;
			}
			
			List<ParserConfig> matchConfigs;
			for (int a = 0; a < nodeList.size(); a++) {
				final Node aNode = nodeList.elementAt(a);
				// 网页title
				if (aNode instanceof TitleTag) {
					title = ((TitleTag) aNode).getStringText();
					continue;
				}
				// item名称
				matchConfigs = ParserUtils.match(aNode, Props.NAME, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					names.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				} 
				// sku 类型
				matchConfigs = ParserUtils
						.match(aNode, Props.SKU_TYPE, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					final List<String> types = ParserUtils.getValues(aNode,
							matchConfigs, configs);
					if (CollectionUtils.isEmpty(types)) {
						continue;
					}

					final NodeList skuNodeList = aNode.getChildren();
					if (skuNodeList == null || skuNodeList.size() == 0) {
						continue;
					}

					final List<ParserConfig> destConfigs = new ArrayList<ParserConfig>();
					for (final Map.Entry<String, ParserConfig> entry : configs
							.entrySet()) {
						if (StringUtils.equals(entry.getValue().getProp(),
								Props.SKU_VALUE)) {
							destConfigs.add(entry.getValue());
						} else if (StringUtils.equals(entry.getValue()
								.getProp(), Props.SKU_TEXT)) {
							destConfigs.add(entry.getValue());
						} else if (StringUtils.equals(entry.getValue()
								.getProp(), Props.SKU_IMG)) {
							destConfigs.add(entry.getValue());
						}
					}

					final NodeList nodeList2;
					try {
						nodeList2 = skuNodeList.extractAllNodesThatMatch(
								NodeFilters.getOrCreateNodeFilter(destConfigs),
								true);
					} catch (final Exception ignore) {
						continue;
					}
					if (nodeList2 == null || nodeList2.size() == 0) {
						continue;
					}

					final List<String> values = new ArrayList<String>();
					final List<String> texts = new ArrayList<String>();

					for (int i = 0; i < nodeList2.size(); i++) {
						final Node n = nodeList2.elementAt(i);

						matchConfigs = ParserUtils.match(n, Props.SKU_VALUE,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							values.addAll(ParserUtils.getValues(n,
									matchConfigs, configs));
						}

						matchConfigs = ParserUtils.match(n, Props.SKU_TEXT,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							texts.addAll(ParserUtils.getValues(n, matchConfigs,
									configs));
						}

						matchConfigs = ParserUtils.match(n, Props.SKU_IMG,
								configs);
						if (CollectionUtils.isNotEmpty(matchConfigs)) {
							skuImgs.addAll(ParserUtils.getValues(n,
									matchConfigs, configs));
						}
					}

					// 解析出问题了
					if (values.size() != texts.size() || values.isEmpty()) {
						Logs.maybeChangedRuleLogger
								.error("Error to Parse sku info from item.htm, maybe invalid ParseRule:{}",
										bean);
						continue;
					}

					if (skuTypes.add(types.get(0))) {
						skuValues.add(values);
						skuTexts.add(texts);
					}
				}
				// 价格
				matchConfigs = ParserUtils.match(aNode, Props.PRICE, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					prices.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}
				// 图片
				matchConfigs = ParserUtils.match(aNode, Props.IMG, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					groupImgs.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}
				// 是否下架
				matchConfigs = ParserUtils
						.match(aNode, Props.SOLD_OUT, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					soldOuts.addAll(ParserUtils.getValues(aNode, matchConfigs,
							configs));
				}
				// userId
				matchConfigs = ParserUtils.match(aNode, Props.USER_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode,
								matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								userIds.add(value.trim());
							} else {
								final String[] kw = matchConfig.getExtractKw()
										.split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value
											.indexOf(kw[1], start);
									if (stop > 0) {
										userIds.add(value.substring(
												start + kw[0].length(), stop)
												.trim());
									} else {
										userIds.add(value.substring(start
												+ kw[0].length()));
									}
								}
							}
						}
					}
				}
				// shopId
				matchConfigs = ParserUtils.match(aNode, Props.SHOP_ID, configs);
				if (CollectionUtils.isNotEmpty(matchConfigs)) {
					for (final ParserConfig matchConfig : matchConfigs) {
						for (final String value : ParserUtils.getValues(aNode,
								matchConfig, configs)) {
							if (StringUtils.isBlank(matchConfig.getExtractKw())) {
								shopIds.add(value.trim());
							} else {
								final String[] kw = matchConfig.getExtractKw()
										.split(",");
								final int start = value.indexOf(kw[0]);
								if (start > 0) {
									final int stop = value
											.indexOf(kw[1], start);
									if (stop > 0) {
										shopIds.add(value.substring(
												start + kw[0].length(), stop)
												.trim());
									} else {
										shopIds.add(value.substring(start
												+ kw[0].length()));
									}
								}
							}
						}
					}
				}

			}
		} catch (final Exception e) {
			// 解析出错
			// 这里不输出html，太长了
			Logs.unpredictableLogger.error(
					"Error to parse item.htm, maybe invalide ParseRule: "
							+ bean, e);
			logStatistics(Logs.StatsResult.FAIL, bean);
		}		
		// 设置 item 名称
		if (CollectionUtils.isNotEmpty(names)) {
			item.setName(names.iterator().next());
		}
		// 没找到 item 名称，默认title
		if (StringUtils.isBlank(item.getName())) {
			item.setName(title);
		}
		if (CollectionUtils.isNotEmpty(userIds)) {
			item.setUserId(userIds.iterator().next());
		}
		if (CollectionUtils.isNotEmpty(shopIds)) {
			item.setShopId(shopIds.iterator().next());
		}
		if (StringUtils.isBlank(item.getName())
			|| StringUtils.isBlank(item.getUserId())
			|| StringUtils.isBlank(item.getShopId())) {
			LOG.warn("lack some important propery, retry later>>\n{}",
				JSON.toJSONString(item, true));			 
			logStatistics(Logs.StatsResult.FAIL, bean);
			return null;
		}		
		// 设置金额
		if (CollectionUtils.isNotEmpty(prices)) {
			Double price = null;
			for (final String e : prices) {
				try {
					final Double d = Double.valueOf(e);
					if (price == null || d > price) {
						price = d;
					}
				} catch (final Exception ignore) {
				}
			}
			item.setPrice(price);
		}		
		if (LOG.isDebugEnabled()) {
			LOG.debug("item >> {}", JSON.toJSONString(item));
			LOG.debug("names >> {}", JSON.toJSONString(names));
			LOG.debug("groupImgs >> {}", JSON.toJSONString(groupImgs));			 
			LOG.debug("userIds >> {}", JSON.toJSONString(userIds));
			LOG.debug("shopIds >> {}", JSON.toJSONString(shopIds));
		}		
		if (CollectionUtils.isNotEmpty(soldOuts)) {
			LOG.warn("item sold out:{}", bean);
			// 下架商品，无需解析 sku
			item.setStatus(Statics.SOLD_OUT);	
			// FIXME why? and who?
//			item.setCompleted(true);
			return new ItemWrapper(item, null, groupImgs, skuImgs,null,null,null,null);
		}	
		
		// 提取详情
		final String remoteDescUrl = ItemParseHelper.getApiDescUrl(bean, result.getContentString());
			
		// 没有 sku
		if (skuTypes.isEmpty() || skuValues.isEmpty()) {		
			// FIXME why? and who?
//			item.setCompleted(true);
			return new ItemWrapper(item, remoteDescUrl, groupImgs, skuImgs,null,null,null,null);
		}        
		// 格式化sku信息
		final List<Sku> skus = ItemParseHelper.createSkuList(skuTypes, skuValues, skuTexts);		 
		// 根据 item.html 设置默认 sku 信息 
		Map<String, Sku> skuStocks=null;
		Map<String, Sku> skuPrices=null;		
		if(bean.getShopType()==ShopType.TAOBAO){
			ItemParseHelper. parseTaoBaoSku(bean, result.getContentString(), skus);
		}
		else if(bean.getShopType()==ShopType.TMALL){
		   //parseTmallSku(result.getContentString(), skus);
			skuStocks=ItemParseHelper.parseTmallSkuStock(bean, result.getContentString(), skus);
			skuPrices=ItemParseHelper.parseTmallSkuPrice(bean, result.getContentString(), skus);
		}
		else{
		   logStatistics(Logs.StatsResult.FAIL, bean);
		   return null;			
		}		
		// 得到 远程获取sku库存、价格的 url地址////////////////////////
		final String remoteUrl = ItemParseHelper.getSkuRemoteUrl(bean, result.getContentString());		
		final ItemWrapper wrapper = new ItemWrapper(item,remoteDescUrl,groupImgs,skuImgs,remoteUrl,skus,skuStocks,skuPrices);
		wrapper.setSkuReferUrl(this.invoker.getUrl());
		wrapper.setSkuReferIp(this.invoker.getIp());
		wrapper.setSkuUserAgent(this.invoker.getUserAgent());
		return wrapper;
	}

	@Override
	protected void persistent(ItemWrapper entity) {
		final Item item = entity.getItem();
		final ItemEvent event = new ItemEvent(ItemEventType.SAVE_ITEM);
		event.setItem(item);
		bsm.signal(event);
		
		if (!(ItemStatus.SUCCESS.ordinal() == item.getCompleted())) {
			// init ItemProcess object
			final ItemProcess itemProcess = new ItemProcess();
			itemProcess.setShopId(getBean().getShopId());
			itemProcess.setItemId(item.getId());
			itemProcess.setGroupImgCount(entity.getGroupImgs().size());
			itemProcess.setSkuImgCount(entity.getSkuImgs().size());
			itemProcess.setOption(getBean().getOption());
			final ItemProcessEvent ipe = new ItemProcessEvent(ItemProcessEventType.INIT);
			ipe.setInitData(itemProcess);
			bsm.signal(ipe);
		}
	}

	@Override
	protected void furtherCrawl(ItemWrapper entity) {
		final Item item = entity.getItem();
		if (item.getStatus() != null && Statics.SOLD_OUT == item.getStatus()) {
			return;
		}
		
		final List<Img> groupImgs = ImgFactory.createGroupImgs(entity.getGroupImgs());
		final ItemTaskBean bean = (ItemTaskBean) getBean();		
		ImgTaskBean imgTask;
		for (final Img img : groupImgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, item.getId(), img);
			bsm.signal(imgTask);
		}
		
		final List<Img> skuImgs = ImgFactory.createSkuImgs(entity.getSkuImgs(), groupImgs.size());
		for (final Img img : skuImgs) {
			imgTask = TaskBeanFactory.createImgTaskBean(bean, item.getId(), img);
			bsm.signal(imgTask);
		}
		
		if (entity.getApiDescUrl() != null) {
			final DescTaskBean task = TaskBeanFactory.createDescTaskBean((ItemTaskBean) getBean(), 
					item.getId(), entity.getApiDescUrl(), groupImgs.size() + skuImgs.size());
			bsm.signal(task);
		}
		
		if (entity.getSkuUrl()!=null && entity.getSkus()!=null) {
			final SkuTaskBean task = TaskBeanFactory.createSkuTaskBean((ItemTaskBean) getBean(),
					item.getId(), entity.getSkuUrl(), entity.getSkus(), entity.getSkuStocks(),entity.getSkuPrices(),
					entity.getSkuReferUrl(), entity.getSkuReferIp(), entity.getSkuUserAgent());
			bsm.signal(task);
		}
	}
	 	 
}
