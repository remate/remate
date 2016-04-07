/**
 * 
 */
package com.vdlm.spider.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
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
import com.vdlm.spider.Statics;
import com.vdlm.spider.core.IntRef;
import com.vdlm.spider.entity.Img;
import com.vdlm.spider.entity.Item;
import com.vdlm.spider.entity.Shop;
import com.vdlm.spider.entity.Sku;
import com.vdlm.spider.service.ItemService;
import com.vdlm.spider.service.SyncResultService;
import com.vdlm.spider.utils.Logs;

/**
 * @author Wayne.Wang<5waynewang@gmail.com>
 * @since 5:24:24 PM Aug 7, 2014
 */
@Component
public class SyncResultToKkkdService implements SyncResultService {

	@Autowired
	private ItemService itemService;
	@Autowired
	private ProductService productService;
	@Autowired
	private FragmentService fragmentService;
	@Autowired
	private FragmentImageService fragmentImageService;
	@Autowired
	private ProductFragmentService productFragmentService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncItem(Item item, List<Sku> skus, List<Img> imgs)
			throws Exception {
		Boolean update = false;
		if (item.getId() != null) {
			update = true;
		}
		if (item.getStatus() != null && item.getStatus() == Statics.SOLD_OUT) {
			// 下架商品，同步在快店下架，只更新状态
			Logs.unpredictableLogger.info(
					"Start to sync Product to KKKD, Status:SOLD_OUT, ItemId:{}",
					item.getItemId());
			this.productService.instockEX(item.getOuerShopId(),
					item.getItemId());
			return;
		}
		if (item.getStatus() != null && item.getStatus() == Statics.NOT_FOUND) {
			// 未找到商品，不做处理
			Logs.unpredictableLogger.info(
					"Can not sync Product to KKKD, Status:NOT_FOUND, ItemId:{}",
					item.getItemId());
			return;
		}
		if(item.getStatus() != null && item.getStatus() == Statics.INCOMPLETED_INFO) {
			// 信息不全商品， 跳出同步快店
			Logs.unpredictableLogger.info(
					"Refu to sync Product to KKKD, Status:INCOMPLETED_INFO, ItemId:{}",
					item.getItemId());
			return;
		}

		String dafaultImg = Config.instance().getDefaultImg();
		final List<ProductImage> imgList = new ArrayList<ProductImage>(
				imgs.size() + 1);
		for (Img img : imgs) {
			if (StringUtils.isNotBlank(img.getImg())) {
				ProductImage proImg = new ProductImage();

				// product取主图第一张
				if (img.getType() == 1
						&& dafaultImg.equals(Config.instance().getDefaultImg()))
					dafaultImg = img.getImg();
				
				// 只保存商品主图、sku图,详情图在片段中处理
				proImg.setImg(img.getImg());
				proImg.setType(img.getType());
				proImg.setImgOrder(img.getOrderNum());
				imgList.add(proImg);
			}
		}
		// 如果没有图片，则默认一张
		ProductImage proImg = new ProductImage();
		if (imgList.isEmpty()) {
			proImg.setImg(dafaultImg);
			proImg.setType(1); // 默认组图
			proImg.setImgOrder(0); // 默认排序
			imgList.add(proImg);
		}

		final Product product = createProduct(item);
		product.setImg(dafaultImg);

		// 该skuList保存一纬组合sku
		final List<com.vdlm.dal.model.Sku> skuList = createSkuList(skus);

		// 该skusList保存二纬sku
		List<com.vdlm.dal.model.Sku> skusList = new ArrayList<com.vdlm.dal.model.Sku>();
		if (item.getSkuTexts() != null && item.getSkuTexts() != null) {
			skusList = createSkuList2(skus, item.getSkuTexts(),
					item.getSkuTypes());
		} else {
			skusList = createSkuList3(skus);
		}
		List<com.vdlm.dal.model.SkuMapping> SkusMappingList = new ArrayList<com.vdlm.dal.model.SkuMapping>();
		if (item.getSkuTypes() != null) {
			int p = 1;
			for (String sType : item.getSkuTypes()) {
				if (p == 3)
					break;
				com.vdlm.dal.model.SkuMapping result = new com.vdlm.dal.model.SkuMapping();
				result.setSpecKey("spec" + p);
				result.setSpecName(sType);
				SkusMappingList.add(result);
				++p;
			}
		}
		
		// 同步快店抛弃库存为0的sku 2015-03-19
		List<com.vdlm.dal.model.Sku> n_skus = new ArrayList<com.vdlm.dal.model.Sku>();
		for(com.vdlm.dal.model.Sku sku : skusList) {
			if(sku.getAmount() != 0) {
				n_skus.add(sku);
			}
		}

		// final List<ProductImage> productImageList =
		// createProductImageList(imgList);

		product.setSource(ProductSource.SPIDER);
		
		try {
			int ret = 0;
			if (update) {
				Logs.unpredictableLogger.info(
						"Exec update to KKKD, Product[{}],Skus[{}],Imgs[{}]",
						JSON.toJSONString(product), JSON.toJSONString(n_skus),
						JSON.toJSONString(imgList));
				
				// TODO 再次搬家不更新价格
				Logs.unpredictableLogger.info("do not update prices again.");
				product.setPrice(null);
				product.setMarketPrice(null);
				product.setOriginalPrice(null);
				
				ret = this.productService.updateEx(product, n_skus, null, imgList,
						SkusMappingList, 1);
			} else {
				Logs.unpredictableLogger.info(
						"Exec create to KKKD, Product[{}],Skus[{}],Imgs[{}]",
						JSON.toJSONString(product), JSON.toJSONString(n_skus),
						JSON.toJSONString(imgList));
				
				ret = this.productService.create(product, n_skus, null, imgList,
						SkusMappingList);
			}
			if(ret > 0)
				Logs.unpredictableLogger.info("Success to sync product:" + JSON.toJSONString(product));
		} catch (Exception e) {
			Logs.unpredictableLogger.error(
					"Error to sync Product:" + JSON.toJSONString(product), e);
			throw e;
		}

		Product resultProduct = new Product();
		BeanUtils.copyProperties(product, resultProduct);
		
		if(update) {
			try {
				List<ProductFragment> productFragments = productFragmentService.selectByProductId(product.getId());
				productFragmentService.deleteByProductId(product.getId());
				List<String> fragmentIds = new ArrayList<String>();
				for(ProductFragment productFragment : productFragments) {
					fragmentIds.add(productFragment.getFragmentId());
				}
				fragmentImageService.batchDeleteByFragmentIds(fragmentIds);
				fragmentService.batchDeleteByFragmentIds(fragmentIds);
			} catch (Exception e) {
				Logs.unpredictableLogger.error(
						"Error to delete fragment:" + JSON.toJSONString(product), e);
				throw e;
			}
		}

		// 详情片段
		final List<FragmentVO> fragments = item.getFragments();
		try {
			// 详情片段
			for (int i = 0; i < fragments.size(); i++) {
				Fragment fragment = new Fragment();
				fragment.setName(fragments.get(i).getName());
				fragment.setDescription(fragments.get(i).getDescription());
				fragment.setShowModel(fragments.get(i).getShowModel());
				fragment.setShopId(fragments.get(i).getShopId());
				
				Logs.unpredictableLogger.info(
						"Start to create fragment, itemId = {}",
						item.getItemId());

				this.fragmentService.insert(fragment);

				FragmentVO result = new FragmentVO();
				BeanUtils.copyProperties(fragment, result);

				// 详情片段图片
				if (fragments.get(i).getImgs() != null) {
					for (int j = 0; j < fragments.get(i).getImgs().size(); j++) {
						FragmentImage fragmentImg = new FragmentImage();
						
						for(Img img : imgs) {
							if(img.getImgUrl().equals(fragments.get(i).getImgs().get(j)
									.getImgUrl())) {
								fragmentImg.setImg(img.getImg());
								fragmentImg.setIdx(fragments.get(i).getImgs().get(j)
										.getIdx());
								fragmentImg.setFragmentId(fragment.getId());

								Logs.unpredictableLogger.info(
										"Start to create fragmentImages, itemId = {}",
										item.getItemId());

								this.fragmentImageService.insert(fragmentImg);
							}
						}
					}
				}

				// 商品片段
				ProductFragment productFragment = new ProductFragment();
				productFragment.setProductId(product.getId());
				productFragment.setFragmentId(fragment.getId());

				Logs.unpredictableLogger.info(
						"Start to create productFragment, productId = {}",
						product.getId());

				productFragmentService.insert(productFragment);
			}
		} catch (Exception e) {
			Logs.unpredictableLogger.error(
					"Error to create Fragment, itemId = {}", item.getItemId());
			throw e;
		}

		// 埋点日志
		Logs.monitorLogger.info(
				"success to create KKKD Product[{}],Skus[{}],Imgs[{}]",
				JSON.toJSONString(product), JSON.toJSONString(skuList),
				JSON.toJSONString(imgList));
	}

	// List<ProductImage> createProductImageList(Collection<String> imgs) {
	// if (CollectionUtils.isEmpty(imgs)) {
	// final ProductImage img = new ProductImage();
	// img.setImg(Config.instance().getDefaultImg());
	// return CollectionTools.asArrayList(img);
	// }
	//
	// final List<ProductImage> results = new
	// ArrayList<ProductImage>(imgs.size());
	//
	// for (String img : imgs) {
	// final ProductImage result = new ProductImage();
	//
	// result.setImg(img);
	//
	// results.add(result);
	// }
	//
	// return results;
	// }

	/**
	 * 返回一纬度组合sku
	 */
	List<com.vdlm.dal.model.Sku> createSkuList(Collection<Sku> skus) {
		final List<com.vdlm.dal.model.Sku> results = new ArrayList<com.vdlm.dal.model.Sku>(
				skus.size());
		int index = 0;
		for (Sku sku : skus) {
			final com.vdlm.dal.model.Sku result = new com.vdlm.dal.model.Sku();
			result.setSpec(sku.getSpec());
			result.setOrder(++index);
			result.setAmount(sku.getAmount());
			result.setPrice(BigDecimal.valueOf(sku.getPrice()));

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
			for (List<String> skuTextList : skuTexts) {
				size *= skuTextList.size();
			}
//			if (sku.size() != size) {
//				Logs.unpredictableLogger.error(
//						"Error to create skuList, parameters error. sku = {}",
//						JSON.toJSON(sku));
//				return null;
//			}
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
					String[] specArray = spec.toString().split(";");
					if (specArray.length > 2) {
						result.setSpec1(specArray[0]);
						for (int j = 0; j < specArray.length; j++) {
							if (j > 0)
								result.setSpec2(specArray[j] + ";");
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

		} catch (Exception e) {
			Logs.unpredictableLogger.error("Error to parse skuList. sku = {}",
					JSON.toJSON(sku));
		}
		return results;
	}

	List<com.vdlm.dal.model.Sku> createSkuList3(Collection<Sku> skus) {
		final List<com.vdlm.dal.model.Sku> results = new ArrayList<com.vdlm.dal.model.Sku>(
				skus.size());
		int index = 0;
		for (Sku sku : skus) {
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
		result.setThirdItemId(BigInteger.valueOf(Long.parseLong(item
				.getItemId())));

		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void syncShop(Shop shop) throws Exception {
		// ignore
	}

	public static void main(String[] args) {
		String str = "a,b,c==d,e,f==x,y,z";
		List<String> result = descartes(str);
		System.out.println(result);
	}

	@SuppressWarnings("rawtypes")
	public static List<String> descartes(String str) {
		String[] list = str.split("==");
		List<List> strs = new ArrayList<List>();
		for (int i = 0; i < list.length; i++) {
			strs.add(Arrays.asList(list[i].split(",")));
		}
		System.out.println(strs);
		int total = 1;
		for (int i = 0; i < strs.size(); i++) {
			total *= strs.get(i).size();
		}
		String[] mysesult = new String[total];
		int now = 1;
		// 每个元素每次循环打印个数
		int itemLoopNum = 1;
		// 每个元素循环的总次数
		int loopPerItem = 1;
		for (int i = 0; i < strs.size(); i++) {
			List temp = strs.get(i);
			now = now * temp.size();
			// 目标数组的索引值
			int index = 0;
			int currentSize = temp.size();
			itemLoopNum = total / now;
			loopPerItem = total / (itemLoopNum * currentSize);
			int myindex = 0;
			for (int j = 0; j < temp.size(); j++) {

				// 每个元素循环的总次数
				for (int k = 0; k < loopPerItem; k++) {
					if (myindex == temp.size())
						myindex = 0;
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
