/**
 * 
 */
package com.vdlm.spider.store;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.vdlm.dal.model.Fragment;
import com.vdlm.dal.model.FragmentImage;
import com.vdlm.dal.model.Product;
import com.vdlm.dal.model.ProductFragment;
import com.vdlm.dal.model.ProductImage;
import com.vdlm.dal.status.ProductStatus;
import com.vdlm.dal.type.ProductSource;
import com.vdlm.dal.vo.FragmentVO;
import com.vdlm.service.fragment.FragmentImageService;
import com.vdlm.service.fragment.FragmentService;
import com.vdlm.service.fragment.ProductFragmentService;
import com.vdlm.service.product.ProductService;
import com.vdlm.spider.Config;
import com.vdlm.spider.SpideItemType;
import com.vdlm.spider.Statics;
import com.vdlm.spider.core.IntRef;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.entity.SkuProp;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:24:24 PM Aug 7, 2014
 */
@Component
public class SyncResultToKkkdService implements SyncResultService {

	@Autowired
	private ItemStore itemService;
	@Autowired
	private ProductService productService;
	@Autowired
	private FragmentService fragmentService;
	@Autowired
	private FragmentImageService fragmentImageService;
	@Autowired
	private ProductFragmentService productFragmentService;
	
	@Value("${spider.update.switch}")
	private int updateSwitch;
	
	private List<ProductImage> genDescImg(Product product, List<Img> imgs) {
		final List<ProductImage> retList = new ArrayList<ProductImage>();
		for (final Img img : imgs) {
			if (StringUtils.isNotBlank(img.getImg())) { // 详情图
				final ProductImage proImg = new ProductImage();
				if (img.getType() !=3) {
					continue;
				}
				proImg.setImg(img.getImg());
				proImg.setType(img.getType());
				proImg.setImgOrder(img.getOrderNum());
				retList.add(proImg);
			}
		}
		return retList;
	}
	
	private List<ProductImage> genGroupImg(Product product, List<Img> imgs) {
		final List<ProductImage> imgList = new ArrayList<ProductImage>(imgs.size() + 1);
		String dafaultImg = Config.instance().getDefaultImg();
		int minOrder = 99;
		for (final Img img : imgs) {
			if (StringUtils.isNotBlank(img.getImg()) && (img!= null && img.getType().equals(1))) { // 先过滤sku图
				final ProductImage proImg = new ProductImage();
                if (img.getType() == 1 && img.getOrderNum() < minOrder) {
					dafaultImg = img.getImg();
					minOrder = img.getOrderNum();
				}
				// 只保存商品主图、sku图,详情图在片段中处理
				proImg.setImg(img.getImg());
				proImg.setType(img.getType());
				proImg.setImgOrder(img.getOrderNum());
				imgList.add(proImg);
			}
		}
		// 如果没有图片，则默认一张
		final ProductImage proImg = new ProductImage();
		if (imgList.isEmpty()) {
			proImg.setImg(dafaultImg);
			proImg.setType(1); // 默认组图
			proImg.setImgOrder(0); // 默认排序
			imgList.add(proImg);
		}
		product.setImg(dafaultImg);
		return imgList;
	}
	
	private List<com.vdlm.dal.model.Sku> genProductSku(Item item, List<Sku> skus,
											List<com.vdlm.dal.model.SkuMapping> SkusMappingList) {
		final List<com.vdlm.dal.model.Sku> n_skus = new ArrayList<com.vdlm.dal.model.Sku>();
		
		if (item.getSkuProps() != null && item.getSkuProps().getSkuProps() != null) {
			final List<SkuProp> skuProps = item.getSkuProps().getSkuProps();
			final List<List<String>> skuTexts = Lists.newArrayList();
			final List<String> skuTypes = Lists.newArrayList();
			for (final SkuProp skuProp : skuProps) {
				skuTypes.add(skuProp.getPropName());
				skuTexts.add(skuProp.getTexts());
			}
			item.setSkuTexts(skuTexts);
			item.setSkuTypes(skuTypes);
		}

		// 该skusList保存二纬sku
		List<com.vdlm.dal.model.Sku> skusList = new ArrayList<com.vdlm.dal.model.Sku>();
		if (item.getSkuTexts() != null && item.getSkuTexts().size() != 0) {
			//skusList = createSkuList2(skus, item.getSkuTexts(), item.getSkuTypes());
			skusList = genSkuMultiDimension(skus, item.getSkuTypes());
		} else {
			skusList = createSkuList3(skus);
		}
		if (item.getSkuTypes() != null) {
			int p = 1;
			for (final String sType : item.getSkuTypes()) {
//				if (p == 3) {
//					break;
//				}
				final com.vdlm.dal.model.SkuMapping result = new com.vdlm.dal.model.SkuMapping();
				result.setSpecKey("spec" + p);
				result.setSpecName(sType);
				SkusMappingList.add(result);
				++p;
			}
		}
		
		// 同步快店抛弃库存为0的sku 2015-03-19
		for(final com.vdlm.dal.model.Sku sku : skusList) {
			if(sku.getAmount() != 0) {
				n_skus.add(sku);
			}
		}
		
		return n_skus;
	}
	
	
	private void handleProudctDesc(Product product, Item item, List<Img> imgs) throws Exception {
		int cnt = 0;
		// 详情片段
		final List<FragmentVO> fragments = item.getFragments();
		try {
			// 详情片段
			for (int i = 0; i < fragments.size(); i++) {
				final Fragment fragment = new Fragment();
				fragment.setName(fragments.get(i).getName());
				fragment.setDescription(fragments.get(i).getDescription());
				fragment.setShowModel(fragments.get(i).getShowModel());
				fragment.setShopId(product.getShopId());
				
				cnt = this.fragmentService.insert(fragment);
				Logs.statistics.info( "Start to create fragment, cnt={}, itemId = {}",cnt, item.getItemId());

				final FragmentVO result = new FragmentVO();
				BeanUtils.copyProperties(fragment, result);

				// 详情片段图片
				if (fragments.get(i).getImgs() != null) {
					for (int j = 0; j < fragments.get(i).getImgs().size(); j++) {
						final FragmentImage fragmentImg = new FragmentImage();
						
						for(final Img img : imgs) {
							if(img.getType() != null &&
								img.getType().equals(3) &&
								img.getImgUrl().equals(fragments.get(i).getImgs().get(j) .getImgUrl())) {
								fragmentImg.setImg(img.getImg());
								fragmentImg.setIdx(fragments.get(i).getImgs().get(j) .getIdx());
								fragmentImg.setFragmentId(fragment.getId());
								cnt = this.fragmentImageService.insert(fragmentImg);
								Logs.statistics.info( "Start to create fragmentImages, cnt = {}, itemId = {}", cnt, item.getItemId());
							}
						}
					}
				}

				// 商品片段
				final ProductFragment productFragment = new ProductFragment();
				productFragment.setProductId(product.getId());
				productFragment.setFragmentId(fragment.getId());
				cnt = productFragmentService.insert(productFragment);
				Logs.statistics.info( "Start to create productFragment, cnt = {}, productId = {}", cnt, product.getId());
			}
			this.productService.updateSpecImg(product, genDescImg(product, imgs));
		} catch (final Exception e) {
			Logs.unpredictableLogger.error( "Error to create Fragment, itemId = {}", item.getItemId());
			throw e;
		}
	}
	
	private List<Img> getSpecImgs(List<Img> imgs, Integer type) {
		final List<Img> retList = new ArrayList<Img>();
		if (imgs == null || imgs.size() == 0 || type == null) {
			return retList;
		}
		for (final Img p : imgs) {
			if (p.getType() == null) {
				continue;
			}
			if (p.getType().equals(type)) {
				retList.add(p);
			}
		}
		return retList;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncItem(Item item, List<Sku> skus, List<Img> imgs, SpideItemType type, Integer option) throws Exception {
		
		// 1: 整体商品状态
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			// 下架商品，同步在快店下架，只更新状态
			Logs.unpredictableLogger.info("Start to sync Product to KKKD, Status:SOLD_OUT, ItemId:{}", item.getItemId());
			this.productService.instockEX(item.getOuerShopId(), item.getItemId());
			return;
		}
		if(item.getStatus() == Statics.INCOMPLETED_INFO) {
			// 信息不全商品， 跳出同步快店
			Logs.unpredictableLogger.info("Refu to sync Product to KKKD, Status:INCOMPLETED_INFO, ItemId:{}", item.getItemId());
			return;
		}
		final Product product = createProduct(item);
        Logs.statistics.info("start syncItem with: option:[{}]. type:[{}], item:[{}], skus[{}], imgs[{}]", 
        		option,
                type.toString(),
                JSON.toJSONString(item), 
                JSON.toJSON(skus), 
                JSON.toJSON(imgs));
		
		// 2: 处理主图
		List<ProductImage> imgList = null;
		if (SpideItemType.GROUP_IMG.equals(type) || SpideItemType.ITEM.equals(type) || SpideItemType.MITEM.equals(type)) {
			imgList = genGroupImg(product, imgs);
		}

		// 3: 该skuList保存一纬组合sku
		final List<com.vdlm.dal.model.Sku> skuList = createSkuList(skus);
		final List<com.vdlm.dal.model.SkuMapping> skusMappingList = new ArrayList<com.vdlm.dal.model.SkuMapping>();
		List<com.vdlm.dal.model.Sku> n_skus = null;
		if (SpideItemType.SKUS.equals(type) || SpideItemType.ITEM.equals(type) || SpideItemType.MITEM.equals(type)) {
			n_skus = genProductSku(item, skus, skusMappingList);
		} 
		
		// 4: write
		int ret = 0;
		try {
			Logs.statistics.info(
					"Exec update to KKKD, Product[{}],skus[{}],Imgs[{}], skuMapping[{}], sync_op[{}]",
					JSON.toJSONString(product), JSON.toJSONString(n_skus), 
					JSON.toJSONString(imgList), JSON.toJSONString(skusMappingList), option);
			if (SpideItemType.SKUS.equals(type)) {
				ret = this.productService.updateEx(product, n_skus, null, null, skusMappingList, option);
			} else if (SpideItemType.GROUP_IMG.equals(type)) {
				ret = this.productService.updateEx(product, null, null, imgList, null, option);
			} else if (SpideItemType.DESC.equals(type)) {
				ret = this.productService.updateEx(product); // TODO:
			} else if (SpideItemType.ITEM.equals(type) || SpideItemType.MITEM.equals(type)) {
				ret = this.productService.updateEx(product, n_skus, null, imgList, skusMappingList, option);
			}
			if(ret > 0) {
				Logs.statistics.info("Success to sync update product:" + JSON.toJSONString(product));
			}
		} catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to sync update Product:" + JSON.toJSONString(product), e);
			throw e;
		}
		
		// 5: 处理详情  不管是新增还是更新 详情都需要走进去, 但是一次选择如果是新增,已有商品详情不能被更新
		if ( (SpideItemType.DESC.equals(type) || SpideItemType.ITEM.equals(type) || SpideItemType.MITEM.equals(type)) 
				&& updateSwitch==1 && ret != -1) {  // ret = -1说明这个商品已存在并且不需要被更新
			try {
				// 先删除
				final List<ProductFragment> productFragments = productFragmentService.selectByProductId(product.getId());
				productFragmentService.deleteByProductId(product.getId());
				final List<String> fragmentIds = new ArrayList<String>();
				for(final ProductFragment productFragment : productFragments) {
					fragmentIds.add(productFragment.getFragmentId());
				}
				fragmentImageService.batchDeleteByFragmentIds(fragmentIds);
				fragmentService.batchDeleteByFragmentIds(fragmentIds);
			} catch (final Exception e) {
				Logs.unpredictableLogger.error("Error to delete fragment:" + JSON.toJSONString(product), e);
				throw e;
			}
			handleProudctDesc(product, item, getSpecImgs(imgs, 3));
		} // desc

		// 埋点日志
		Logs.monitorLogger.info(
				"success to create KKKD  spideType:[{}] Product[{}],Skus[{}],Imgs[{}]",
				type.toString(),
				JSON.toJSONString(product), JSON.toJSONString(skuList),
				JSON.toJSONString(imgList));
	}

	/**
	 * 返回一纬度组合sku
	 */
	List<com.vdlm.dal.model.Sku> createSkuList(Collection<Sku> skus) {
		final List<com.vdlm.dal.model.Sku> results = new ArrayList<com.vdlm.dal.model.Sku>(
				skus.size());
		int index = 0;
		for (final Sku sku : skus) {
			final com.vdlm.dal.model.Sku result = new com.vdlm.dal.model.Sku();
			result.setSpec(sku.getSpec());
			result.setOrder(++index);
			result.setAmount(sku.getAmount());
			result.setPrice(BigDecimal.valueOf(sku.getPrice()));

			results.add(result);
		}

		return results;
	}
	
	private void matchSpecTypes(com.vdlm.dal.model.Sku sku, String[] specs,  List<String>types) {
		if (sku == null || specs == null || types == null)  return;
		
		for (String spec : specs) {
			String[] pair = spec.split(":");
			if (pair.length == 0) continue;
			if (pair.length == 1) { // 无 or 220*240被套 ...
				sku.setSpec(spec);  
				sku.setSpec1(spec);
			} else if (pair.length == 2) { // 颜色分类:220*240被套;适用床尺寸:2.0m（6.6英尺）床
				int idx  = 1;
				for (String type : types) {
					if (type.trim().equals(pair[0].trim())) {  // match key   pair[0]为key(颜色分类), pair[1]为value(220*240被套)
						if (idx == 1) {
							sku.setSpec1(pair[1]); break;
						} else if (idx == 2) {
							sku.setSpec2(pair[1]); break;
						} else if (idx == 3) {
							sku.setSpec3(pair[1]); break;
						} else if (idx == 4) {
							sku.setSpec4(pair[1]); break;
						} else if (idx == 5) {
							sku.setSpec5(pair[1]);  break;
						} else  // OMG
							;
					}
					idx++;
				}
			} else { } // error
		}
	}
	
	private List<com.vdlm.dal.model.Sku> genSkuMultiDimension(List<Sku> skus, List<String> skuTypes) {
		List<com.vdlm.dal.model.Sku> results =  new ArrayList<com.vdlm.dal.model.Sku>();
		if (skus == null || skus.size() == 0 || skuTypes == null) return results;
		Map<Sku, String[]> skuMap = new HashedMap<Sku, String[]>();
		for (Sku sku : skus) {
			if (StringUtils.isEmpty(sku.getSpec())) continue;
			// 此型号有几个维度
			String[] skuDimes = sku.getSpec().split(";");   // eg:  颜色分类:220*240被套;适用床尺寸:2.0m（6.6英尺）床
			if (skuDimes.length == 0) continue;
			skuMap.put(sku, skuDimes);
		}
		
		Set<Map.Entry<Sku, String[]>> set = skuMap.entrySet();
        for (Iterator<Map.Entry<Sku, String[]>> it = set.iterator(); it.hasNext();) {
            Map.Entry<Sku, String[]> entry = (Map.Entry<Sku, String[]>) it.next();
            int index = 0;
            Sku key = entry.getKey();
            String[] values = entry.getValue(); 
            com.vdlm.dal.model.Sku result =  new com.vdlm.dal.model.Sku();
			result.setOrder(++index);
           	matchSpecTypes(result, values, skuTypes);  // fill  sku spec/spec1/... 因为skyType是各维度名,并按顺序写进skuMapping里
           	//result.setSpec(key.getSpec());    // spec记录完整信息 ( 颜色分类:220*240被套;适用床尺寸:2.0m（6.6英尺）床 ) key:value ; key:value
			result.setAmount(key.getAmount());
			result.setPrice(BigDecimal.valueOf(key.getPrice()));
			result.setThirdSkuId(key.getSkuId());
            results.add(result);
        }
		
        return results;
	}

	/**
	 * 返回仅支持二纬sku
	 */
	List<com.vdlm.dal.model.Sku> createSkuList2(List<Sku> sku,
			List<List<String>> skuTexts, List<String> skuTypes) {
		List<com.vdlm.dal.model.Sku> results = null;
		try {
			int size = 1;
			for (final List<String> skuTextList : skuTexts) {
				size *= skuTextList.size();
			}
			size = size < sku.size() ? size : sku.size();
			final List<IntRef> indexs = new ArrayList<IntRef>(skuTypes.size());
			for (int i = 0; i < skuTypes.size(); i++) {
				indexs.add(new IntRef());
			}
			results = new ArrayList<com.vdlm.dal.model.Sku>(size);
			for (int i = 0; i < size; i++) {
				final com.vdlm.dal.model.Sku result = new com.vdlm.dal.model.Sku();
				final StringBuilder spec = new StringBuilder();
				for (int a = 0; a < skuTypes.size(); a++) {
					final int index = indexs.get(a).get();
					spec.append(';').append(skuTexts.get(a).get(index));
				}
				spec.deleteCharAt(0).toString();
				if (spec.length() > 0) {
					final String[] specArray = spec.toString().split(";");
					if (specArray.length > 2) {
						result.setSpec1(specArray[0]);
						for (int j = 0; j < specArray.length; j++) {
							if (j > 0) {
								result.setSpec2(specArray[j] + ";");
							}
						}
					} else {
						if (specArray.length < 2) {
							result.setSpec1(specArray[0]);
							result.setSpec2(null);
						} else {
							result.setSpec1(specArray[0]);
							result.setSpec2(specArray[1]);
						}
					}
				}
				result.setOrder(i);
				result.setAmount(sku.get(i).getAmount());
				result.setPrice(BigDecimal.valueOf(sku.get(i).getPrice()));

				results.add(result);
				for (int a = skuTypes.size() - 1; a >= 0; a--) {
					final int index = indexs.get(a).incrementAndGet();
					if (index >= skuTexts.get(a).size()) {
						indexs.get(a).set(0);
					} else {
						break;
					}
				}
			}

		} catch (final Exception e) {
			Logs.unpredictableLogger.error("Error to parse skuList. sku = {}",
					JSON.toJSON(sku), e);
		}
		return results;
	}

	List<com.vdlm.dal.model.Sku> createSkuList3(Collection<Sku> skus) {
		final List<com.vdlm.dal.model.Sku> results = new ArrayList<com.vdlm.dal.model.Sku>(
				skus.size());
		int index = 0;
		for (final Sku sku : skus) {
			final com.vdlm.dal.model.Sku result = new com.vdlm.dal.model.Sku();
			result.setSpec("");
			result.setSpec1(sku.getSpec());
			result.setOrder(++index);
			result.setAmount(sku.getAmount());
			result.setPrice(BigDecimal.valueOf(sku.getPrice()));

			results.add(result);
		}

		return results;
	}

	Product createProduct(Item item) {
		final Product result = new Product();

		switch (item.getStatus()) {
		case Statics.NORMAL:
			result.setStatus(ProductStatus.ONSALE);
			break;
		default:
			result.setStatus(ProductStatus.DRAFT);
		}

		result.setName(item.getName());
		result.setDescription(item.getDetails());
		result.setUserId(item.getOuerUserId());
		result.setShopId(item.getOuerShopId());
		result.setAmount(item.getAmount());
		result.setPrice(BigDecimal.valueOf(item.getPrice()));
		result.setThirdItemId(BigInteger.valueOf(Long.parseLong(item.getItemId())));
		result.setSource(ProductSource.NEWSPIDER);

		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncShop(Shop shop) throws Exception {
		// ignore
	}

	public static void main(String[] args) {
		final String str = "a,b,c==d,e,f==x,y,z";
		final List<String> result = descartes(str);
		System.out.println(result);
	}

	@SuppressWarnings("rawtypes")
	public static List<String> descartes(String str) {
		final String[] list = str.split("==");
		final List<List> strs = new ArrayList<List>();
		for (int i = 0; i < list.length; i++) {
			strs.add(Arrays.asList(list[i].split(",")));
		}
		System.out.println(strs);
		int total = 1;
		for (int i = 0; i < strs.size(); i++) {
			total *= strs.get(i).size();
		}
		final String[] mysesult = new String[total];
		int now = 1;
		// 每个元素每次循环打印个数
		int itemLoopNum = 1;
		// 每个元素循环的总次数
		int loopPerItem = 1;
		for (int i = 0; i < strs.size(); i++) {
			final List temp = strs.get(i);
			now = now * temp.size();
			// 目标数组的索引值
			int index = 0;
			final int currentSize = temp.size();
			itemLoopNum = total / now;
			loopPerItem = total / (itemLoopNum * currentSize);
			int myindex = 0;
			for (int j = 0; j < temp.size(); j++) {

				// 每个元素循环的总次数
				for (int k = 0; k < loopPerItem; k++) {
					if (myindex == temp.size()) {
						myindex = 0;
					}
					// 每个元素每次循环打印个数
					for (int m = 0; m < itemLoopNum; m++) {
						mysesult[index] = (mysesult[index] == null ? ""
								: mysesult[index] + ",")
								+ ((String) temp.get(myindex));
						index++;
					}
					myindex++;

				}
			}
		}
		return Arrays.asList(mysesult);
	}

}
