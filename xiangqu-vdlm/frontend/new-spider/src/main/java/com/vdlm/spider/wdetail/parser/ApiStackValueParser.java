package com.vdlm.spider.wdetail.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.vdlm.spider.entity.Item.SkuProps;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.entity.SkuProp;
import com.vdlm.spider.entity.SkuProp.Value;
import com.vdlm.spider.wdetail.ItemResponse;
import com.vdlm.spider.wdetail.Prop;

/**
 *
 * @author: chenxi
 */

public abstract class ApiStackValueParser {

	public static WItem parseItem(ItemResponse resp) {
		if (!resp.isSuccess()) {
			return null;
		}
		final WItem item = new WItem();
		item.setName(resp.getData().getItemInfoModel().getTitle());
		item.setShopId(resp.getData().getSeller().getShopId());
		item.setUserId(resp.getData().getSeller().getUserNumId());
		
		final JSONObject json = JSON.parseObject(resp.getData().getApiStack().get(0).getValue());
		final JSONObject data = json.getJSONObject("data");
		final JSONObject itemInfoModel = data.getJSONObject("itemInfoModel");
		final JSONArray priceUnits = itemInfoModel.getJSONArray("priceUnits");
		final List<Prop> itemProps = resp.getData().getProps();
		
		Double minPrice = null;
		Integer amount = 0;
		List<Sku> skus = null;
		
		if (itemInfoModel.containsKey("quantity")) {
			amount = itemInfoModel.getInteger("quantity");
		}
		
		JSONObject priceUnit;
//		Double rangePrice = null;
		for (int i = 0; i < priceUnits.size(); i++) {
			priceUnit = priceUnits.getJSONObject(i);
			// FIXME fix price not consistent problem, if we encounter "rangePrice" key, ignore it for now
			if (priceUnit.containsKey("rangePrice")) {
//				try {
//					rangePrice = priceUnit.getDouble("price");
//				} catch (final Throwable t) {
//					rangePrice = null;
//				}
				continue;
			}
			if (priceUnit.containsKey("price")) {
				try {
					if (minPrice == null) {
						minPrice = priceUnit.getDouble("price");
					} else {
						minPrice = Math.min(minPrice, priceUnit.getDouble("price"));
					}
				} catch (final Throwable t) {
					minPrice = null;
				}
			}
		}
		
		String props = "";
		if (itemProps != null && itemProps.size() != 0) {
			for (Prop p : itemProps) {
				int i = 0;
				props += p.getName() + " : " + p.getValue() + "\n";
				//if (i % 2 == 0) props += "\n";
				i++;
			}
		}
		item.setProps(props);
		
		item.setAmount(amount);
		item.setPrice(minPrice);
		final Set<String> groupImgs = resp.getData().getItemInfoModel().getPicsPath();
		item.setGroupImgs(groupImgs);

		if (data.containsKey("skuModel") && data.getJSONObject("skuModel").getJSONObject("skus") != null) {
			final List<SkuProp> skuProps = resp.getData().getSkuModel().getSkuProps();
			final JSONObject ppathIdmap = resp.getData().getSkuModel().getPpathIdmap();
			skus = parseSkus(data.getJSONObject("skuModel").getJSONObject("skus"), skuProps, ppathIdmap);
			item.setSkus(skus);
            if (skuProps != null) {
				parseSkuTypeValueTexts(item, skuProps);
				item.setSkuImgs(resp.getData().getSkuModel().getSkuImgs());
				item.setSkuProps(simplifySkuProps(skuProps));
            }
		} else {
			final Sku sku = new Sku();
			sku.setAmount(amount);
			sku.setPrice(minPrice);
			sku.setSpec("无");
			sku.setOrigSpec("");
			item.setDefaultSku(sku);
		}
		
//		item.setApiDescUrl(resp.getData().getDescInfo().getFullDescUrl());
		item.setApiDescUrl(resp.getData().getDescInfo().getBriefDescUrl());
		return item;
	}
	
	/* 去掉values中的imgUrl */
	private static SkuProps simplifySkuProps(List<SkuProp> skuProps) {
		final SkuProps ret = new SkuProps();
		ret.setSkuProps(new ArrayList<SkuProp>());
		if (skuProps == null) {
			return ret;
		}
		for (final SkuProp sp : skuProps) {
			if (sp.getValues() == null) {
				continue;
			}
			for (final SkuProp.Value value : sp.getValues()) {
				if (value.getImgUrl() != null) {
					value.setImgUrl(null);
				}
			}
		}
		ret.setSkuProps(skuProps);
		return ret;
	}
	
	private static void parseSkuTypeValueTexts(WItem item, List<SkuProp> skuProps) {
		final List<String> skuTypes = Lists.newArrayList();
		final List<List<String>> skuValues = Lists.newArrayList();
		final List<List<String>> skuTexts = Lists.newArrayList();
		List<String> values;
		List<String> texts;
		for (final SkuProp skuProp : skuProps) {
			skuTypes.add(skuProp.getPropName());
			values = Lists.newArrayList();
			texts = Lists.newArrayList();
			for (final Value value : skuProp.getValues()) {
				values.add(skuProp.getPropId() + ":" + value.getValueId());
				texts.add(skuProp.getPropName() + ":" + value.getName());
			}
			skuValues.add(values);
			skuTexts.add(texts);
		}
		item.setSkuTypes(skuTypes);
		item.setSkuValues(skuValues);
		item.setSkuTexts(skuTexts);
	}
	
	private static List<Sku> parseSkus(JSONObject json, List<SkuProp> skuProps, JSONObject ppathIdmap) {
		final List<Sku> skus = Lists.newArrayList();
        if (json == null || skuProps == null)  return skus;
		final Iterator<String> it = json.keySet().iterator();
		String skuId;
		JSONObject skuJson;
		Sku sku;
		while (it.hasNext()) {
			skuId = it.next();
			skuJson = json.getJSONObject(skuId);
			sku = parseReallySku(skuId, skuJson, skuProps, ppathIdmap);
			if (sku.getAmount() == null || sku.getAmount() == 0) {
				continue;
			}
			if (sku.getPrice() == null || sku.getPrice() == 0) {
				continue;
			}
			skus.add(sku);
		}
		return skus;
	}
	
	private static Sku parseReallySku(String skuId, JSONObject json, List<SkuProp> skuProps, JSONObject ppathIdmap) {
		final Sku sku = new Sku();
		sku.setSkuId(skuId);
		sku.setAmount(json.getInteger("quantity"));
		sku.setPrice(json.getJSONArray("priceUnits").getJSONObject(0).getDouble("price"));
		final PpathIdmap map = parsePpathIdmap(skuId, ppathIdmap);
		if (map != null) {
			sku.setOrigSpec(map.getOrigSpec());
			sku.setSpec(parseSepcAndSetSkuImg(map, skuProps, sku));
		}
		return sku;
	}
	
	private static PpathIdmap parsePpathIdmap(String skuId, JSONObject json) {
		if (!json.containsValue(skuId)) {
			return null;
		}
		
		final PpathIdmap map = new PpathIdmap();
		final Iterator<String> it = json.keySet().iterator();
		String key;
		while (it.hasNext()) {
			key = it.next();
			if (json.getString(key).equals(skuId)) {
				map.setOrigSpec(key);
				map.setSkuId(skuId);
				return map;
			}
		}
		
		return null;
	}
	
	private static String parseSepcAndSetSkuImg(PpathIdmap ppathIdmap, List<SkuProp> skuProps, Sku sku) {
		final String[] origSpecs = StringUtils.split(ppathIdmap.getOrigSpec(), ";");
		String propId;
		String valueId;
		String origSpec;
		final StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < origSpecs.length; i++) {
			origSpec = origSpecs[i];
			propId = StringUtils.split(origSpec, ":")[0];
			valueId = StringUtils.split(origSpec, ":")[1];
			sb.append(parseSepc(skuProps, propId, valueId, sku));
			if (i < origSpecs.length - 1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}
	
	private static String parseSepc(List<SkuProp> skuProps, String propId, String valueId, Sku sku) {
		final StringBuilder sb = new StringBuilder("");
		for (final SkuProp skuProp : skuProps) {
			if (propId.equals(skuProp.getPropId())) {
				sb.append(skuProp.getPropName()).append(":");
				for (final Value value : skuProp.getValues()) {
					if (valueId.equals(value.getValueId())) {
						sb.append(value.getName());
						if (value.getImgUrl() != null) {
							sku.setImgUrl(value.getImgUrl());
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
//	private String parseSepcAndSetSkuImg(PpathIdmap ppathIdmap, List<SkuProp> skuProps, Sku sku) {
//		SkuProp size = null;
//		SkuProp color = null;
//		for (final SkuProp skuProp : skuProps) {
//			if (ppathIdmap.getPropSize().equals(skuProp.getPropId())) {
//				size = skuProp;
//			}
//			else if (ppathIdmap.getPropColor().equals(skuProp.getPropId())) {
//				color = skuProp;
//			}
//		}
//		
//		final StringBuilder sb = new StringBuilder("");
//		if (size != null) {
//			final List<Value> values = size.getValues();
//			for (final Value value : values) {
//				if (value.getValueId().equals(ppathIdmap.getValueSize())) {
//					sb.append(size.getPropName())
//					  .append(":")
//					  .append(value.getName());
//					break;
//				}
//			}
//		}
//		sb.append(";");
//		if (color != null) {
//			final List<Value> values = color.getValues();
//			for (final Value value : values) {
//				if (value.getValueId().equals(ppathIdmap.getValueColor())) {
//					sb.append(color.getPropName())
//					  .append(":")
//					  .append(value.getName());
//					// FIXME -- not test yet
////					sku.setImgUrl(value.getImgUrl());
//					break;
//				}
//			}
//		}
//		return sb.toString();
//	}
}
